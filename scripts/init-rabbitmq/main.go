package main

import (
	"log"

	rabbithole "github.com/michaelklishin/rabbit-hole/v2"
)

func main() {
	config := getManagementClientConfig()
	rmqc, err := rabbithole.NewClient(config.URL, config.Admin.Username, config.Admin.Password)
	if err != nil {
		log.Fatalf("Cannot initiate rabbithole client: %s", err.Error())
	}
	_, err = rmqc.PutVhost(config.Vhost, rabbithole.VhostSettings{})
	log.Printf("Creating vhost: %s\n", config.Vhost)
	if err != nil {
		log.Fatalf("Cannot create vhost %s: %s\n", config.Vhost, err.Error())
	}
	for serviceName, creds := range config.Users {
		log.Printf("Creating user for service %s\n", serviceName)
		rmqc.PutUser(creds.Username, rabbithole.UserSettings{Name: creds.Username, Password: creds.Password})
	}
	for serviceName, servicePermission := range servicePermissions {
		log.Printf("Creating permission %v for service %s\n", servicePermission, serviceName)
		if _, ok := config.Users[serviceName]; !ok {
			log.Fatalf("Found unexpected service name in permissions: %s\n", serviceName)
		}
		_, err := rmqc.UpdatePermissionsIn(config.Vhost, config.Users[serviceName].Username, servicePermission)
		if err != nil {
			log.Fatalf("Failed to create permission %v for service %s: %s\n", servicePermission, serviceName, err.Error())
		}
	}
}
