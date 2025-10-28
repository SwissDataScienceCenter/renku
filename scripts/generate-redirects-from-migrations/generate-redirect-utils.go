package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"
)

type MigratedProject struct {
	v1Id      uint
	projectId string
}

type Platform string

const (
	Renku  Platform = "Renku"
	GitLab Platform = "GitLab"
)

type PlatformRedirectConfig struct {
	SourceUrl string `json:"source_url"`
	TargetUrl string `json:"target_url"`
}

type ProjectRedirectDescription struct {
	gitLabProjectPathWithNamespace string
	renkuProjectUlid               string
}
type ServerCredentials struct {
	host  string
	token string
}

var defaultHosts = map[Platform]string{
	"Renku":  "renkulab.io",
	"GitLab": "gitlab.renkulab.io",
}

type queryApiFlags struct {
	accept404 bool
}

func queryRenkuApi(renkuCredentials ServerCredentials, endpoint string, postBody io.Reader, flags queryApiFlags) []byte {
	status404IsNotError := flags.accept404
	var method string
	if postBody == nil {
		method = "GET"
	} else {
		method = "POST"
	}

	req, err := http.NewRequest(method, fmt.Sprintf("https://%s/api/data%s", renkuCredentials.host, endpoint), postBody)
	if err != nil {
		fmt.Printf("Error creating request: %v\n", err)
		return nil
	}
	req.Header.Set("Accept", "application/json")
	req.AddCookie(&http.Cookie{
		Name:  "_renku_session",
		Value: renkuCredentials.token,
	})
	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		fmt.Printf("Error fetching migrated projects: %v\n", err)
		return nil
	}
	defer resp.Body.Close()

	if resp.StatusCode == 404 && status404IsNotError {
		return nil
	}

	if resp.StatusCode != 200 && resp.StatusCode != 201 {
		fmt.Printf("Unexpected status code: %d\n", resp.StatusCode)
		return nil
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		fmt.Printf("Error reading response body: %v\n", err)
		return nil
	}
	return body
}

func CollectProjectRedirectDescriptions(gitlabCredentials ServerCredentials, migratedProjects []MigratedProject) []ProjectRedirectDescription {
	descriptions := make([]ProjectRedirectDescription, 0, len(migratedProjects))
	for _, migratedProject := range migratedProjects {
		gitlabId := RetrieveGitLabProjectIdentifier(gitlabCredentials, migratedProject)
		descriptions = append(descriptions, ProjectRedirectDescription{
			gitLabProjectPathWithNamespace: gitlabId,
			renkuProjectUlid:               migratedProject.projectId,
		})
	}
	return descriptions
}

func CreateRedirects(renkuCredentials ServerCredentials, redirectDescriptions []ProjectRedirectDescription) {
	for _, desc := range redirectDescriptions {
		endpoint := "/platform/redirects"
		payload := PlatformRedirectConfig{
			SourceUrl: fmt.Sprintf("/projects/%s", desc.gitLabProjectPathWithNamespace),
			TargetUrl: fmt.Sprintf("/p/%s", desc.renkuProjectUlid),
		}
		payloadBytes, err := json.Marshal(payload)
		if err != nil {
			fmt.Printf("Error marshalling payload for %s: %v\n", desc.gitLabProjectPathWithNamespace, err)
			continue
		}
		body := queryRenkuApi(renkuCredentials, endpoint, strings.NewReader(string(payloadBytes)), queryApiFlags{})
		if body == nil {
			fmt.Printf("Error creating redirect for %s\n", desc.gitLabProjectPathWithNamespace)
			continue
		}
		var response PlatformRedirectConfig
		if err := json.Unmarshal(body, &response); err != nil {
			fmt.Printf("Error parsing response for %s: %v\n", desc.gitLabProjectPathWithNamespace, err)
			continue
		}

		fmt.Printf("Successfully created redirect for %s\n", desc.gitLabProjectPathWithNamespace)
	}
}

func FilterToUnregisteredDescriptions(renkuCredentials ServerCredentials, redirectDescriptions []ProjectRedirectDescription) []ProjectRedirectDescription {
	descriptions := make([]ProjectRedirectDescription, 0, len(redirectDescriptions))
	for _, desc := range redirectDescriptions {
		if HasExistingRedirect(renkuCredentials, desc) {
			fmt.Printf("Skipping existing redirect for %s\n", desc.gitLabProjectPathWithNamespace)
			continue
		}
		descriptions = append(descriptions, desc)
	}
	return descriptions
}

