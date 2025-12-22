import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import {
  createSessionLauncher,
  deleteSessionsForProject,
  getEnvironmentByName,
  setSessionLauncherEnvironment,
} from "../../support/utils/sessions";
import { login } from "../../support/utils/general";
import { Environment } from "../../support/types/sessions";
import { TIMEOUTS } from "../../../config";

const sessionId = ["sessions", getRandomString()];

describe("Session tests with an existing session launcher", () => {
  const projectName = `project-for-session-tests-${getRandomString()}`;
  let projectId: string;
  let sessionLauncherId: string;
  let sessionLauncherName: string;
  const sessionLauncherImage = "alpine:latest";

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
    createSessionLauncher(projectId, sessionLauncherName, {
      container_image: sessionLauncherImage,
    }).then((response) => {
      sessionLauncherId = response.body.id;
    });
  });

  after(() => {
    // Delete all project's sessions
    deleteSessionsForProject(projectId);
    deleteProject(projectId);
  });

  it("An error is shown when launching with non-existing image", () => {
    // Update the session launcher to use a non-existing image
    setSessionLauncherEnvironment(sessionLauncherId, {
      container_image: "non-existent-image:non-existing-tag",
    });

    // Verify "Image inaccessible" message is displayed
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("contain", "Image inaccessible");

    // Click the dropdown button to show launch options
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=button-with-menu-dropdown]")
      .click();

    // Click "Force launch" option for that session launcher
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .should("be.visible")
      .click();

    // Verify error dialog appears
    cy.getDataCy("session-image-not-accessible-header").should("be.visible");

    // Click Cancel button to close the dialog
    cy.getDataCy("session-image-not-accessible-cancel-button").click();

    // Verify we're back to the project page
    cy.getDataCy("project-name").contains(projectName).should("be.visible");

    // Set the correct image
    setSessionLauncherEnvironment(sessionLauncherId, {
      container_image: sessionLauncherImage,
    });

    // Verify "Image inaccessible" message is not displayed
    cy.visitProjectByName(projectName);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("not.contain", "Image inaccessible");
  });

  it("Can launch a session with a global Python environment", () => {
    // Set the session to use a Python global environment
    getEnvironmentByName("python").then((environment: Environment) => {
      setSessionLauncherEnvironment(sessionLauncherId, {
        id: environment.id,
      });

      // Find the session launcher and click the launch button
      cy.visitProjectByName(projectName);
      cy.getDataCy("sessions-box")
        .contains("[data-cy=session-launcher-item]", sessionLauncherName)
        .find("[data-cy=start-session-button]")
        .click();

      // Verify the session progress page is shown
      cy.get("[data-cy=session-status-starting]", {
        timeout: TIMEOUTS.long,
      }).should("be.visible");

      // Verify the session is shown
      cy.getDataCy("session-header").contains(sessionLauncherName);
      cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
        "be.visible",
      );
    });
  });
});
