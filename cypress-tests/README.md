# Cypress tests for Renku

This section includes a set of acceptance tests for RenkuLab. The tests are
written using [Cypress](https://www.cypress.io), run in Chrome, and the main target
is the web interface of RenkuLab.

## Running the tests

You need a fully working RenkuLab deployment to run the tests against. You will also
need a user account for the deployment.

If you wish to run the tests locally, you will need to clone the repository and have a
recent version of Node.js installed. Then follow these steps:

- Create a `cypress.env.json` file with the necessary user credentials. You can use the
  `cypress.env.template.json` file as a template.
- Install the dependencies with `npm install`.
- Run the tests with `npm run e2e`. If you prefer not to use the GUI, you can run the
  tests in headless mode with `npm run e2e:headless`.

> Tip: you might prefer not to save you password in plain text in the `cypress.env.json`
  file. In that case, you can use the `TEST_PASSWORD` variable to the command line when
  running the tests. For example `TEST_PASSWORD=mySecretPassword npm run e2e`.`

## Integration with CI pipeline

The primary purpose of the tests is to spot regressions early on new pull requests.
Most Renku repositories have a CI pipeline that runs the tests automatically for every
commit. That requires deploying a full RenkuLab instance using the
`/deploy #persist` command. The tests are run automatically in headless mode unless
the flag `#notest` is included.

You can read more details in the documentation in the
[renku-actions repository](https://github.com/SwissDataScienceCenter/renku-actions/tree/master/test-renku-cypress).

Mind that including the `#persist` flag is currently necessary since the deployment
is deleted automatically after running a separate set of acceptance tests; the Cypress
tests generally run much quicker but they are not guaranteed to finish on time.
Re-running single tests would also fail when the deployment is deleted.

## Limitations

- Using the electron browser from Cypress will not work because a few features in
  RenkuLab do not work with that (E.G: RStudio does not load at all).
- Tests currently do not run on Firefox. Please use [Chrome](https://www.google.com/chrome)
  or [Chromium](https://www.chromium.org) instead. 
