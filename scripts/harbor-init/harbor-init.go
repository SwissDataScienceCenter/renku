package main

import (
	"bytes"
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"strconv"
	"strings"
)

const (
	// Harbor API version
	HARBOR_API_ENDPOINT = "/api/v2.0"
)

type SensitiveString string

func (s SensitiveString) String() string {
	return "<redacted sensitive string>"
}

func (s SensitiveString) Format(w fmt.State, v rune) {
	_, err := w.Write([]byte(s.String()))
	if err != nil {
		panic(err)
	}
}

func (s *SensitiveString) Set(value string) error {
	// Set the sensitive string value
	*s = SensitiveString(value)
	return nil
}

type Login struct {
	Client   *http.Client
	Url      string
	Username string
	Password SensitiveString
}

func (l *Login) String() string {
	return l.Url + " " + l.Username + " " + "<redacted password>"
}

func (l *Login) Format(w fmt.State, v rune) {
	_, err := w.Write([]byte(l.String()))
	if err != nil {
		panic(err)
	}
}

type Projects []struct {
	Name string `json:"name"`
}

type ProjectRobots []struct {
	Name        string `json:"name"`
	Id          int    `json:"id"`
	Permissions []struct {
		Namespace string `json:"namespace"`
	} `json:"permissions"`
}

type ResponseRobot struct {
	Id int `json:"id"`
}

func request(method string, login Login, endpoint string, body []byte) (*http.Response, []byte, error) {
	req, err := http.NewRequest(method, login.Url+endpoint, bytes.NewBuffer(body))
	if err != nil {
		return nil, nil, err
	}
	req.SetBasicAuth(login.Username, string(login.Password))
	req.Header.Set("Content-Type", "application/json")

	resp, err := login.Client.Do(req)
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
	urlFlag := flag.String("url", "https://harbor.example.com", "Harbor URL")
	adminFlag := flag.String("admin", "admin", "Harbor admin username")
	var passwordFlag SensitiveString = ""
	flag.Var(&passwordFlag, "password", "Harbor admin password")
	projectNameFlag := flag.String("project", "renku-build", "Project name")
	robotAccountNameFlag := flag.String("robot", "renku-registry-robot", "Robot account name")
	var robotSecretFlag SensitiveString = ""
	flag.Var(&robotSecretFlag, "secret", "Robot account secret")
	flag.Parse()
	// Initialize logger to print to stdout
	logger := log.New(os.Stdout, "INFO: ", log.Ldate|log.Ltime)
	login := Login{}
	login.Client = &http.Client{}
	// Get login from environment variable
	if *urlFlag == "" {
		logger.Fatal("url flag is not set")
	}
	login.Url = strings.TrimRight(*urlFlag, "/") + HARBOR_API_ENDPOINT
	login.Username = *adminFlag
	if login.Username == "" {
		logger.Fatal("admin flag is not set")
	}
	login.Password = passwordFlag
	if login.Password == "" {
		logger.Fatal("password flag is not set")
	}
	projectName := *projectNameFlag
	if projectName == "" {
		logger.Fatal("project flag is not set")
	}
	robotAccountName := *robotAccountNameFlag
	if robotAccountName == "" {
		logger.Fatal("robot flag is not set")
	}
	robotSecret := robotSecretFlag
	if robotSecret == "" {
		logger.Fatal("secret flag is not set")
	}

	// Authenticate and get a session cookie
	logger.Println("Authenticating with Harbor server")
	resp, body, err := request("GET", login, "/audit-logs", nil)
	if err != nil {
		logger.Fatal("Error connecting to Harbor: ", err)
	}
	if resp.StatusCode != http.StatusOK {
		logger.Fatal("Failed to authenticate:", resp.Status, err, string(body))
		return
	}
	logger.Println("Authenticated successfully")

	// Check if project already exists
	logger.Println("Checking if project exists:", projectName)
	resp, body, err = request("GET", login, "/projects?name="+projectName, nil)
	if err != nil {
		logger.Fatal("Error getting project Harbor: ", err)
	}
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
			"public":       true, // For Renkulab we assume that the project is public and can be read by anyone
		}
		projectData, err := json.Marshal(project)
		if err != nil {
			logger.Fatal("Error marshaling project data:", err)
		}

		resp, _, err = request("POST", login, "/projects", projectData)
		if err != nil {
			logger.Fatal("Error creating project: ", err)
		}
		if resp.StatusCode != http.StatusCreated {
			logger.Fatal("Failed to create project:", resp.Status, err)
		}
		logger.Println("Project created successfully")
	} else {
		logger.Println("Project already exists")
	}

	// Check if robot account already exists
	logger.Println("Checking if robot account exists:", robotAccountName)
	resp, body, err = request("GET", login, "/projects/"+projectName+"/robots", nil)
	if err != nil {
		logger.Fatal("Error getting robot account: ", err)
	}
	if resp.StatusCode != http.StatusOK {
		logger.Fatal("Failed to get robot account:", err)
	}
	var robots ProjectRobots
	err = json.Unmarshal(body, &robots)
	if err != nil {
		logger.Fatal("Error unmarshaling robots account data:", err)
	}
	robotId := ""
	for _, robot := range robots {
		if robot.Name != "robot$"+projectName+"+"+robotAccountName {
			continue
		}
		for _, permission := range robot.Permissions {
			if permission.Namespace != projectName {
				continue
			}
			logger.Println("Robot account already exists")
			robotId = strconv.Itoa(robot.Id)
			break
		}
	}

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
			"duration": -1,
			"level":    "project",
		}
		robotAccountData, err := json.Marshal(robotData)
		if err != nil {
			logger.Fatal("Error marshaling robot account data:", err)
		}
		resp, respBody, err := request("POST", login, "/robots/"+robotId, robotAccountData)
		if err != nil {
			logger.Fatal("Error creating robot account: ", err)
		}
		createdRobot := ResponseRobot{}
		json.Unmarshal(respBody, &createdRobot)
		robotId = strconv.Itoa(createdRobot.Id)
		if resp.StatusCode != http.StatusCreated {
			logger.Fatal("Failed to create robot account:", resp.Status, string(respBody), err)
		}
	}

	// Update the robot account secret
	logger.Println("Setting the secret for the robot account:", robotAccountName)
	robotData = map[string]interface{}{
		"secret": string(robotSecret),
	}
	robotAccountData, err := json.Marshal(robotData)
	if err != nil {
		logger.Fatal("Error marshaling robot account data:", err)
	}

	resp, respBody, err := request("PATCH", login, "/robots/"+robotId, robotAccountData)
	if err != nil {
		logger.Fatal("Error updating robot secret: ", err)
	}
	if resp.StatusCode != http.StatusOK {
		logger.Fatal("Failed to update the secret of the robot account:", resp.Status, string(respBody), err)
	}

	logger.Println("Robot account secret updated successfully.")
}
