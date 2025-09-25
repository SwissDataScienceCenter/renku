# Generate Redirects Script

Renku Legacy projects can be migrated into projects for the current version of Renku. There is a problem, though. The identifier for the (Legacy) GitLab project is the GitLab identifier, and this requires access to GitLab to determine the namespace/slug. Once GitLab is decommissioned, this will no longer be possible.

This script address this problem and registers redirect entries for the namespace/slug of the migrated project.


## Running

In this folder, run

```
go run .
```

You will be prompted for authentication tokens for Renku and GitLab. In both cases, admin tokens are required for the script wo work correctly.

For Renku there is no way to create tokens -- the only option is to get the session cookie from the browser.
