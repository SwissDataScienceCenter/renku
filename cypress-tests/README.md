# Cypress Tests for Renku

This repository aims to fill in gaps in current tests with Cypress.

The tests currently only do the following:
- Register a user.
- Create a R project.
- Launch a session.
- Open the session in the iframe view and test basic functionality.
- Stop the session.
- Delete the created project.

Limitations:
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

## Requirements

- Node LTS or later.
- On Linux you will need additional libraries see [here](https://docs.cypress.io/guides/getting-started/installing-cypress#Linux-Prerequisites).
- You can also swap the regular `Dockerfile` in `.devcontainer/devcontainer.json` with `Dockerfile.cypress`
  and launch a dev container. In the dev container you have to run the tests headless but you will
  have access to a Chrome browser.

## To install

```
npm install
```

## To run

The tests use environment variables to get certain information.
You can set these as shell enviornment variables before running, or
you can copy the `cypress.env.template.json` file to `cypress.env.json` and set the variables there.

```
TEST_USERNAME=my.username TEST_EMAIL=my.username@email.com TEST_PASSWORD=XXXXX TEST_FIRST_NAME=fname TEST_LAST_NAME=lname BASE_URL=https://dev.renku.ch npx cypress open
```
