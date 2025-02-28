import { TIMEOUTS } from "../../../config";
import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import { ProjectIdentifierV2 } from "../../support/types/project.types";
import { User } from "../../support/types/user.types";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
} from "../../support/utils/projectsV2.utils";

const sessionId = ["sessionBasics", getRandomString()];

describe("Start a session that consumes project resources", () => {
  // Define required resources
  let projectNameRandomPart: string;
  let projectName;
  let projectIdentifier: ProjectIdentifierV2;

  function resetRequiredResources() {
    projectNameRandomPart = getRandomString();
    projectName = `session-basics-${projectNameRandomPart}`;
    projectIdentifier = {
      slug: projectName,
      id: null,
      namespace: null,
    };
  }

  beforeEach(() => {
    // Restore the session (login)
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLoginV2,
    );

    // Create a project
    resetRequiredResources();
    getUserData().then((user: User) => {
      projectIdentifier.namespace = user.username;
      createProjectIfMissingAPIV2({
        name: projectName,
        namespace: user.username,
        slug: projectName,
      }).then((project) => (projectIdentifier.id = project.id));
    });
  });

  // Cleanup the project after the test
  after(() => {
    getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
      if (response.status === 200) {
        projectIdentifier.id = response.body.id;
        projectIdentifier.namespace = response.body.namespace;
        deleteProjectFromAPIV2(projectIdentifier);
      }
    });
  });

  it("Start a session with VSCode and check it has the necessary resources", () => {
    const dataConnectorName = `giab-${getRandomString()}`;
    const dataContainerFolder = "data_RNAseq";
    const repositoryName = "renku-ui";
    const repositoryFolder = "client";
    const session = {
      arguments: '["/entrypoint.sh"]',
      command: '["bash"]',
      image: "renku/renkulab-vscodium-python-runimage:ubuntu-59fcea4",
      mountdir: "/home/ubuntu/work",
      name: "vscode-launcher",
      url: "/",
      workdir: "/home/ubuntu/work",
    };

    // Access the project and create some resources.
    cy.visit("/v2");
    cy.getDataCy("dashboard-project-list")
      .get(
        `a[href*="/${projectIdentifier.namespace}/${projectIdentifier.slug}"]`,
      )
      .click();
    cy.getDataCy("project-name").should("contain", projectName);

    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("project-data-controller-mode-create").click();
    cy.getDataCy("data-storage-s3").click();
    cy.getDataCy("data-provider-AWS").click();
    cy.getDataCy("data-connector-edit-next-button").click();
    cy.getDataCy("data-connector-source-path").clear().type("giab");
    cy.getDataCy("test-data-connector-button").click();
    cy.getDataCy("add-data-connector-continue-button").click();
    cy.getDataCy("data-connector-name-input").clear().type(dataConnectorName);
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();
    // ? data connectors don't refresh properly yet so we need to reload the page
    cy.reload();
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName);

    cy.getDataCy("add-code-repository").click();
    cy.getDataCy("project-add-repository-url")
      .clear()
      .type("https://github.com/SwissDataScienceCenter/renku-ui.git");
    cy.getDataCy("add-code-repository-modal-button").click();
    cy.getDataCy("code-repositories-box")
      .find("[data-cy=code-repository-item]")
      .contains(repositoryName);
    cy.contains("[data-cy=code-repository-item]", repositoryName).contains(
      "Pull only",
    );

    cy.getDataCy("add-session-launcher").click();
    cy.getDataCy("existing-custom-button").click();
    cy.getDataCy("custom-image-input").clear().type(session.image);
    cy.getDataCy("environment-advanced-settings-toggle").click();
    cy.getDataCy("session-launcher-field-default_url")
      .clear()
      .type(session.url);
    cy.getDataCy("session-launcher-field-mount_directory")
      .clear()
      .type(session.mountdir);
    cy.getDataCy("session-launcher-field-working_directory")
      .clear()
      .type(session.workdir);
    cy.getDataCy("session-launcher-field-command")
      .clear()
      .type(session.command);
    cy.getDataCy("session-launcher-field-args")
      .clear()
      .type(session.arguments, { parseSpecialCharSequences: false });
    cy.getDataCy("next-session-button").click();
    cy.getDataCy("launcher-name-input").clear().type(session.name);
    cy.getDataCy("add-session-button").click();
    cy.getDataCy("session-launcher-creation-success").should("be.visible");
    cy.getDataCy("close-cancel-button").click();

    // Start a pristine session
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", session.name)
      .then(($el) => {
        const $resume = $el.find("[data-cy=resume-session-button]");
        const $open = $el.find("[data-cy=open-session]");
        if ($resume.length > 0 || $open.length > 0) {
          cy.wrap($el).find("[data-cy=button-with-menu-dropdown]").click();
          cy.wrap($el).find("[data-cy=delete-session-button]").click();
          cy.getDataCy("delete-session-modal-button").click();
        }
      });
    cy.getDataCy("sessions-box")
      .contains("[data-cy=session-launcher-item]", session.name)
      .find("[data-cy=start-session-button]", {
        timeout: TIMEOUTS.long,
      })
      .click();
    cy.getDataCy("session-status-starting");
    cy.get("[data-cy=session-status-starting]", {
      timeout: TIMEOUTS.long,
    }).should("be.visible");
    cy.getDataCy("session-header").contains(session.name);
    cy.get("[data-cy=session-iframe]", { timeout: TIMEOUTS.vlong }).should(
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
          findAndExpandFolder(repositoryName, 1);
          cy.get(
            `[role="treeitem"][aria-level="2"][aria-label="${repositoryFolder}"]`,
          ).should("exist");

          // Check the S3 bucket content
          findAndExpandFolder(dataConnectorName, 1);
          cy.get(
            `[role="treeitem"][aria-level="2"][aria-label="${dataContainerFolder}"]`,
          ).should("exist");
        },
      );
    });
  });
});
