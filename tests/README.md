# renku-acceptance-tests

This project contains Renku acceptance tests.

## Running over Docker

The easiest way to run the tests is to use docker/docker-compose. The docker-compose environment is made up of two containers: one hosting selenium, and one running the tests.

#### Installation
The `docker-compose` command has to be available. For installation steps see: https://docs.docker.com/compose/install/.

#### Running
For running with docker, the parameters to the test need to be provided as environment variables:

```
docker-compose run -e RENKU_TEST_URL=https://renku-kuba.dev.renku.ch -e RENKU_TEST_FULL_NAME="<full user name>" -e RENKU_TEST_EMAIL=<email> -e RENKU_TEST_USERNAME=<username> -e RENKU_TEST_PASSWORD=<password> -e RENKU_TEST_CLI_VERSION=<renkuVersion> sbt
```

By default, all tests are run. You can provide an argument to run a specific test:

```
docker-compose run -e RENKU_TEST_URL=https://renku-kuba.dev.renku.ch -e RENKU_TEST_FULL_NAME="<full user name>" -e RENKU_TEST_EMAIL=<email> -e RENKU_TEST_USERNAME=<username> -e RENKU_TEST_PASSWORD=<password> -e RENKU_TEST_CLI_VERSION=<renkuVersion> sbt  /tests/docker-run-tests.sh *<test-class-name>*
```

## Running using sbt

#### Installation
In order to run the tests you need to install:
* Java Development Kit (JDK) >= 8 (see: https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9)
* sbt >= 1.3.3 (use `sbt sbtVersion` to verify current version but run it not from the project root)
  * Mac users: `brew install sbt`
  * Linux users see: https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html
* chromedriver
  * Mac users: `brew tap homebrew/cask && brew cask install chromedriver`
  * Debian based users: `sudo apt-get install chromium-chromedriver`
  * for other see: https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver

#### Running
The tests are run by executing `sbt` in the project's root.

In order to run the acceptance tests, it is necessary to provide information for reaching and logging into the Renku instance. These parameters can be provided either as command-line arguments (using `-D<argument>=<value>`), environment variables or put into the `tests-defaults.conf` file. The precedence order is command-line argument, environment variable and the defaults file.

| Argument   | Environment               | Purpose                                                          |
| ---------- | ------------------------- | ---------------------------------------------------------------- |
| env        | RENKU_TEST_URL            | URL to a Renku server, e.g., https://dev.renku.ch                |
| email      | RENKU_TEST_EMAIL          | user's email e.g. `jakub.chrobasik@epfl.ch`                      |
| username   | RENKU_TEST_USERNAME       | user's username e.g. `jakub.chrobasik1`                          |
| fullname   | RENKU_TEST_FULL_NAME      | user's full name e.g. `Jakub Józef Chrobasik`                    |
| password   | RENKU_TEST_PASSWORD       | user's password                                                  |
| provider   | RENKU_TEST_PROVIDER       | if non-empty, use an OpenID provider for auth                    |
| register   | RENKU_TEST_REGISTER       | if non-empty, an register new user; has precedence over provider |
| docsrun    | RENKU_TEST_DOCS_RUN       | if non-empty, screenshot for docs during hands-on test           |
| extant     | RENKU_TEST_EXTANT_PROJECT | if non-empty, an existing project to use for tests               |
| anon       | RENKU_TEST_ANON_PROJECT   | namespace/name for the project to test anonymously / without developer privileges: defaults to cramakri/covid-19-dashboard |
| anonAvail  | RENKU_TEST_ANON_AVAILABLE | if true, anonymous environments will be tested. Defaults to true on dev.renku.ch, false everywhere else. |
| renkuVersion | RENKU_TEST_CLI_VERSION  | renku cli version to be used on command line e.g `0.10.4`        |

For example, the following may be run in the project's root: `sbt -Denv=https://renku-kuba.dev.renku.ch -Demail=<email> -Dusername=<username> -Dfullname='<full user name>' -Dpassword=<password> -DrenkuVersion=<renkuVersion> test`

In the case there's a need of running a single test the `test` part should be replaced with `"testOnly *<test-class-name>*"`.

On some machines, providing values with spaces using `-D` causes sbt to complain `Could not find or load main class`. In this case, the environment variables should be used to provide the values.

__**NOTICE**__

* When running locally, it's very important to match version of your Chrome with the version of the chromedriver. If these versions are not the same you have to upgrade one or the other and *reopen* the browser. Running with Docker avoids this problem.
* There's a `tests-execution.log` file created in the `target` directory of the tests where `DEBUG` log statements generated during tests execution are written. This information might be useful when investigating tests failures.

## Running in console

For development, it can be nice to try things out on the console. For this, get started by running the following:

```bash
sbt test:console
```

Once the console is up, execute the following to import the libraries for working with Selenium:

```
:load src/test/scala/ch/renku/acceptancetests/ReplShell.scala
```

Then, on the console, you can execute statements like:

```scala
val webDriver = ReplShell.getChromeWebDriver("https://dev.renku.ch")
val loginButtons = webDriver.findElements(By.cssSelector("a[href='/login']"))
```

# Development
You can work on the tests using editor of your choice but using either Intellij or VS Code could make your life by far simpler. In both cases you need a Scala plugin installed into your IDE. In case of the VS Code there's a Scalameta plugin which can be installed from https://marketplace.visualstudio.com/items?itemName=scalameta.metals.

Please use the `scalameta.metals` as a default formatter for the scala sources and **I strongly encourage to click the `Format on save` feature**.

The test are built using the Page Object Pattern (e.g. https://www.pluralsight.com/guides/getting-started-with-page-object-pattern-for-your-selenium-tests) which in short is about wrapping an UI page into a class/object and using it in the test script.

## Project organization
All the interesting parts are located in the `src/test/scala/ch/renku/acceptancetests` folder which can be perceived as a tests classes root folder. The test scenarios are suffixed with `Spec` while all the page objects are put into the `pages` subfolder.

# FAQ

* Q: I'm getting `Error: Could not find or load main class`

  A: Please verify version of sbt in your computer using `sbt sbtVersion` but run it not from the project root.
