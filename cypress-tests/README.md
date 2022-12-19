# Cypress Tests for Renku

This repository aims to fill in gaps in current tests with Cypress.

# Test suites

We currently have the following test suites

## R-Studio sessions

- Register a user / login
- Create a R project
- Launch a session
- Open the session in the iframe view and test basic functionality
- Stop the session
- Delete the created project

## Public Project

- Register a user / login
- Create a Python project
- Search for the project
- Check the content in the Overview tab
- Check the content in the Files tab
- Create and modify a dataset
- View the project settings


# Limitations

- The tests expect to have to register first. If the registration fails
  then the tests will try to login with the provided credentials. After
  this the tests will check if the login succceeded or if an additional
  login is required (as is the case of CI deployments). These are the only
  login scenarios that are supported. The tests cannot handle logging in
  through any but the Renku login screens.
- The tests expect to use the one set of provided credentials to log in
  to up to 2 different Renku deployments. In the case where the Gitlab used by
  Renku is part of another deployment the tests will try to log in twice.
- Using `https` if possible is reccomended in the `BASE_URL` environment
  variable. Completing the registration and logging in fails if the `http`
  is used on the standard dev and/or CI Renku deployments.
- Using the electron browser from cypress will not work because rstudio does not load at
  all in the electron browser.
- Tests currently do not run on Firefox.

# Requirements

- Node LTS or later.
- On Linux you will need additional libraries see [here](https://docs.cypress.io/guides/getting-started/installing-cypress#Linux-Prerequisites).
- You can also swap the regular `Dockerfile` in `.devcontainer/devcontainer.json` with `Dockerfile.cypress`
  and launch a dev container. In the dev container you have to run the tests headless but you will
  have access to a Chrome browser.

# To install

```
npm install
```

# To run

The tests use environment variables to get certain information.
You can set these as shell enviornment variables before running, or
you can copy the `cypress.env.template.json` file to `cypress.env.json` and set the variables there.

**Run with environment variables set in cypress.env.json**
```
npm run e2e
```
**Run passing in environment variables**
```
TEST_USERNAME=my.username TEST_EMAIL=my.username@email.com TEST_PASSWORD=XXXXX TEST_FIRST_NAME=fname TEST_LAST_NAME=lname BASE_URL=https://dev.renku.ch npm run e2e
```

# Integration with CI pipeline

In repos that have integrated the actions for running the cypress tests
(currently `renku` and `renku-ui`), it is possible to run these tests
in the CI pipline with the deploy command.

In the `renku` repo, the cypress-tests are run when a PR is deployed.

E.g.,

```
/deploy
```

In other repos, it is necessary to explicitly request that the
cypress tests are run by adding the `#cypress` token (the deployment)
also needs to be persisted:

```
/deploy #persist #cypress
```

Finally, it is possible to run the cypress acceptance tests without
the selenium acceptance tests:

```
/deploy #persist #notest #cypress
```

To incorporate the cypress tests into the CI pipleines of other
projects, see the documentation in the [renku-actions repo](https://github.com/SwissDataScienceCenter/renku-actions/tree/master/test-renku-cypress).

The action in the [renku-ui repo](https://github.com/SwissDataScienceCenter/renku-ui/blob/master/.github/workflows/acceptance-tests.yml#L109) could also be helpful to look at.
