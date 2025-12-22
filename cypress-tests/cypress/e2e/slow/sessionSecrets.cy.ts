import { TIMEOUTS } from "../../../config";
import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import {
  createSessionLauncher,
  createSessionSecretSlot,
  deleteSessionsForProject,
  executeInSession,
  getEnvironmentByName,
} from "../../support/utils/sessions";
import { login } from "../../support/utils/general";
import { Environment } from "../../support/types/sessions";

const sessionId = ["slow-session-secrets", getRandomString()];

describe("Slow Session Secrets", () => {
  let projectSlug: string;
  let projectId: string;
  let userNamespace: string;
  let pythonSessionLauncherId: string;
  let sessionLauncherName: string;

  before(() => {
    login(sessionId);

    getEnvironmentByName("python").then((environment: Environment) => {
      pythonSessionLauncherId = environment.id;
    });
  });

  beforeEach(() => {
    login(sessionId);

    const randomString = getRandomString();
    projectSlug = `project-for-slow-session-secret-tests-${randomString}`;
    sessionLauncherName = `session-launcher-${randomString}`;

    // Create a project
    getUserData().then((user: User) => {
      userNamespace = user.username;

      createProjectIfMissingV2({
        name: projectSlug,
        namespace: userNamespace,
        slug: projectSlug,
      }).then((response) => {
        projectId = response.body.id;

        createSessionLauncher(projectId, sessionLauncherName, {
          id: pythonSessionLauncherId,
        });
        // Clean up the session and the project after the test
        // eslint-disable-next-line max-nested-callbacks
        cy.defer(() => {
          deleteSessionsForProject(projectId);
          deleteProject(projectId);
        });
      });
    });
  });

  it("Session with secret value provided after creating the secret", () => {
    const secretName = "saved-secret";
    const secretFilename = `${secretName}.txt`;
    const secretValue = "my-saved-secret-value";

    // Navigate to project settings to add a session secret slot
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-settings-session-secrets").scrollIntoView();
    cy.getDataCy("add-session-secret-button").click();

    // Add and save a session secret slot
    cy.getDataCy("add-session-secret-modal").should("be.visible");
    cy.getDataCy("add-session-secret-name-input").type(secretName);
    cy.getDataCy("add-session-secret-filename-input").type(secretFilename);
    cy.getDataCy("add-session-secret-submit-button").click();

    // Provide the secret value
    cy.getDataCy("provide-session-secret-mode-new-value-tab").click();
    cy.getDataCy("provide-session-secret-mode-new-value-input").type(
      secretValue,
    );
    cy.getDataCy("provide-session-secret-save-button").click();

    // Wait for the modal to close (the test fails without this wait)
    cy.getDataCy("add-session-secret-modal").should("not.exist");

    // Navigate to the project page and launch the session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Verify the session starts
    cy.get("[data-cy=session-status-starting]", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");

    // Verify the session is shown
    cy.getDataCy("session-header").contains(sessionLauncherName);
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );

    // Verify the secret file exists and contains the correct value
    cy.url().then((url) => {
      executeInSession(url, `cat /secrets/${secretFilename}`).then((output) => {
        expect(output).to.contain(secretValue);
      });
    });
  });

  it("Session with secret value provided during launch", () => {
    const secretName = "unsaved-password";
    const secretFilename = "unsaved-password.txt";
    const secretValue = "unsaved password value";

    // Create a session secret slot without a value
    createSessionSecretSlot(projectId, secretName, secretFilename);

    // Navigate to the project page and launch the session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Verify the session secrets modal appears
    cy.getDataCy("session-secrets-modal").should("be.visible");

    // Provide the secret value
    cy.getDataCy("session-secrets-unready-item").within(() => {
      cy.getDataCy("session-secrets-unready-new-value-tab").click();
      cy.getDataCy("session-secrets-unready-new-value-input").type(secretValue);
      cy.getDataCy("session-secrets-unready-save-button").click();
    });

    // Verify the session starts
    cy.get("[data-cy=session-status-starting]", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");

    // Verify the session is shown
    cy.getDataCy("session-header").contains(sessionLauncherName);
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );

    // Verify the secret file exists and contains the correct value
    cy.url().then((url) => {
      executeInSession(url, `cat /secrets/${secretFilename}`).then((output) => {
        expect(output).to.contain(secretValue);
      });
    });
  });

  it("Session with two secrets where second one references the first", () => {
    const secretName = "original-secret";
    const secretFilename = `${secretName}.txt`;
    const secretValue = "original secret value";

    const copiedSecretName = "copied-secret";
    const copiedSecretFilename = `${copiedSecretName}.txt`;

    // Navigate to project settings to add first session secret slot
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-settings-session-secrets").scrollIntoView();
    cy.getDataCy("add-session-secret-button").click();

    // Add and save first session secret slot
    cy.getDataCy("add-session-secret-modal").should("be.visible");
    cy.getDataCy("add-session-secret-name-input").type(secretName);
    cy.getDataCy("add-session-secret-filename-input").type(secretFilename);
    cy.getDataCy("add-session-secret-submit-button").click();

    // Provide the secret value for first secret
    cy.getDataCy("provide-session-secret-mode-new-value-tab").click();
    cy.getDataCy("provide-session-secret-mode-new-value-input").type(
      secretValue,
    );
    cy.getDataCy("provide-session-secret-save-button").click();

    // Add second session secret slot
    cy.getDataCy("add-session-secret-button").click();
    cy.getDataCy("add-session-secret-modal").should("be.visible");
    cy.getDataCy("add-session-secret-name-input").type(copiedSecretName);
    cy.getDataCy("add-session-secret-filename-input").type(
      copiedSecretFilename,
    );
    cy.getDataCy("add-session-secret-submit-button").click();

    // Use the existing secret value for the second secret
    cy.getDataCy("provide-session-secret-mode-existing-tab").click();
    cy.getDataCy("session-secret-select-user-secret").click();
    cy.get('[role="option"]').contains(secretName).click();
    cy.getDataCy("provide-session-secret-use-secret-button").click();

    // Navigate to the project page and launch the session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Verify the session starts
    cy.get("[data-cy=session-status-starting]", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");

    // Verify the session is shown
    cy.getDataCy("session-header").contains(sessionLauncherName);
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );

    // Verify both secret files exist and contain the correct value
    cy.url().then((url) => {
      // Verify first secret file
      executeInSession(url, `cat /secrets/${secretFilename}`).then((output) => {
        expect(output).to.contain(secretValue);
      });

      // Verify second secret file has the same value as the first
      executeInSession(url, `cat /secrets/${copiedSecretFilename}`).then(
        (output) => {
          expect(output).to.contain(secretValue);
        },
      );
    });
  });

  it("Session won't start if session secret value is not provided", () => {
    const secretName = "missing-secret";
    const secretFilename = `${secretName}.txt`;

    // Create a session secret slot without a value
    createSessionSecretSlot(projectId, secretName, secretFilename);

    // Navigate back to the project page and launch the session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Verify the session secrets modal appears
    cy.getDataCy("session-secrets-modal").should("be.visible");
    cy.getDataCy("session-secrets-modal")
      .contains("Session secrets")
      .should("be.visible");

    // Verify the missing secret is shown
    cy.getDataCy("session-secrets-unready-item")
      .contains(secretName)
      .should("be.visible");
    cy.getDataCy("session-secrets-unready-item")
      .contains(secretFilename)
      .should("be.visible");

    // Click Cancel to abort the launch
    cy.getDataCy("session-secrets-modal-cancel-button").click();

    // Verify we're back to the project page
    cy.getDataCy("project-name").should("be.visible");
  });
});
