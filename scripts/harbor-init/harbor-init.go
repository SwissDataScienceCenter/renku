package main

import (
	"bytes"
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"
	"os"
)

const (
	harborURL      = "https://harbor.dev.renku.ch/api/v2.0"
	username       = "admin"
	password       = "p3mWMMs7f2inu4jX7%pNb&UzY&ACtg4H"
	projectName    = "test-automation-project"
	robotAccountName = "test-automation-robot"
)

func request(method, endpoint string, body []byte) (*http.Response, error) {
	client := &http.Client{}
	req, err := http.NewRequest(method, harborURL+endpoint, bytes.NewBuffer(body))
	if err != nil {
		return nil, err
	}
	req.SetBasicAuth(username, password)
	req.Header.Set("Content-Type", "application/json")

	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	return resp, nil
}

func main() {
	// Initialize logger to print to stdout
	logger := log.New(os.Stdout, "INFO: ", log.Ldate|log.Ltime)

	// Authenticate and get a session cookie
	logger.Println("Authenticating with Harbor server")
	resp, err := request("GET", "/projects", nil)
	if resp.StatusCode != http.StatusOK {
		logger.Println("Failed to authenticate:", resp.Status)
		return
	}
	logger.Println("Authenticated successfully")

	// Check if project already exists
	logger.Println("Checking if project exists:", projectName)
	resp, err = request("GET", "/projects?name="+projectName, nil)
	if resp.StatusCode != http.StatusOK {
		logger.Println("The project does not exist yet, creating: ", projectName)
		project := map[string]interface{}{
			"project_name": projectName,
			"public":       false,
		}
		projectData, err := json.Marshal(project)
		if err != nil {
			logger.Println("Error marshaling project data:", err)
			return
		}

		resp, err = request("POST", "/projects", projectData)
		if resp.StatusCode != http.StatusCreated {
			body, _ := ioutil.ReadAll(resp.Body)
			logger.Println("Failed to create project:", resp.Status, string(body))
			return
		}
		logger.Println("Project created successfully")
	} else {
		logger.Println("Project already exists")
	}

	// Create a robot account
	logger.Println("Creating robot account:", robotAccountName)
	robotAccount := map[string]interface{}{
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

	robotAccountData, err := json.Marshal(robotAccount)
	if err != nil {
		logger.Println("Error marshaling robot account data:", err)
		return
	}

	resp, err = request("POST", "/robots", robotAccountData)
	if resp.StatusCode != http.StatusCreated {
		body, _ := ioutil.ReadAll(resp.Body)
		logger.Println("Failed to create robot account:", resp.Status, string(body))
		return
	}
	logger.Println("Robot account created successfully")
}
