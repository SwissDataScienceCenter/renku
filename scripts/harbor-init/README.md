# Harbor initialization for Renkulab script

This script can be used to initialize a Harbor registry for usage with Renkulab: it will create a project, a robot account in that project that uses the specified secret for authentication.

## Prerequisite

1. A Harbor deployment
2. Admin credentials
3. Project name
4. Robot account name and secret
5. Access to the Renkulab namespace in Kubernetes

## Usage

With `go` installed, the script can be simply run:

```bash
go run harbor-init.go --url https://<harbor-fqdn> --admin <harbor-admin-username> --password <admin-password> --project <project-name> --robot <robot-account-username> --secret <robot-account-secret>
```

The script is idem-potent, so if the project or robot already exist the script will move on, additionally the robot's secret will always be updated (NOTE: it will overwrite the previous one!).

## Kubernetes secret for Renkulab

The secret for Renkulab can now be created so that container images build can be uploaded to the Harbor project. Using the <robot-account-username> and <robot-account-secret> the Kubernetes secret can be created using `kubectl` as following:

```bash
kubectl --namespace <renku-namespace> create secret docker-registry renku-build-docker-secret --docker-server <harbor-fqdn> --docker-username 'robot$<project-name>+<robot-account-username>' --docker-password '<robot-account-secret>'
```

Note: the username of the robot account is composed by harbor combining the project and its username, e.g. if the project is `foo` and the robot account `bar` then the resulting username would be `robot$foo+bar`.
To make sure that the terminal respects the special characters, the string should be surrounded by single quotes ''.

The name of the secret should be matched in the Renku Helm chart values file under `dataService.imageBuilders.pushSecretName`.
