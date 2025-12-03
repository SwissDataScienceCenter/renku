import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { createGroupIfMissing, deleteGroup } from "../../support/utils/groups";
import {
  createDataConnector,
  deleteDataConnector,
} from "../../support/utils/dataConnectors";
import { login } from "../../support/utils/general";

const sessionId = ["dataConnectors", getRandomString()];

describe("Data Connectors", () => {
  const randomString = getRandomString();
  const projectName = `Project data connector tests ${randomString}`;
  const projectSlug = `project-data-connector-tests-${randomString}`;
  let userNamespace: string;
  let dataConnectorName: string;
  let projectId: string;
  let groupName: string;
  let groupSlug: string;

  // Create a project and keep that around for the rest of the tests
  before(() => {
    login(sessionId);

    getUserData().then((user: User) => {
      userNamespace = user.username;

      createProjectIfMissingV2({
        description: "Test project for data connector tests",
        name: projectName,
        namespace: userNamespace,
        slug: projectSlug,
        visibility: "private",
      }).then((response) => {
        projectId = response.body.id;
      });
    });
  });

  after(() => {
    deleteProject(projectId);
  });

  beforeEach(() => {
    login(sessionId);

    const randomString = getRandomString();

    dataConnectorName = `data-connector-${randomString}`;

    groupName = `Group ${randomString}`;
    groupSlug = `group-${randomString}`;

    createGroupIfMissing({
      name: groupName,
      slug: groupSlug,
      description: "Test group",
    });
  });

  afterEach(() => {
    deleteDataConnector([
      `${userNamespace}/${dataConnectorName}`,
      `${userNamespace}/${projectSlug}/${dataConnectorName}`,
      `${groupSlug}/${dataConnectorName}`,
    ]);
    deleteGroup(groupSlug);
  });

  const visitCurrentProject = () => {
    cy.visitProjectByName(projectName);
  };

  it("Create and delete a data connector in a project", () => {
    const dataConnectorName = `data-connector-in-project-${getRandomString()}`;

    // Create a data connector from the project page
    visitCurrentProject();
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("project-data-controller-mode-create").click();
    cy.getDataCy("data-connector-edit-next-button").should("be.disabled");
    cy.getDataCy("data-storage-s3").click();
    cy.getDataCy("data-provider-AWS").click();
    cy.getDataCy("data-connector-edit-next-button").click();

    cy.getDataCy("data-connector-source-path").should("be.empty").type("giab");
    cy.getDataCy("test-data-connector-button").click();
    cy.getDataCy("cloud-storage-connection-success").should("be.visible");
    cy.getDataCy("add-data-connector-continue-button").click();

    cy.getDataCy("data-connector-name-input")
      .should("be.empty")
      .type(dataConnectorName);
    cy.getDataCy("data-connector-slug-toggle").click();
    cy.getDataCy("data-connector-slug-input").should(
      "have.value",
      dataConnectorName,
    );
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Navigate to the user's namespace page by clicking the namespace link
    // Note: You can only delete data connectors from the user's page, not
    // from a project page (where you can only unlink them)
    visitCurrentProject();
    cy.getDataCy("project-namespace-link").click();

    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();

    // Open the dropdown menu and click delete
    cy.getDataCy("data-connector-menu-dropdown")
      .should("be.visible")
      .scrollIntoView()
      .click();
    cy.getDataCy("data-connector-delete").should("be.visible").click();

    // Confirm deletion by typing the slug
    cy.getDataCy("delete-confirmation-input").type(dataConnectorName);
    cy.getDataCy("delete-data-connector-modal-button").click();

    // Verify the data connector is deleted
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .contains(`[data-cy=data-connector-name]`, dataConnectorName)
      .should("not.exist");
  });

  it("Create and delete a data connector in user's namespace", () => {
    const dataConnectorName = `data-connector-for-user-${getRandomString()}`;

    // Navigate to the user's namespace page
    cy.visit("/");
    cy.getDataCy("user-namespace-link").click();

    // Create a data connector from the user's page
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("data-storage-s3").click();
    cy.getDataCy("data-provider-AWS").click();
    cy.getDataCy("data-connector-edit-next-button").click();

    cy.getDataCy("data-connector-source-path").should("be.empty").type("giab");
    cy.getDataCy("test-data-connector-button").click();
    cy.getDataCy("cloud-storage-connection-success").should("be.visible");
    cy.getDataCy("add-data-connector-continue-button").click();

    cy.getDataCy("data-connector-name-input")
      .should("be.empty")
      .type(dataConnectorName);
    cy.getDataCy("data-connector-slug-toggle").click();
    cy.getDataCy("data-connector-slug-input").should(
      "have.value",
      dataConnectorName,
    );
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Verify the data connector appears in the user's page
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();

    // Delete the data connector
    cy.getDataCy("data-connector-menu-dropdown")
      .should("be.visible")
      .scrollIntoView()
      .click();
    cy.getDataCy("data-connector-delete").should("be.visible").click();

    // Confirm deletion by typing the slug
    cy.getDataCy("delete-confirmation-input").type(dataConnectorName);
    cy.getDataCy("delete-data-connector-modal-button").click();

    // Verify the data connector is deleted
    cy.getDataCy("data-connector-box")
      .contains(`[data-cy=data-connector-name]`, dataConnectorName)
      .should("not.exist");
  });

  it("Create and delete a data connector in a group", () => {
    const dataConnectorName = `data-connector-for-group-${getRandomString()}`;

    // Navigate to the group's page
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Create a data connector from the group's page
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("data-storage-s3").click();
    cy.getDataCy("data-provider-AWS").click();
    cy.getDataCy("data-connector-edit-next-button").click();

    cy.getDataCy("data-connector-source-path").should("be.empty").type("giab");
    cy.getDataCy("test-data-connector-button").click();
    cy.getDataCy("cloud-storage-connection-success").should("be.visible");
    cy.getDataCy("add-data-connector-continue-button").click();

    cy.getDataCy("data-connector-name-input")
      .should("be.empty")
      .type(dataConnectorName);
    cy.getDataCy("data-connector-slug-toggle").click();
    cy.getDataCy("data-connector-slug-input").should(
      "have.value",
      dataConnectorName,
    );
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Verify the data connector appears in the group's page
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();

    // Delete the data connector
    cy.getDataCy("data-connector-menu-dropdown")
      .should("be.visible")
      .scrollIntoView()
      .click();
    cy.getDataCy("data-connector-delete").should("be.visible").click();

    // Confirm deletion by typing the slug
    cy.getDataCy("delete-confirmation-input").type(dataConnectorName);
    cy.getDataCy("delete-data-connector-modal-button").click();

    // Verify the data connector is deleted
    cy.getDataCy("data-connector-box")
      .contains(`[data-cy=data-connector-name]`, dataConnectorName)
      .should("not.exist");
  });

  it("Edit a data connector", () => {
    // Create a data connector in the project
    createDataConnector(
      `${userNamespace}/${projectSlug}/${dataConnectorName}`,
      projectId,
    );

    // ? Currently, data connectors newly linked might not appear immediately
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();
    cy.getDataCy("data-connector-edit").click();

    // Edit the data connector
    const newName = `${dataConnectorName} edited`;

    cy.getDataCy("data-connector-name-input")
      .should("have.value", dataConnectorName)
      .clear()
      .type(newName);
    cy.getDataCy("data-connector-slug-toggle").click();
    cy.getDataCy("data-connector-slug-input").should(
      "have.value",
      dataConnectorName,
    );
    cy.getDataCy("data-connector-mount-input").should(
      "have.value",
      dataConnectorName,
    );
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Verify the data connector is present with the edited name
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(newName);
  });

  it("Link an existing data connector to a project", () => {
    // Create a data connector not linked to a project
    const dataConnectorIdentifier = `${userNamespace}/${dataConnectorName}`;
    createDataConnector(dataConnectorIdentifier);

    // Now link the data connector to the project
    visitCurrentProject();
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("project-data-controller-mode-link").click();

    // Enter the data connector identifier
    cy.get("#data-connector-identifier")
      .should("be.empty")
      .type(dataConnectorIdentifier);
    cy.getDataCy("link-data-connector-button").click();

    // Verify the data connector is linked to the project
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName);
  });

  it("Unlink a data connector from a project", () => {
    // Create a data connector not linked to a project
    const dataConnectorIdentifier = `${userNamespace}/${dataConnectorName}`;
    createDataConnector(dataConnectorIdentifier);

    // Now link the data connector to the project
    visitCurrentProject();
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("project-data-controller-mode-link").click();

    // Enter the data connector identifier
    cy.get("#data-connector-identifier")
      .should("be.empty")
      .type(dataConnectorIdentifier);
    cy.getDataCy("link-data-connector-button").click();

    // ? Currently, data connectors newly linked might not appear immediately
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();

    cy.getDataCy("data-connector-title")
      .should("be.visible")
      .contains(dataConnectorName);

    // Click the dropdown button to open the menu
    cy.getDataCy("data-connector-menu-dropdown").should("be.visible").click();
    cy.getDataCy("data-connector-unlink").should("be.visible").click();

    cy.getDataCy("delete-data-connector-modal-button").click();

    // Verify the data connector is no longer linked to the project
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .contains(`[data-cy=data-connector-name]`, dataConnectorName)
      .should("not.exist");
  });

  it("Link and unlink data connector from another project", () => {
    const otherProjectName = `other-project-${getRandomString()}`;

    // Create another project
    createProjectIfMissingV2({
      description: "Another project with data connector",
      name: otherProjectName,
      namespace: userNamespace,
      slug: otherProjectName,
      visibility: "private",
    }).then((response) => {
      const otherProjectId = response.body.id;

      // Defer-delete the other project (which will also delete the data connector)
      cy.defer(() => {
        deleteProject(otherProjectId);
      });

      // Create a data connector in the other project
      const dataConnectorIdentifier = `${userNamespace}/${otherProjectName}/${dataConnectorName}`;
      createDataConnector(dataConnectorIdentifier, otherProjectId);

      // Navigate to the main project
      visitCurrentProject();

      // Link the data connector from the other project to the main project
      cy.getDataCy("add-data-connector").click();
      cy.getDataCy("project-data-controller-mode-link").click();

      cy.get("#data-connector-identifier")
        .should("be.empty")
        .type(dataConnectorIdentifier);
      cy.getDataCy("link-data-connector-button").click();

      // Verify the data connector is linked to the main project
      visitCurrentProject();
      cy.getDataCy("data-connector-box")
        .find(`[data-cy=data-connector-name]`)
        .contains(dataConnectorName)
        .click();

      cy.getDataCy("data-connector-title")
        .should("be.visible")
        .contains(dataConnectorName);

      // Unlink the data connector from the main project
      cy.getDataCy("data-connector-menu-dropdown").should("be.visible").click();
      cy.getDataCy("data-connector-unlink").should("be.visible").click();
      cy.getDataCy("delete-data-connector-modal-button").click();

      // Verify the data connector is no longer linked to the main project
      visitCurrentProject();
      cy.getDataCy("data-connector-box")
        .contains(`[data-cy=data-connector-name]`, dataConnectorName)
        .should("not.exist");
    });
  });

  it("Transfer data connector from project to group", () => {
    // Create a data connector in the project
    createDataConnector(
      `${userNamespace}/${projectSlug}/${dataConnectorName}`,
      projectId,
    );

    // Navigate to the current project
    visitCurrentProject();

    // Verify data connector appears in the project
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();

    cy.getDataCy("data-connector-title")
      .should("be.visible")
      .contains(dataConnectorName);

    // Edit the data connector to transfer it to the group
    cy.getDataCy("data-connector-edit").click();

    // Change the owner to the group
    cy.getDataCy("data-controller-namespace-input").click();
    // NOTE: If there are many groups/projects in the dropdown, this might not find
    // the group since not all of them are loaded at once.
    cy.get('[role="option"]')
      .filter(`:contains("${groupSlug}")`)
      .first()
      .click();

    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Verify data connector is no longer in the project
    visitCurrentProject();
    cy.getDataCy("data-connector-box")
      .contains(`[data-cy=data-connector-name]`, dataConnectorName)
      .should("not.exist");

    // Navigate to the group
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Verify data connector appears in the group
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName);
  });

  it("Link and unlink group data connector to user project", () => {
    // Create a data connector owned by the group
    const dataConnectorIdentifier = `${groupSlug}/${dataConnectorName}`;
    createDataConnector(dataConnectorIdentifier);

    // Navigate to the user's project
    visitCurrentProject();

    // Link the group data connector to the user's project
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("project-data-controller-mode-link").click();

    // Enter the data connector identifier
    cy.get("#data-connector-identifier")
      .should("be.empty")
      .type(dataConnectorIdentifier);
    cy.getDataCy("link-data-connector-button").click();

    // Verify the data connector is linked to the project
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(dataConnectorName)
      .click();

    cy.getDataCy("data-connector-title")
      .should("be.visible")
      .contains(dataConnectorName);

    // Unlink the data connector from the project
    cy.getDataCy("data-connector-menu-dropdown").should("be.visible").click();
    cy.getDataCy("data-connector-unlink").should("be.visible").click();
    cy.getDataCy("delete-data-connector-modal-button").click();

    // Verify the data connector is no longer linked to the project
    cy.getDataCy("data-connector-box")
      .contains(`[data-cy=data-connector-name]`, dataConnectorName)
      .should("not.exist");
  });
});