func ListMigratedProjects(renkuCredentials ServerCredentials) []MigratedProject {
	// Query the Renku API to get the list of migrated projects
	body := queryRenkuApi(renkuCredentials, "/renku_v1_projects/migrations", nil, queryApiFlags{})
	if body == nil {
		return nil
	}

	type migrationResponse struct {
		ProjectID  string `json:"project_id"`
		V1ID       uint   `json:"v1_id"`
		LauncherID string `json:"launcher_id"`
	}

	var migrations []migrationResponse
	if err := json.Unmarshal(body, &migrations); err != nil {
		fmt.Printf("Error parsing JSON response: %v\n", err)
		return nil
	}

	projects := make([]MigratedProject, 0, len(migrations))
	for _, m := range migrations {
		projects = append(projects, MigratedProject{
			v1Id:      m.V1ID,
			projectId: m.ProjectID,
		})
	}
	return projects
}

func HasExistingRedirect(renkuCredentials ServerCredentials, projectRedirectDesc ProjectRedirectDescription) bool {
	sourceUrl := fmt.Sprintf("/projects/%s", projectRedirectDesc.gitLabProjectPathWithNamespace)
	endpoint := fmt.Sprintf("/platform/redirects/%s", url.PathEscape(sourceUrl))
	body := queryRenkuApi(renkuCredentials, endpoint, nil, queryApiFlags{accept404: true})
	if body == nil {
		return false
	}

	var config PlatformRedirectConfig
	if err := json.Unmarshal(body, &config); err != nil {
		fmt.Printf("Error parsing platform redirect config: %v\n", err)
		return false
	}
	return true
}

func RetrieveGitLabProjectIdentifier(gitlabCredentials ServerCredentials, migratedProject MigratedProject) string {
	// Query the GitLab API to get the project identifier
	url := fmt.Sprintf("https://%s/api/v4/projects/%d", gitlabCredentials.host, migratedProject.v1Id)
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		fmt.Printf("Error creating GitLab request: %v\n", err)
		return ""
	}
	req.Header.Set("PRIVATE-TOKEN", gitlabCredentials.token)
	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		fmt.Printf("Error calling GitLab API: %v\n", err)
		return ""
	}
	defer resp.Body.Close()

	if resp.StatusCode != 200 {
		fmt.Printf("GitLab API returned status: %d\n", resp.StatusCode)
		fmt.Printf("Retrieving information about GitLab project: %d (GitLab id) <- %s (Renku id)\n", migratedProject.v1Id, migratedProject.projectId)
		return ""
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		fmt.Printf("Error reading GitLab response: %v\n", err)
		return ""
	}

	var result struct {
		PathWithNamespace string `json:"path_with_namespace"`
	}
	if err := json.Unmarshal(body, &result); err != nil {
		fmt.Printf("Error parsing GitLab JSON: %v\n", err)
		return ""
	}

	return result.PathWithNamespace
}

func ScanCredentials(name Platform) ServerCredentials {
	var token string
	hostEnvVar := fmt.Sprintf("%s_HOST", strings.ToUpper(string(name)))
	host := defaultHosts[name]
	if envHost := os.Getenv(hostEnvVar); envHost != "" {
		fmt.Printf("Using %s host from environment variable (%s): %s\n", name, hostEnvVar, envHost)
		host = envHost
	} else {
		fmt.Printf("No %s host environment variable (%s) found, using default: %s\n", name, hostEnvVar, defaultHosts[name])
	}
	tokenEnvVar := fmt.Sprintf("%s_TOKEN", strings.ToUpper(string(name)))
	if envToken := os.Getenv(tokenEnvVar); envToken != "" {
		fmt.Printf("Using %s token from environment variable (%s).\n", name, tokenEnvVar)
		return ServerCredentials{host: host, token: envToken}
	}

	if name == "GitLab" {
		fmt.Printf("  Enter GitLab (%s) admin user access token for the read_api scope: ", host)
	} else {
		fmt.Printf("  Enter Renku (%s) admin session token (from browser cookie '_renku_session'): ", host)
	}
	fmt.Scanln(&token)

	return ServerCredentials{host: host, token: token}
}
