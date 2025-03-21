package main

import (
	"bytes"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"os"
	"strconv"
)

const (
	harborURL        = "https://harbor.dev.renku.ch/api/v2.0"
	username         = "admin"
	password         = "foobar"
	projectName      = "foobarproj"
	robotAccountName = "test-automation-robot-foo-bar-blo-bla"
	robotSecret      = "Foo-Bar-Password-321"
)

type Projects []struct {
	Name string `json:"name"`
}

type ProjectRobots []struct {
	Name        string `json:"name"`
	Id int `json:"id"`
	Permissions []struct {
		Namespace string `json:"namespace"`
	} `json:"permissions"`
}

func request(method, endpoint string, body []byte) (*http.Response, []byte, error) {
	client := &http.Client{}
	req, err := http.NewRequest(method, harborURL+endpoint, bytes.NewBuffer(body))
	if err != nil {
		return nil, nil, err
	}
	req.SetBasicAuth(username, password)
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, nil, err
	}
	defer resp.Body.Close()
	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, nil, err
	}
	return resp, respBody, nil
}

func main() {
	// Initialize logger to print to stdout
	logger := log.New(os.Stdout, "INFO: ", log.Ldate|log.Ltime)

	// Authenticate and get a session cookie
	logger.Println("Authenticating with Harbor server")
	resp, body, err := request("GET", "/projects", nil)
	if resp.StatusCode != http.StatusOK {
		logger.Println("Failed to authenticate:", resp.Status)
		return
	}
	logger.Println("Authenticated successfully")

	// Check if project already exists
	logger.Println("Checking if project exists:", projectName)
	resp, body, err = request("GET", "/projects?name="+projectName, nil)
	if resp.StatusCode != http.StatusOK {
		logger.Println("Failed to get project:", err)
		return
	}
	var projects Projects
	err = json.Unmarshal(body, &projects)
	if err != nil {
		logger.Println("Error unmarshaling projects data:", err)
		return
	}
	if len(projects) == 0 {
		logger.Println("The project does not exist yet, creating: ", projectName)
		project := map[string]interface{}{
			"project_name": projectName,
			"public":       false,
		}
		projectData, err := json.Marshal(project)
		if err != nil {
			logger.Fatal("Error marshaling project data:", err)
		}

		resp, _, err = request("POST", "/projects", projectData)
		if resp.StatusCode != http.StatusCreated {
			logger.Fatal("Failed to create project:", resp.Status, err)
		}
		logger.Println("Project created successfully")
	} else {
		logger.Println("Project already exists")
	}

	// Check if robot account already exists
	logger.Println("Checking if robot account exists:", robotAccountName)
	resp, body, err = request("GET", "/projects/"+projectName+"/robots", nil)
	if resp.StatusCode != http.StatusOK {
		logger.Fatal("Failed to get robot account:", err)
		return
	}
	var robots ProjectRobots
	err = json.Unmarshal(body, &robots)
	if err != nil {
		logger.Fatal("Error unmarshaling robots account data:", err)
	}
	robotId := ""
	for _, robot := range robots {
		if robot.Name == "robot$"+projectName+"+"+robotAccountName {
			for _, permission := range robot.Permissions {
				if permission.Namespace == projectName {
					logger.Println("Robot account already exists")
					robotId = strconv.Itoa(robot.Id)
				}
			}
		}
	}
	reqMethod := ""
	robotData := map[string]interface{}{}
	if robotId == "" {
		// Create a robot account
		logger.Println("Creating robot account:", robotAccountName)
		robotData = map[string]interface{}{
			"name": robotAccountName,
			"permissions": []map[string]interface{}{
				{
					"access": []map[string]string{
						{"resource": "repository", "action": "list"},
						{"resource": "repository", "action": "pull"},
						{"resource": "repository", "action": "push"},
						{"resource": "repository", "action": "read"},
					},
					"kind":      "project",
					"namespace": projectName,
				},
			},
			"secret":   robotSecret,
			"duration": -1,
			"level":    "project",
		}
		reqMethod = "POST"
	}

	// The robot account exists, making sure that the secret is up-to-date
	if robotId != "" {
		logger.Println("Robot account already exists, refreshing the secret")
		robotData = map[string]interface{}{
			"secret": robotSecret,
		}
		reqMethod = "PATCH"
	}

	robotAccountData, err := json.Marshal(robotData)
	if err != nil {
		logger.Fatal("Error marshaling robot account data:", err)
	}

	resp, respBody, err := request(reqMethod, "/robots/"+robotId, robotAccountData)
	if resp.StatusCode != http.StatusOK && resp.StatusCode != http.StatusCreated {
		logger.Fatal("Failed to create or update robot account:", resp.Status, string(respBody))
	}

	logger.Println("Robot account created or updated successfully: ", string(respBody))
}
