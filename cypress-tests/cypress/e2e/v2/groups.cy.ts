import { getRandomString, getUserData } from "../../support/commands/general";
import {
  createGroupIfMissing,
  deleteGroup,
  getGroup,
} from "../../support/utils/groups";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProjectByNamespaceSlug,
} from "../../support/utils/projects";
import { verifySearchIndexing } from "../../support/utils/search";
import { createDataConnector } from "../../support/utils/dataConnectors";
import { login } from "../../support/utils/general";

const sessionId = ["groups", getRandomString()];

describe("Groups", () => {
  // Define some group details
  const groupDescription = "This is a test group from Cypress";
  let groupName: string;
  let groupSlug: string;
  let userNamespace: string;

  before(() => {
    login(sessionId);

    getUserData().then((user: User) => {
      userNamespace = user.username;
    });
  });

  beforeEach(() => {
    login(sessionId);

    const randomString = getRandomString();
    groupName = `Test Group ${randomString}`;
    groupSlug = `test-group-${randomString}`;
  });

  it("Create and delete a group from navbar", () => {
    // Create a new group from navbar
    cy.visit("/");
    cy.getDataCy("navbar-new-entity").click();
    cy.getDataCy("navbar-group-new").click();
    cy.getDataCy("group-creation-form").should("exist");
    cy.getDataCy("group-name-input").type(groupName);
    cy.getDataCy("group-slug-toggle").click();
    cy.getDataCy("group-slug-input").should("have.value", groupSlug);
    cy.getDataCy("group-description-input").type(groupDescription);
    cy.getDataCy("group-url-preview").contains(`/${groupSlug}`);
    cy.getDataCy("group-create-button").click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Verify group appears in user's groups list
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .should("be.visible");

    // Navigate back to the group
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Delete group
    cy.getDataCy("group-settings-link").click();
    cy.getDataCy("group-delete-button").click();
    cy.getDataCy("group-delete-confirm-button").should("not.be.enabled");
    cy.getDataCy("delete-confirmation-input").type(groupSlug);
    cy.intercept("DELETE", /(?:\/ui-server)?\/api\/data\/groups\/[^/]+/).as(
      "deleteGroups",
    );
    cy.getDataCy("group-delete-confirm-button").should("be.enabled").click();
    cy.wait("@deleteGroups");
    getGroup(groupSlug).then((response) => {
      expect(response.status).to.equal(404);
    });
  });

  // ? This is disabled cause the button "button-group-new" is not visible when there are no groups
  it.skip("Create and delete a group from user groups list", () => {
    // Create a new group from groups list
    cy.visit("/");
    cy.getDataCy("button-group-new").click();
    cy.getDataCy("group-creation-form").should("exist");
    cy.getDataCy("group-name-input").type(groupName);
    cy.getDataCy("group-slug-toggle").click();
    cy.getDataCy("group-slug-input").should("have.value", groupSlug);
    cy.getDataCy("group-description-input").type(groupDescription);
    cy.getDataCy("group-url-preview").contains(`/${groupSlug}`);
    cy.getDataCy("group-create-button").click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Verify group appears in user's groups list
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .should("be.visible");

    // Navigate back to the group
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Delete group
    cy.getDataCy("group-settings-link").click();
    cy.getDataCy("group-delete-button").click();
    cy.getDataCy("group-delete-confirm-button").should("not.be.enabled");
    cy.getDataCy("delete-confirmation-input").type(groupSlug);
    cy.intercept("DELETE", /(?:\/ui-server)?\/api\/data\/groups\/[^/]+/).as(
      "deleteGroups",
    );
    cy.getDataCy("group-delete-confirm-button").should("be.enabled").click();
    cy.wait("@deleteGroups");
    getGroup(groupSlug).then((response) => {
      expect(response.status).to.equal(404);
    });
  });

  describe("With an existing group", () => {
    beforeEach(() => {
      createGroupIfMissing({
        name: groupName,
        slug: groupSlug,
        description: groupDescription,
      });
    });

    afterEach(() => {
      deleteGroup(groupSlug);
    });

    it("Edit a group", () => {
      // Change settings
      const modifiedGroupName = `${groupName} - modified`;
      const modifiedGroupDescription = `${groupDescription} - modified`;

      cy.visit("/");
      cy.getDataCy("groups-container")
        .find('[data-cy="dashboard-group-list"]')
        .contains(groupName)
        .click();
      cy.getDataCy("group-name").should("contain", groupName);

      cy.getDataCy("group-settings-link").click();
      cy.getDataCy("group-name-input").should("have.value", groupName);
      cy.getDataCy("group-name-input").clear().type(modifiedGroupName);
      cy.getDataCy("group-description-input").should(
        "have.value",
        groupDescription,
      );
      cy.getDataCy("group-description-input")
        .clear()
        .type(modifiedGroupDescription);
      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/groups\/[^/]+/).as(
        "updateGroup",
      );
      cy.getDataCy("group-update-button").click();
      cy.wait("@updateGroup");
      cy.getDataCy("group-name").should("contain", modifiedGroupName);
      cy.getDataCy("group-description").should(
        "contain",
        modifiedGroupDescription,
      );
    });

    it("Search data connector and project in groups", () => {
      const randomString = getRandomString();
      const dataConnectorName = `group-data-connector-${randomString}`;
      const projectName = `project-${randomString}`;

      // Create a data connector owned by the group
      createDataConnector({
        namespace: groupSlug,
        slug: dataConnectorName,
      });

      // Create a project owned by the group
      createProjectIfMissingV2({
        description: "A test project in group",
        name: projectName,
        namespace: groupSlug,
        slug: projectName,
        visibility: "private",
      });

      // Create a data connector in project
      const projectDataConnectorName = `project-data-connector-${randomString}`;
      createDataConnector({
        namespace: groupSlug,
        slug: projectDataConnectorName,
      });

      // Navigate back to group page
      cy.visit("/");
      cy.getDataCy("groups-container")
        .find('[data-cy="dashboard-group-list"]')
        .contains(groupName)
        .click();
      cy.getDataCy("group-name").should("contain", groupName);

      // Test group search functionality
      // Wait for search indexing to complete (1 project, 2 data connectors)
      verifySearchIndexing(randomString, 3);

      // Use the group search button
      cy.getDataCy("group-search-link").click();

      // By default, projects are shown - verify the project is visible
      cy.contains(projectName).should("be.visible");

      // Click on the Data Connectors filter to show data connectors
      cy.getDataCy("search-filter-type-DataConnector").last().click();

      // Verify data connectors are visible
      cy.contains(dataConnectorName).should("be.visible");
      cy.contains(projectDataConnectorName).should("be.visible");

      // Move project to user's namespace
      cy.visit("/");
      cy.getDataCy("groups-container")
        .find('[data-cy="dashboard-group-list"]')
        .contains(groupName)
        .click();
      cy.getDataCy("group-name").should("contain", groupName);

      cy.getDataCy("project-box")
        .find('[data-cy="dashboard-project-list"]')
        .contains(projectName)
        .click();
      cy.getDataCy("project-name").should("contain", projectName);

      cy.getDataCy("project-settings-link").click();

      cy.getDataCy("project-settings-form-project-namespace-input").click();
      cy.get('[role="option"]')
        .filter(`:contains("${userNamespace}")`)
        .first()
        .click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject",
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");

      // Wait for search indexing to complete
      verifySearchIndexing(randomString, 3);

      // Navigate back to group search page
      cy.visit("/");
      cy.getDataCy("groups-container")
        .find('[data-cy="dashboard-group-list"]')
        .contains(groupName)
        .click();
      cy.getDataCy("group-name").should("contain", groupName);
      cy.getDataCy("group-search-link").click();

      // Verify the project no longer appears in group search
      cy.contains(projectName).should("not.exist");

      // Click on the Data Connectors filter to show data connectors
      cy.getDataCy("search-filter-type-DataConnector").last().click();

      // Verify the project's data connectors no longer appears in group search
      cy.contains(dataConnectorName).should("be.visible");

      // Cleanup
      deleteProjectByNamespaceSlug(userNamespace, projectName);
    });
  });
});
