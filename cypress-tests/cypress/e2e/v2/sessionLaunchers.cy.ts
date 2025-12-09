import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import {
  createSessionLauncher,
  deleteSessionLauncher,
  deleteSessionsForProject,
} from "../../support/utils/sessions";
import { login } from "../../support/utils/general";

const sessionId = ["sessions", getRandomString()];

describe("Session launcher tests", () => {
  const projectName = `project-session-tests-${getRandomString()}`;
  let projectId: string;
  const sessionImage = "alpine:latest";
  const sessionUrl = "/test";
  let sessionLauncherName: string;

  before(() => {
    login(sessionId);

    // Create a project
    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        name: projectName,
        namespace: user.username,
        slug: projectName,
      }).then((response) => {
        projectId = response.body.id;
      });
    });
  });

  beforeEach(() => {
    login(sessionId);
    sessionLauncherName = `session-launcher-${getRandomString()}`;
  });

  after(() => {
    // Delete all project's sessions
    deleteSessionsForProject(projectId);
    deleteProject(projectId);
  });

  it("Add and delete a session launcher in the project's sessions list", () => {
    // Add session environment
    cy.visitProjectByName(projectName);
    cy.getDataCy("add-session-launcher").click();
    cy.getDataCy("environment-kind-custom").click();
    cy.getDataCy("custom-image-input").should("be.empty").type(sessionImage);
    cy.getDataCy("session-launcher-field-default_url").clear().type(sessionUrl);
    cy.getDataCy("next-session-button").click();
    cy.getDataCy("launcher-name-input")
      .should("be.empty")
      .type(sessionLauncherName);
    cy.getDataCy("add-session-button").click();
    cy.getDataCy("session-launcher-creation-success").should("be.visible");
    cy.getDataCy("close-cancel-button").click();

    // Verify session launcher was created
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("be.visible");

    // Delete session launcher from the list
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=button-with-menu-dropdown]")
      .click();
    cy.getDataCy("session-launcher-menu-delete").click();
    cy.getDataCy("delete-session-launcher-title").should("be.visible");
    cy.getDataCy("delete-session-launcher-button").click();

    // Verify session launcher was deleted
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("not.exist");
  });

  it("And and delete a session launcher in properties view", () => {
    // Add session environment
    cy.visitProjectByName(projectName);
    cy.getDataCy("add-session-launcher").click();
    cy.getDataCy("environment-kind-custom").click();
    cy.getDataCy("custom-image-input").should("be.empty").type(sessionImage);
    cy.getDataCy("session-launcher-field-default_url").clear().type(sessionUrl);
    cy.getDataCy("next-session-button").click();
    cy.getDataCy("launcher-name-input")
      .should("be.empty")
      .type(sessionLauncherName);
    cy.getDataCy("add-session-button").click();
    cy.getDataCy("session-launcher-creation-success").should("be.visible");
    cy.getDataCy("close-cancel-button").click();

    // Click on the session launcher to open the properties view
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("be.visible")
      .click();

    // Delete session launcher from the properties view
    cy.getDataCy("session-view-title").should("be.visible");
    cy.getDataCy("session-launcher-menu-dropdown").click();
    cy.getDataCy("session-view-menu-delete").click();
    cy.getDataCy("delete-session-launcher-title").should("be.visible");
    cy.getDataCy("delete-session-launcher-button").click();

    // Verify session launcher is deleted
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("not.exist");
  });

  describe("With an existing session launcher", () => {
    let sessionLauncherId: string;

    beforeEach(() => {
      createSessionLauncher(projectId, sessionLauncherName, {
        container_image: sessionImage,
        default_url: sessionUrl,
      }).then((response) => {
        sessionLauncherId = response.body.id;
      });
    });

    afterEach(() => {
      deleteSessionLauncher(sessionLauncherId);
    });

    it("Edit session launcher", () => {
      const newName = `${sessionLauncherName} edited`;

      // Click on the session launcher to open the properties view
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box")
        .contains("[data-cy=session-launcher-item]", sessionLauncherName)
        .click();

      // Edit session launcher
      cy.getDataCy("session-view-menu-edit").click();
      cy.getDataCy("edit-session-name")
        .should("have.value", sessionLauncherName)
        .clear()
        .type(newName);
      cy.getDataCy("edit-session-button").click();
      cy.getDataCy("session-launcher-update-success").should("be.visible");
      cy.getDataCy("close-cancel-button").click();

      // Verify session launcher is edited
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box").contains(
        "[data-cy=session-launcher-item]",
        newName,
      );
    });

    it("Edit session launcher container image", () => {
      const newImage = "ubuntu:latest";

      // Click on the session launcher to open the properties view
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box")
        .contains("[data-cy=session-launcher-item]", sessionLauncherName)
        .click();

      // Click modify session environment button
      cy.getDataCy("session-view-modify-session-environment-button").click();

      // Clear and update the container image
      cy.getDataCy("custom-image-input")
        .should("have.value", sessionImage)
        .clear()
        .type(newImage);

      cy.getDataCy("edit-session-button").click();
      cy.getDataCy("session-launcher-update-success").should("be.visible");
      cy.getDataCy("close-cancel-button").click();

      // Verify the image was updated in the properties view
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box")
        .contains("[data-cy=session-launcher-item]", sessionLauncherName)
        .click();

      // Verify the container image is displayed correctly
      cy.getDataCy("session-view-session-environment-image").should(
        "contain",
        newImage,
      );
    });

    it("Edit session launcher resource class", () => {
      // Click on the session launcher to open the properties view
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box")
        .contains("[data-cy=session-launcher-item]", sessionLauncherName)
        .click();

      // Open edit resource class dialog
      cy.getDataCy("session-view-title").should("be.visible");
      cy.getDataCy("session-view-resource-class-edit-button").click();

      // Wait for the dialog to open
      cy.getDataCy("set-resource-class-header").should("be.visible");

      // Open the resource class dropdown
      cy.get("#addSessionResourceClass").click();

      // Select the "medium" resource class option
      cy.get('[role="option"]').contains("medium").filter(":visible").click();

      // Modify resource class button should be enabled
      cy.getDataCy("set-resource-class-submit-button")
        .should("not.be.disabled")
        .click();

      // Verify success message appears
      cy.getDataCy("set-resource-class-success-alert").should("be.visible");

      // Close the dialog
      cy.getDataCy("set-resource-class-cancel-button").click();

      // Click on the session launcher to open the properties view
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box")
        .contains("[data-cy=session-launcher-item]", sessionLauncherName)
        .click();

      // Scroll to the Default Resource Class section
      cy.getDataCy("session-view-resource-class-heading")
        .scrollIntoView()
        .should("be.visible");

      // Verify the resource class was changed in the session launcher view
      cy.getDataCy("session-view-resource-class-description")
        .should("be.visible")
        .and("contain", "medium class from default pool");
    });
  });
});
