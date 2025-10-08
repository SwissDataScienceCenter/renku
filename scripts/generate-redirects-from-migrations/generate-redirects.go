package main

import (
	"fmt"
)

func main() {
	fmt.Println("This script creates redirect entries for migrated projects.")
	fmt.Print("You will be prompted for access tokens with admin rights for Renku and GitLab, respectively.\n\n")
	renkuCredentials := ScanCredentials("Renku")
	gitlabCredentials := ScanCredentials("GitLab")
	migratedProjects := ListMigratedProjects(renkuCredentials)
	allPotentialRedirects := CollectProjectRedirectDescriptions(gitlabCredentials, migratedProjects)
	redirectDescriptions := FilterToUnregisteredDescriptions(renkuCredentials, allPotentialRedirects)
	fmt.Printf("%d migrated projects found\n%d new redirects to create\n", len(allPotentialRedirects), len(redirectDescriptions))
	for _, redirect := range redirectDescriptions {
		fmt.Printf("\tRedirect %s -> %s\n", redirect.gitLabProjectPathWithNamespace, redirect.renkuProjectUlid)
	}
	if len(redirectDescriptions) == 0 {
		fmt.Println("No new redirects to create. Exiting.")
		return
	}
	fmt.Printf("\nDo you want to create these redirects? (y/N): ")
	var response string
	fmt.Scanln(&response)
	if response == "y" || response == "Y" {
		CreateRedirects(renkuCredentials, redirectDescriptions)
	} else {
		fmt.Println("Aborting.")
	}
}
