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
  then the tests will try to login wiht the provided credentials. After
  this the tests will check if the login succceeded or if an additional
  login is required (as is the case of CI deployments). These are the only
  login scenarios that are supported. The tests cannot handle logging in 
  through any but the Renku login screens.
- The tests expect to use the one set of provided credentials to log in
  to up to 2 different Renku deployments. In the case where the Gitlab used by
  Renku is part of another deployment the tests will try to log in twice.

## To run

```
TEST_USERNAME=username TEST_EMAIL=some@email.com TEST_PASSWORD=XXXXX TEST_FIRST_NAME=fname TEST_LAST_NAME=lname BASE_URL=http://dev.renku.ch npx cypress open
```
