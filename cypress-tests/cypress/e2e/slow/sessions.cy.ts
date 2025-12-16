import { TIMEOUTS } from "../../../config";
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
} from "../../support/utils/sessions";
import { login } from "../../support/utils/general";
import { createDataConnector } from "../../support/utils/dataConnectors";
import { createCodeRepository } from "../../support/utils/codeRepositories";
import { Environment } from "../../support/types/sessions";

const sessionId = ["slow-sessions", getRandomString()];

describe("Slow Sessions", () => {
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
    projectSlug = `project-for-slow-session-tests-${randomString}`;
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

        // eslint-disable-next-line max-nested-callbacks
        cy.defer(() => {
          deleteSessionsForProject(projectId);
          deleteProject(projectId);
        });
      });
    });
  });

  it("Start a session with VSCode and check it has the necessary resources", () => {
    const dataConnectorSlug = `giab-${getRandomString()}`;
    const dataContainerFolder = "data_RNAseq";
    const codeRepositoryName = "renku-ui";
    const repositoryFolder = "client";
    const session = {
      arguments: ["/entrypoint.sh"],
      command: ["bash"],
      image: "renku/renkulab-vscodium-python-runimage:ubuntu-a841f8e",
      mount_directory: "/home/ubuntu/work",
      name: "vscode-launcher",
      url: "/",
      working_directory: "/home/ubuntu/work",
    };

    createDataConnector(
      { namespace: `${userNamespace}/${projectSlug}`, slug: dataConnectorSlug },
      projectId,
    );

    createCodeRepository(
      `https://github.com/SwissDataScienceCenter/${codeRepositoryName}.git`,
      projectId,
    );

    createSessionLauncher(projectId, session.name, {
      container_image: session.image,
      default_url: session.url,
      mount_directory: session.mount_directory,
      working_directory: session.working_directory,
      command: session.command,
      args: session.arguments,
    });

    // Start a pristine session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", session.name)
      .find("[data-cy=start-session-button]")
      .click();

    // Verify the session progress page is shown
    cy.get("[data-cy=session-status-starting]", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");

    // Verify the session is shown
    cy.getDataCy("session-header").contains(session.name);
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );

    // Check the project resources are available in the session.
    cy.getIframe("[data-cy=session-iframe]").within(() => {
      // ? Mind the following commands target the VSCode web interface where we have little control.
      function findAndExpandFolder(name: string, depth: number) {
        cy.contains(`[role=treeitem][aria-level="${depth}"]`, name).then(
          ($el) => {
            if ($el.attr("aria-expanded") === "false") {
              // ? Forcing clicks is a bad practice but VScode has unpredictable behavior for its popups.
              // eslint-disable-next-line cypress/no-force
              cy.wrap($el).click({ force: true });
            }
          },
        );
      }
      cy.get("#workbench\\.view\\.explorer", { timeout: TIMEOUTS.long }).within(
        () => {
          // Check the repository content
          findAndExpandFolder(codeRepositoryName, 1);
          cy.get(
            `[role="treeitem"][aria-level="2"][aria-label="${repositoryFolder}"]`,
          ).should("exist");

          // Check the S3 bucket content
          findAndExpandFolder(dataConnectorSlug, 1);
          cy.get(
            `[role="treeitem"][aria-level="2"][aria-label="${dataContainerFolder}"]`,
          ).should("exist");
        },
      );
    });
  });

  it("Start a session, pause it, and resume it", () => {
    // Start a session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Wait for session to be running
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );

    // Pause the session
    cy.getDataCy("pause-session-button").should("be.visible").click();
    cy.getDataCy("pause-session-modal-button").should("be.visible").click();

    // Wait for session to be paused and then resume it
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=resume-session-button]", { timeout: TIMEOUTS.long })
      .should("be.visible")
      .click();

    // Verify session is running again
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );
  });

  it("Start a session, pause it, change resource class, and resume it", () => {
    // Start a session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Wait for session to be running
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );

    // Pause the session from the project page
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=pause-session-button]", { timeout: TIMEOUTS.long })
      .should("be.visible")
      .click();

    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=resume-session-button]", { timeout: TIMEOUTS.long })
      .should("be.visible");

    // Click on the paused session to open launcher properties
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .click();

    // Change resource class from launcher properties
    cy.getDataCy("session-view-resource-class-edit-button").click();
    cy.getDataCy("set-resource-class-header").should("be.visible");
    cy.get("#addSessionResourceClass").click();
    cy.get('[role="option"]').contains("medium").filter(":visible").click();
    cy.getDataCy("set-resource-class-submit-button")
      .should("not.be.disabled")
      .click();
    cy.getDataCy("set-resource-class-cancel-button").click();

    // Resume the session with new resource class
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=resume-session-button]")
      .should("be.visible")
      .click();

    // Verify session is running again
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vvlong }).should(
      "be.visible",
    );
  });

  it("Start a session and delete it while starting", () => {
    // Start a session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=start-session-button]")
      .click();

    // Wait a bit for the session to start launching
    cy.get("[data-cy=session-status-starting]", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");

    // Go back to project page and delete the session
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .find("[data-cy=session-item]")
      .find("[data-cy=button-with-menu-dropdown]")
      .click();
    cy.getDataCy("delete-session-button").click();
    cy.getDataCy("delete-session-modal-button").click();

    // Verify session is deleted
    cy.visitProjectByName(projectSlug);
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", sessionLauncherName)
      .should("not.exist");
  });
});
