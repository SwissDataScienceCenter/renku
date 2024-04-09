import { TIMEOUTS } from "../../config";
import { generatorProjectName } from "../support/commands/projects";
import { validateLogin, getRandomString } from "../support/commands/general";
import { v4 as uuidv4 } from "uuid";

const username = Cypress.env("TEST_USERNAME");

const projectTestConfig = {
  shouldCreateProject: true,
  projectName: generatorProjectName("useSession"),
};

// ? Modify the config -- useful for debugging
// projectTestConfig.shouldCreateProject = false;
// projectTestConfig.projectName = "cypress-usesession-a8c6823e40ff";

const projectIdentifier = {
  name: projectTestConfig.projectName,
  namespace: username,
};

const projectWithoutPermissions = {
  namespace: "renku-ui-tests",
  name: "stable-project",
};

const sessionId = ["useSession", getRandomString(8)];

describe("Basic public project functionality", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin
    );

    // Create a project
    if (projectTestConfig.shouldCreateProject) {
      cy.visit("/");
      cy.createProject({ templateName: "Python", ...projectIdentifier });
    }
  });

  after(() => {
    if (projectTestConfig.shouldCreateProject)
      cy.deleteProjectFromAPI(projectIdentifier);
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
  });

  it("Start a new session on the project and interact with the terminal.", () => {
    cy.stopAllSessionsForProject(projectIdentifier);

    // Define workflow object
    const workflowNameSalt = uuidv4().substring(0, 4);
    const workflow = {
      name: `dummyworkflow-${workflowNameSalt}`,
      output: `o${workflowNameSalt}.txt`, // ? Keep the name short or it won't show up entirely in the file browser
    };

    // Start a session with options
    let serversInvoked = false;
    cy.intercept("/ui-server/api/notebooks/servers*", (req) => {
      serversInvoked = true;
    }).as("getServers");
    cy.getProjectSection("Sessions").click();
    if (serversInvoked) cy.wait("@getServers");
    cy.getDataCy("more-menu").should("be.visible").click();
    cy.getProjectPageLink(projectIdentifier, "sessions/new")
      .should("be.visible")
      .first()
      .click();

    // Wait for the image to be ready and start a session
    cy.get(".renku-container")
      .contains("A session gives you an environment")
      .should("exist");
    cy.waitForImageToBuild();
    cy.get(".renku-container button.btn-secondary", { timeout: TIMEOUTS.long })
      .contains("Start Session")
      .should("exist")
      .click();
    cy.get(".progress-box .progress-title").should("exist"); //.contains("Step 2 of 2");
    cy.get("button")
      .contains(projectTestConfig.projectName)
      .should("be.visible");
    cy.get(".progress-box .progress-title")
      .contains("Starting Session")
      .should("exist");
    cy.get(".progress-box .progress-title", { timeout: TIMEOUTS.vlong }).should(
      "not.exist"
    );

    // Verify the "Connect" button works as well
    cy.get(".fullscreen-back-button")
      .contains("Back")
      .should("be.visible")
      .click();
    cy.getDataCy("open-session").first().should("be.visible").click();
    cy.get(".progress-box .progress-title")
      .contains("Starting Session")
      .should("be.visible");

    // Run a simple workflow in the iframe
    cy.getIframe("iframe#session-iframe").within(() => {
      // Open the terminal and check the repo is not ahead
      cy.get(".jp-Launcher-content", { timeout: TIMEOUTS.long }).should(
        "be.visible"
      );
      cy.get(".jp-Launcher-section").should("be.visible");
      cy.get('.jp-LauncherCard[title="Start a new terminal session"]')
        .should("be.visible")
        .click();

      // Run a dummy workflow
      cy.get(".xterm-helper-textarea")
        .click()
        .type(
          `renku run --name ${workflow.name} echo "123" > ${workflow.output}{enter}`
        );
      cy.get("#filebrowser")
        .should("be.visible")
        .contains(workflow.output)
        .should("be.visible");
    });

    // Save the changes
    cy.getDataCy("save-session-button").should("be.visible").click();
    cy.get(".modal").contains("1 commit will be pushed").should("be.visible");
    cy.getDataCy("save-session-modal-button").should("be.visible").click();
    cy.get(".modal")
      .contains("Saving Session", { timeout: TIMEOUTS.long })
      .should("be.visible");
    cy.get(".modal")
      .contains("Your session has been saved successfully", {
        timeout: TIMEOUTS.long,
      })
      .should("be.visible");
    cy.get(".modal .btn-close").should("be.visible").click();

    // Pause the session
    cy.pauseSession();

    // Stop the session and check the project has been indexed
    cy.deleteSession();
    cy.waitMetadataIndexing();

    // Go the workflows page and check the new workflow appears
    cy.getProjectSection("Workflows").click();
    cy.getDataCy("workflows-browser")
      .should("be.visible")
      .children()
      .should("have.length", 1)
      .contains(workflow.name)
      .should("be.visible")
      .click();
    cy.getDataCy("workflow-details")
      .should("be.visible")
      .contains(`echo 123 > ${workflow.output}`)
      .should("be.visible");

    // Go the file page and check the lineage exists
    cy.getProjectSection("Files").click();
    cy.get("div.tree-container")
      .contains("button", "Lineage")
      .should("be.visible")
      .click();
    cy.get("#tree-content").contains(workflow.output).should("exist").click();
    cy.get(".graphContainer").contains(workflow.output).should("exist");
  });

  it("Start a new session as anonymous user.", () => {
    // Log out and go to the project again
    cy.visit("/");
    cy.logout();
    cy.visitAndLoadProject(projectIdentifier);

    // Check we show the appropriate message
    cy.getProjectSection("Sessions").click();
    cy.getDataCy("more-menu").first().should("be.visible").click();
    cy.getProjectPageLink(projectIdentifier, "sessions/new")
      .should("be.visible")
      .first()
      .click();
    cy.get(".alert-info").contains("As an anonymous user").should("be.visible");
    cy.waitForImageToBuild();

    // Quickstart a session and check it spins up
    cy.getDataCy("go-back-button").click();
    cy.quickstartSession();

    // Stop the session -- mind that anonymous users cannot pause sessions
    cy.deleteSession({ fromSessionPage: true });
  });

  it("Start a new session on a project without permissions.", () => {
    cy.stopAllSessionsForProject(projectWithoutPermissions);

    // Check we show the appropriate message
    cy.getProjectSection("Sessions").click();
    cy.getDataCy("more-menu").first().should("be.visible").click();
    cy.getProjectPageLink(projectWithoutPermissions, "sessions/new")
      .should("be.visible")
      .first()
      .click();
    cy.get(".alert-info")
      .contains("You have limited permissions for this project")
      .should("be.visible");
    cy.waitForImageToBuild();

    // Quickstart a session and check it spins up
    cy.getDataCy("go-back-button").click();
    cy.quickstartSession();

    // Pause, then delete the session
    cy.pauseSession();
    cy.deleteSession();
  });

  it("Start a new session with cloud storage attached.", () => {
    // verify that cloud storage is supported in the deployment
    cy.request({ url: "ui-server/api/notebooks/version" }).then((resp) => {
      if (!resp.body.versions[0].data.cloudstorageEnabled) {
        cy.log(
          "Skipping the test since the deployment doesn't support Cloud Storage"
        );
        return;
      }

      cy.stopAllSessionsForProject(projectIdentifier);
      cy.intercept("/ui-server/api/data/storage*").as("getProjectCloudStorage");

      cy.getProjectSection("Settings").click();
      cy.getDataCy("settings-navbar")
        .contains("a.nav-link", "Cloud Storage")
        .should("be.visible")
        .click();

      // Add a S3 storage configuration if it doesn't exist
      cy.wait("@getProjectCloudStorage").then(({ response }) => {
        const storages = response.body as { storage: { name: string } }[];
        if (storages.find(({ storage }) => storage.name === "data_s3")) {
          return;
        }

        cy.getDataCy("cloud-storage-section")
          .find("button")
          .contains("Add Cloud Storage")
          .should("be.visible")
          .click();
        cy.getDataCy("cloud-storage-edit-header")
          .contains("Add Cloud Storage")
          .should("be.visible");

        cy.getDataCy("cloud-storage-edit-schema")
          .contains("s3")
          .should("be.visible")
          .click();
        cy.getDataCy("cloud-storage-edit-providers")
          .contains("AWS")
          .should("be.visible")
          .click();
        cy.getDataCy("cloud-storage-edit-next-button")
          .should("be.visible")
          .click();

        cy.getDataCy("cloud-storage-edit-options").should("be.visible");
        cy.get("#sourcePath").should("have.value", "").type("giab");
        cy.get("#endpoint")
          .should("have.value", "")
          .type("http://s3.amazonaws.com");
        cy.getDataCy("cloud-storage-edit-next-button")
          .should("be.visible")
          .click();

        cy.getDataCy("cloud-storage-edit-mount").should("be.visible");
        cy.get("#name").should("have.value", "").type("data_s3");
        cy.get("#mountPoint")
          .should("have.value", "external_storage/data_s3")
          .type("{selectAll}data_s3");
        cy.get("#readOnly").should("not.be.checked").check();

        cy.getDataCy("cloud-storage-edit-update-button")
          .should("be.visible")
          .contains("Add")
          .click();

        cy.getDataCy("cloud-storage-edit-body").contains(
          "storage data_s3 has been succesfully added"
        );
        cy.getDataCy("cloud-storage-edit-close-button")
          .should("be.visible")
          .click();
      });

      cy.getDataCy("more-menu").should("be.visible").click();
      cy.getProjectPageLink(projectIdentifier, "sessions/new")
        .should("be.visible")
        .first()
        .click();

      // Wait for the image to be ready and start a session
      cy.get(".renku-container")
        .contains("A session gives you an environment")
        .should("exist");
      cy.waitForImageToBuild();
      cy.getDataCy("cloud-storage-item").contains("data_s3").should("exist");
      cy.get("#cloud-storage-data_s3-active").should("be.checked");
      cy.get(".renku-container button.btn-secondary", {
        timeout: TIMEOUTS.long,
      })
        .contains("Start Session")
        .should("exist")
        .click();
      cy.get(".progress-box .progress-title").should("exist"); //.contains("Step 2 of 2");
      cy.get("button")
        .contains(projectTestConfig.projectName)
        .should("be.visible");
      cy.get(".progress-box .progress-title")
        .contains("Starting Session")
        .should("exist");
      cy.get(".progress-box .progress-title", {
        timeout: TIMEOUTS.vlong,
      }).should("not.exist");

      // Verify that the S3 data is mounted
      cy.getIframe("iframe#session-iframe").within(() => {
        cy.get(".jp-DirListing-content", { timeout: TIMEOUTS.long }).should(
          "be.visible"
        );
        cy.get(".jp-DirListing-item")
          .contains("data_s3")
          .should("be.visible")
          .dblclick();

        cy.get(".jp-DirListing-item")
          .contains("README.s3_structure")
          .should("be.visible")
          .dblclick();

        cy.get(".jp-FileEditor", { timeout: TIMEOUTS.long }).should(
          "be.visible"
        );
        cy.get(".jp-FileEditor")
          .contains("The GIAB s3 bucket and URLs")
          .should("be.visible");
      });

      cy.pauseSession();
      cy.deleteSession();
    });
  });
});
