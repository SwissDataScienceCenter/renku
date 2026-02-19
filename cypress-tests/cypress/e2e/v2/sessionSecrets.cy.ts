import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { login } from "../../support/utils/general";

const sessionId = ["session-secrets", getRandomString()];

describe("Session Secrets", () => {
  let projectSlug: string;
  let projectId: string;

  beforeEach(() => {
    login(sessionId);

    projectSlug = `project-for-session-secrets-tests-${getRandomString()}`;

    // Create a project
    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        name: projectSlug,
        namespace: user.username,
        slug: projectSlug,
      }).then((response) => {
        projectId = response.body.id;

        // eslint-disable-next-line max-nested-callbacks
        cy.defer(() => {
          deleteProject(projectId);
        });
      });
    });
  });

  it("Copied secret displays reference to original secret in project settings", () => {
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

    // Verify the copied secret is displayed correctly in the project settings
    cy.getDataCy("session-secret-slot-item")
      .filter(`:contains("${copiedSecretName}")`)
      .within(() => {
        cy.contains(copiedSecretName).should("be.visible");
        // Verify "Secret saved" badge is shown
        cy.contains("Secret saved").should("be.visible");
        // Verify the reference to the original secret
        cy.contains("Secret name:").should("be.visible");
        cy.contains(secretName).should("be.visible");
        // Verify the location in sessions
        cy.contains("Location in sessions:").should("be.visible");
        cy.contains(`/secrets/${copiedSecretFilename}`).should("be.visible");
      });
  });

  it("Create and delete a secret", () => {
    const secretName = "test-secret";

    // Navigate to project settings to add a session secret slot
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-settings-session-secrets").scrollIntoView();
    cy.getDataCy("add-session-secret-button").click();

    // Add and save a session secret slot without providing a value
    cy.getDataCy("add-session-secret-modal").should("be.visible");
    cy.getDataCy("add-session-secret-name-input").type(secretName);
    cy.getDataCy("add-session-secret-filename-input").type("test-secret.txt");
    cy.getDataCy("add-session-secret-description-input").type("Test secret");
    cy.getDataCy("add-session-secret-submit-button").click();
    cy.getDataCy("provide-session-secret-close-button").click();

    // Open the secret's dropdown menu and click Remove
    cy.getDataCy("project-settings-session-secrets")
      .find("[data-cy=session-secret-slot-item]")
      .filter(`:contains("${secretName}")`)
      .find("[data-cy=session-secret-actions]")
      .find("[data-cy=button-with-menu-dropdown]")
      .click();
    cy.getDataCy("session-secret-remove-button").click();
    // Confirm deletion
    cy.getDataCy("delete-modal-button").click();

    // Verify the secret was deleted
    cy.getDataCy("project-settings-session-secrets")
      .contains(`[data-cy=session-secret-slot-item]`, secretName)
      .should("not.exist");
  });

  it("Create a secret with value and then clear it", () => {
    const secretName = "test-secret-with-value";
    const secretValue = "initial-secret-value";

    // Navigate to project settings to add a session secret slot
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-settings-session-secrets").scrollIntoView();
    cy.getDataCy("add-session-secret-button").click();

    // Add and save a session secret slot with a value
    cy.getDataCy("add-session-secret-name-input").type(secretName);
    cy.getDataCy("add-session-secret-filename-input").type("test-secret.txt");
    cy.getDataCy("add-session-secret-submit-button").click();
    cy.getDataCy("provide-session-secret-mode-new-value-tab").click();
    cy.getDataCy("provide-session-secret-mode-new-value-input").type(
      secretValue,
    );
    cy.getDataCy("provide-session-secret-save-button").click();

    // Verify the secret has a value (check for "Secret saved" badge)
    cy.getDataCy("project-settings-session-secrets")
      .find("[data-cy=session-secret-slot-item]")
      .filter(`:contains("${secretName}")`)
      .contains("Secret saved")
      .should("be.visible");

    // Open the secret's dropdown menu and click Clear secret
    cy.getDataCy("session-secret-slot-item")
      .filter(`:contains("${secretName}")`)
      .find("[data-cy=session-secret-actions]")
      .find("[data-cy=button-with-menu-dropdown]")
      .click();
    cy.getDataCy("session-secret-clear-button").click();
    // Confirm clearing
    cy.getDataCy("clear-session-secret-modal-button").click();

    // Verify "Secret not provided" badge is visible
    cy.getDataCy("project-settings-session-secrets")
      .find("[data-cy=session-secret-slot-item]")
      .filter(`:contains("${secretName}")`)
      .contains("Secret not provided")
      .should("be.visible");
  });

  it("Edit a secret and rename it", () => {
    const secretName = "original-secret-name";
    const newName = "renamed-secret-name";

    // Navigate to project settings to add a session secret slot
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-settings-session-secrets").scrollIntoView();
    cy.getDataCy("add-session-secret-button").click();

    // Add and save a session secret slot without providing a value
    cy.getDataCy("add-session-secret-modal").should("be.visible");
    cy.getDataCy("add-session-secret-name-input").type(secretName);
    cy.getDataCy("add-session-secret-filename-input").type("secret.txt");
    cy.getDataCy("add-session-secret-description-input").type("Test secret");
    cy.getDataCy("add-session-secret-submit-button").click();
    cy.getDataCy("provide-session-secret-close-button").click();

    // Open the secret's dropdown menu and click Edit
    cy.getDataCy("session-secret-slot-item")
      .filter(`:contains("${secretName}")`)
      .find("[data-cy=session-secret-actions]")
      .find("[data-cy=button-with-menu-dropdown]")
      .click();
    cy.getDataCy("session-secret-edit-button").click();

    // Rename the secret
    cy.getDataCy("add-session-secret-name-input")
      .should("have.value", secretName)
      .clear()
      .type(newName);
    cy.getDataCy("edit-session-secret-modal-button").click();

    // Verify the secret was renamed
    cy.getDataCy("session-secret-slot-item")
      .contains(newName)
      .should("be.visible");
    cy.getDataCy("session-secret-slot-item")
      .contains(secretName)
      .should("not.exist");
  });
});
