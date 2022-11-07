import { rstudioTestFuncs } from "@renku/notebooks-cypress-tests";
import { v4 as uuidv4 } from 'uuid';
import { registerCustomCommands } from "../support";

const email = Cypress.env("TEST_EMAIL")
const username = Cypress.env("TEST_USERNAME")
const password = Cypress.env("TEST_PASSWORD")
const firstName = Cypress.env("TEST_FIRST_NAME")
const lastName = Cypress.env("TEST_LAST_NAME")
const projectName = `test-project-${uuidv4()}`

describe('Basic rstudio functionality', () => {
  before(() => {
    // Save all cookies across tests
    Cypress.Cookies.defaults({
      preserve: (_) => true
    })
    registerCustomCommands()
    // Register with the CI deployment
    cy.register(email, password, firstName, lastName)
    cy.url().then((url) => {
      const parsedUrl = new URL(url)
      if (parsedUrl.pathname.includes("registration")) {
        // Still on registration page, it means registration failed, so simply try to log in.
        // Usually occurs because test user already exists on the CI deployment.
        cy.contains('Back to Login').click()
        cy.renkuLogin([{ username: email, password }])
      }
    })
    // Login with the main dev deployment after registering on the CI deployment (if required)
    cy.url().then((url) => {
      if (url.includes("auth/realms/Renku/protocol/openid-connect/auth")) {
        cy.renkuLogin([{ username: email, password }])
      }
    })
    cy.url().should("be.oneOf", [Cypress.config("baseUrl"), Cypress.config("baseUrl") + "/"])
    cy.request("ui-server/api/user").its("status").should("eq", 200)
  })
  afterEach(() => {
    // Prevents the ui from automatically refreshing the page, it does this when the local storage is wiped
    // and the local storage is wiped by default after an "it" section ends in cypress
    cy.saveLocalStorage()
  })
  beforeEach(() => {
    // Prevents the ui from automatically refreshing the page, it does this when the local storage is wiped
    // and the local storage is wiped by default after an "it" section ends in cypress
    cy.restoreLocalStorage()
  })
  it('Creates a project', { defaultCommandTimeout: 30000 }, () => {
    const templateName = "Basic R"
    cy.visit("/")
    cy.get('[data-cy="username-home"]').should("include.text", username)
    cy.createProject(projectName, templateName, username)
  })
  it('Waits for the image to build', { defaultCommandTimeout: 300000 }, () => {
    cy.waitForImageToBuild(username, projectName)
  })
  it('Launches a session', () => {
    cy.startSession(username, projectName)
  })
  it('Waits for the session to fully start', { defaultCommandTimeout: 300000 }, () => {
    cy.get('.time-caption').should("contain.text", "Running")
  })
  it('Opens the session in an iframe', () => {
    cy.contains("Open").click()
    cy.get('div.details-progress-box', { timeout: 300000 }).should("not.exist")
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.findExpectedElements()
    })
  })
  it('Launches a terminal in the session iframe', () => {
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.launchTerminal()
    })
  })
  it('Runs terminal command to create a file', () => {
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.makeFileWithTerminal("new-file.txt")()
    })
  })
  it('Removes the file', () => {
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.removeFileWithTerminal("new-file.txt")()
    })
  })
  it('Closes the terminal', () => {
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.closeTerminal()
    })
  })
  it('Finds expected start page elements again', () => {
    cy.getIframe("iframe#session-iframe").within(() => {
      rstudioTestFuncs.findExpectedElements()
    })
  })
  it('Deletes the project', () => {
    cy.deleteProject(username, projectName)
  })
  it('Stops the session', () => {
    cy.stopSessionFromIframe()
  })
})
