/// <reference types="cypress" />

import { getIframe } from "./commands/general"
import { renkuLogin, register } from "./commands/login"
import { createProject, deleteProject } from "./commands/projects"
import { startSession, stopSessionFromIframe, waitForImageToBuild } from "./commands/sessions"
import "cypress-localstorage-commands";

export const registerCustomCommands = () => {
  Cypress.Commands.add("getIframe", getIframe)
  Cypress.Commands.add("renkuLogin", renkuLogin)
  Cypress.Commands.add("createProject", createProject)
  Cypress.Commands.add("deleteProject", deleteProject)
  Cypress.Commands.add("startSession", startSession)
  Cypress.Commands.add("waitForImageToBuild", waitForImageToBuild)
  Cypress.Commands.add("stopSessionFromIframe", stopSessionFromIframe)
  Cypress.Commands.add("register", register)
}

declare global {
  namespace Cypress {
    interface Chainable {
      getIframe(selector: string): Chainable<any>,
      renkuLogin(credentials: { username: string, password: string }[]),
      createProject(projectName: string, templateName?: string, namespace?: string),
      deleteProject(namespace: string, name: string),
      startSession(projectNamespace: string, projectName: string),
      waitForImageToBuild(projectNamespace: string, projectName: string),
      stopSessionFromIframe(),
      register(email: string, password: string, firstName?: string, lastName?: string),
    }
  }
}
