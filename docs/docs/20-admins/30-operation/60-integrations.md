---
title: Integrations and Connected Services
---

Renku uses integrations so that users can easily gain access to resources
from third party sources. Currently, these are limited to code repositories.
But work is underway to expand this to container registries and different
compute platforms such as HPC clusters. All of these integrations or connected
services are just OAuth2 clients to 3rd party services or applications.

Therefore, for every integration there are 2 steps:

1. Creating the client at the 3rd party application (e.g. at Gitlab.com).
2. Adding the client configuration in Renku from the admin console.

:::note

You need to be a Renku administrator in order to add integrations. You can assign
the administrator role by following the steps [here](user-management).

:::

## Creating Integrations

Currently, we support only a few integrations. The steps to create and register the clients
for each are described below.

### Gitlab

This can be used with `Gitlab.com` or with any other Gitlab deployment. You can also
register more than one client of the "Gitlab" kind.

#### Create the client in Gitlab

1. Navigate to your Gitlab deployment and log in. You may create the application
   as an admin or even as a regular user as long as your specific Gitlab deployment
   allows this.
   :::info
   Some Gitlab deployments have the option to create applications disabled.
   If this is the case you have to request this setting to be changed by the
   administrators of your Gitlab deployment.
   :::
2. Click on your profile picture in Gitlab and then navigate to `Preferences`.
3. Click on `Applications`.
4. Click `Add new application`.
5. Write a descriptive name for the application
6. Specify the redirect URL based the base URL of your Renku deployment
   like `https://<server-name>/api/data/oauth2/callback`. For example,
   if your Renku deployment is at `https://renkulab.io` then the callback will be
   `https://renkulab.io/api/data/oauth2/callback`.
7. Use the following scopes:
   - `read_api`
   - `read_repository`
   - `write_repository`
   - `read_registry`
8. Save the client ID and secret that were generated once you created the application.
   These will be needed in the steps to follow below.

From this point on you need to navigate to your Renku deployment and log in as an administrator.
You can follow the steps below to finish adding the integration.

#### Create the integration in Renku

1. Log into Renku and navigate to the admin panel.
2. Come up with an identifier for the integration and fill that in the `Id` field.
   Some people use the domain of the Gitlab deployment as the `Id`.
3. Select `Gitlab` in the `Kind` field dropdown menu.
4. You can skip the `Application slug` field and leave it blank.
5. Provide a concise but clear descriptive name that your users can understand for the `Display Name` field.
6. Specify the base URL for the Gitlab deployment, for example `https://gitlab.com`.
7. Specify the client ID and secret that you received when you created the application in the
   Gitlab pages for the corresponding `Client ID` and `Client Secret` fields.
8. Specify the same scopes in the `Scope` field that you requested when you created the application
   in the Gitlab pages in the steps above. List these separated by spaces like:

```
read_api read_repository write_repository read_registry
```

### GitHub.com

GitHub allows for two ways to integrate it. Renku uses _GitHub Apps_
for accessing code repositories. It requires an _OAuth App_ for
accessing private images at `ghcr.io`.

Therefore, two integrations for GitHub are necessary in
Renku. It is important, that the _GitHub App_ - the one accessing code
repositories has an _empty_ `image_registry_url` setting. For the
_OAuth App_ however, a `image_registry_url` (which is
`https://ghcr.io` for `github.com`) is required.

#### Create the client in GitHub

This creates a _GitHub App_ for accessing code repositories.

1. Navigate to `https://github.com`.
2. You can create the Renku client as part of your own profile or an organization.
   It is more maintainable if you create the client as part of an organization rather than
   as an individual user. 1. To create the client in an organization, navigate to the settings
   page for the organization and then to `GitHub Apps` -> `New GitHubApp`. 2. To create the client as part of a specific user, navigate to your profile settings page
   then to `Developer settings` -> `GitHub Apps` -> `New GitHub App`.
3. You should now see the form for adding the application.
4. Name the GitHub application. GitHub will derive a slug from this name that you will need when you
   register the integration in Renku.
5. Fill in the description which will be displayed to users when they connect the integration
   and are asked to log into GitHub.
6. For the `Homepage URL` use the URL of your Renku deployment.
7. The `Callback URL` is based on the URL of your Renku deployment
   like `https://<server-name>/api/data/oauth2/callback`. For example,
   if your Renku deployment is at `https://renkulab.io` then the `Callback URL` will be
   `https://renkulab.io/api/data/oauth2/callback`.
8. Under the `Webhook` section uncheck the `Active` checkbox.
9. In the `Permissions` section select the following permissions:
   - `Repository permissions` -> `Contents` -> `Read and write`
   - `Repository permissions` -> `Packages` -> `Read-only`
10. For the question at the bottom asking `Where can this GitHub App be installed` select `Any account`.
11. Click `Create GitHub App`
12. The URL where your browser page gets redirected to once you have created the app
    will contain the slug of the application. Save this version of the name because you will need it
    in the `Application slug` field in the Renku forms to add the integration. The url is different based on whether
    you are creating the application at the user or organization level. - Organization: `https://github.com/organizations/<organization>/settings/apps/<application-slug>` - User: `https://github.com/settings/apps/<application-slug>`
13. You will need to request a client secret to be created, one does not get created automatically.
    You can do this by clicking the `Generate a new client secret` button once you have created the application.
14. Save the `Client ID` and client secret because you will need them in the steps to follow.

#### Creating OAuth App client in GitHub

This is very similar to the above.

1. Go to `Developer settings` -> `OAuth Apps` -> `New OAuth App`.
2. You should now see the form for adding the application.
3. Name the application. GitHub will derive a slug from this name that
   you will need when you register the integration in Renku.
