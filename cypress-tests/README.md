# Cypress tests for Renku

This section includes a set of acceptance tests for RenkuLab. The tests are
written using [Cypress](https://www.cypress.io), run in Chrome, and the main target
is the web interface of [RenkuLab](https://renkulab.io).

## Test coverage

The tests aim to cover the most common user interactions with RenkuLab. The
goal is to ensure that the most important features work as expected and new
features or changes do not introduce regressions.

Other details specific to the web interface (like the UI design, responsiveness,
error handling, etc.) are tested in the unit and integration tests of the
[Renku UI repository](https://github.com/SwissDataScienceCenter/renku-ui).

## Running the tests

You need a fully working RenkuLab deployment to run the tests against, and a user
account for the deployment.

To run the tests locally, you will need to clone this repository and have a
recent version of Node.js installed. Then follow these steps:

- Install the dependencies with `npm install`.
- Either create a `cypress.env.json` file with the necessary user and deployment
  details (you can start from the `cypress.env.template.json` template) or be sure
  to set all the environment variables listed below.
- Run the tests with `npm run e2e` or one of the many available flags. For example,
  you can run the tests without the GUI with `npm run e2e:headless`.

Here is a list of the environment variables that you should set in the
`cypress.env.json` file:

| VARIABLE        | USE                                                   |
| --------------- | ----------------------------------------------------- |
| BASE_URL        | Full URL of the target environment.                   |
| TEST_EMAIL      | The email used to register on Keycloak.               |
| TEST_PASSWORD   | Password.                                             |
| TEST_FIRST_NAME | First name.                                           |
| TEST_LAST_NAME  | Last name.                                            |
| TEST_USERNAME   | Username. Usually, it's the email without the domain. |

> Tip: you might prefer _not_ to save your password in plain text in the `cypress.env.json`
> file.

## Integration with CI pipeline

We require these tests to pass before merging any feature branch to the release branch.
The primary purpose is to spot regressions early and avoid leaving the burden of
fixing them to the release manager.

Most Renku repositories have a CI pipeline that runs the tests automatically for every
commit. That requires deploying a full RenkuLab instance using the `/deploy` string
in the PR description. Tests can be excluded by adding the `#notest` flag.
You can read more details in the [renku-actions repository](https://github.com/SwissDataScienceCenter/renku-actions/tree/master/test-renku-cypress).