4. Fill in the description which will be displayed to users when they
   connect the integration and are asked to log into GitHub.
5. For the `Homepage URL` use the URL of your Renku deployment.
6. The `Callback URL` is based on the URL of your Renku deployment
   like `https://<server-name>/api/data/oauth2/callback`. For example,
   if your Renku deployment is at `https://renkulab.io` then the `Callback URL` will be
   `https://renkulab.io/api/data/oauth2/callback`.
7. `Device Flow` is not necessary, it can be left disabled.
8. Register the application

Once registered, complete the process by generating a new client
secret. Copy the client secret and client id somewhere, it is required
to specify when creating the Renku integration.

#### Create the integration in Renku

1. Log into Renku and navigate to the admin panel.
2. Come up with an identifier for the integration and fill that in the `Id` field.
   Some people use the domain of the external service as the `Id`. For example in this case `github.com`.
3. Select `GitHub` in the `Kind` field dropdown menu.
4. Fill in the `Application slug` field, this is required for GitHub providers.
   There are instructions in the steps above on how to find the application slug.
5. Provide a concise but clear descriptive name that your users can understand for the `Display Name` field.
6. Specify the base URL for GitHub (i.e. `https://github.com`) in the `URL` field.
7. Specify the client ID and secret that you received when you created the application in the
   GitHub pages for the corresponding `Client ID` and `Client Secret` fields.
8. Only for an _OAuth App_: fill in the `Image Registry Url`. For
   `github.com` it is `https://ghcr.io`. Leave `Image Registry Url`
   empty when adding an integration for a _GitHub App_.

This needs to be done twice: once for a _GitHub App_ (for accessing
code repositories) and once for the _OAuth App_ (for accessing
images).

### Enterprise GitHub

The steps for enterprise GitHub are almost identical to the steps for `GitHub.com`.
The main difference is that when you register the integration in Renku in the `URL` field you should provide
the specific URL for your GitHub deployment which is different from `https://github.com`.

### Google

This integration can be used for mounting `drive` data connectors.

#### Create the client in Google

1. Log into the [Cloud Console](https://console.cloud.google.com/).
2. Select an existing project or create one.
3. Go to `APIs & Services` -> `Enabled APIs & Services`, then click `+ Enable APIs and services`, search "Drive", pick
    the `Google Drive API` and make sure that the API is enabled.
4. Now go to `APIs & Services` -> `OAuth consent screen`. If the consent screen is not configured, click `Configure consent screen`
    then `Get Started`:
    1. Enter the application name
    2. Enter the user support email (you can pick the email account used for the Cloud Console).
    3. Under `Audience`, pick `External`
    4. Enter any remaining required details and click `Create`
5. Configure scopes in `APIs & Services` -> `OAuth consent screen` -> `Data Access` by clicking `Add or remove scopes`:
    1. Search for `userinfo.email` and select it (full string is `https://www.googleapis.com/auth/userinfo.email`)
    2. Search for `auth/docs` and select it (full string is `https://www.googleapis.com/auth/docs`)
    3. Search for `auth/drive` and select it (full string is `https://www.googleapis.com/auth/drive`)
    4. Search for `auth/drive.metadata.readonly` and select it (full string is `https://www.googleapis.com/auth/drive.metadata.readonly`)
    5. Make sure to click `Save` to persist the changes
6. You may need to manually add test users in `APIs & Services` -> `OAuth consent screen` -> `Audience`
7. Go to `APIs & Services` -> `OAuth consent screen` -> `Clients` and click `Create client`:
    1. Under `Application type`, pick `Web application`
    2. Enter a name
    3. Under `Authorized redirect URIs`, click `+ Add URI` and enter the callback URL: `https://<DOMAIN>/api/data/oauth2/callback`
        where `<DOMAIN>` is the domain where your RenkuLab instance is deployed
    4. Click `Create`
    5. Save the OAuth 2.0 application credentials; this can be done by downloading the JSON configuration file.
8. Once you have tested the integration, you can go to `APIs & Services` -> `OAuth consent screen` -> `Audience` and click `Publish app`
    to let users connect

Note: you can also consult the [rclone documentation about Google Drive](https://rclone.org/drive/#making-your-own-client-id).

#### Create the integration in Renku

1. Log into Renku and navigate to the admin panel.
2. Come up with an identifier for the integration and fill that in the `Id` field.
Some people use the domain of the Gitlab deployment as the `Id`.
3. Select `Google` in the `Kind` field dropdown menu.
4. You can skip the `Application slug` field and leave it blank.
5. Use `Google` for then `Display Name` field.
6. Set the `URL` to `https://google.com`.
7. Specify the client ID and secret that you received when you created the client in the Google Cloud Console.
8. Specify the same scopes in the `Scope` field that you requested when you created the client:

```
https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/docs https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/drive.metadata.readonly
```

## Testing Integrations {#testing-integrations}

After you have created an integration, you should test it to ensure that it is functioning properly.
It is very easy to have a typo in some of the configurations which will cause the integration to not work.

Here are the steps needed to test an integration you have just added:

1. Navigate to the `Integrations` page by logging in, clicking on your profile in
   the top right corner and selecting `Integrations` from the menu.
2. Find the integration you just added and click the `Connect` button.
3. Fully complete the login flow by logging into the 3rd party service (i.e. Gitlab.com).
   Once the login flow is complete you will redirected back to Renku and the integration should
   have a status that reads `Connected`
   :::note
   If you do not have an account at the 3rd party service, then either create one
   or ask one of the Renku users that has an account to test the integration.
   :::
4. Try to access the resources from the 3rd party service in a session. For example for code repositories,
   try to add a repository to a project and check that you can both pull and push to the repository.
