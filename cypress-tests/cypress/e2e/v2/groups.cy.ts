import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import {
  deleteGroupFromAPI,
  getGroupFromAPI,
} from "../../support/utils/group.utils";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
} from "../../support/utils/projectsV2.utils";
import { ProjectIdentifierV2 } from "../../support/types/project.types";
import { User } from "../../support/types/user.types";
import { verifySearchIndexing } from "../../support/utils/search.utils";

const sessionId = ["groups", getRandomString()];

describe("Group - create, edit and delete", () => {
  // Define some group details
  const groupDescription = "This is a test group from Cypress";
  let groupNameRandomPart;
  let groupName;
  let groupSlug;
  let userNamespace: string;

  function resetRequiredResources() {
    groupNameRandomPart = getRandomString();
    groupName = `group/$test-${groupNameRandomPart}`;
    groupSlug = `group-test-${groupNameRandomPart}`;
  }

  beforeEach(() => {
    // Restore the session (login)
    cy.session(
      sessionId,
      () => {
        cy.robustLogin("v2");
      },
      validateLoginV2,
    );

    getUserData().then((user: User) => {
      userNamespace = user.username;
    });

    // Define new group details to avoid conflicts on retries
    resetRequiredResources();
  });

  // Cleanup the group after the test -- useful on failure
  afterEach(() => {
    getGroupFromAPI(groupSlug).then((response) => {
      if (response.status === 200) {
        deleteGroupFromAPI(groupSlug);
      }
    });
  });

  it("Group - create, edit and delete", () => {
    // Create a new group
    cy.visit("/");
    cy.getDataCy("navbar-new-entity").click();
    cy.getDataCy("navbar-group-new").click();
    cy.getDataCy("group-creation-form").should("exist");
    cy.getDataCy("group-name-input").type(groupName);
    cy.getDataCy("group-slug-toggle").click();
    cy.getDataCy("group-slug-input").should("have.value", groupSlug);
    cy.getDataCy("group-description-input").type(groupDescription);
    cy.getDataCy("group-url-preview").contains(`/${groupSlug}`);
    cy.intercept("POST", /(?:\/ui-server)?\/api\/data\/groups/).as(
      "createGroup",
    );
    cy.getDataCy("group-create-button").click();
    cy.wait("@createGroup");
    cy.getDataCy("group-name").should("contain", groupName);

    // Change settings
    const modifiedGroupName = `${groupName} - modified`;
    const modifiedGroupDescription = `${groupDescription} - modified`;
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
    getGroupFromAPI(groupSlug).then((response) => {
      expect(response.status).to.equal(404);
    });
  });

  it("Search data connector and project in groups", () => {
    const name = `giab-data-connector-${groupNameRandomPart}`;
    const project1Name = `project-1-${groupNameRandomPart}`;
    const project2Name = `project-2-${groupNameRandomPart}`;

    const project1Identifier: ProjectIdentifierV2 = {
      slug: project1Name,
      id: null,
      namespace: groupSlug,
    };
    const project2Identifier: ProjectIdentifierV2 = {
      slug: project2Name,
      id: null,
      namespace: groupSlug,
    };

    // Create a new group
    cy.visit("/");
    cy.getDataCy("navbar-new-entity").click();
    cy.getDataCy("navbar-group-new").click();
    cy.getDataCy("group-creation-form").should("exist");
    cy.getDataCy("group-name-input").type(groupName);
    cy.getDataCy("group-slug-toggle").click();
    cy.getDataCy("group-slug-input").should("have.value", groupSlug);
    cy.getDataCy("group-description-input").type(groupDescription);
    cy.intercept("POST", /(?:\/ui-server)?\/api\/data\/groups/).as(
      "createGroup",
    );
    cy.getDataCy("group-create-button").click();
    cy.wait("@createGroup");
    cy.getDataCy("group-name").should("contain", groupName);

    // Create a data connector owned by the group
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("data-storage-s3").click();
    cy.getDataCy("data-provider-AWS").click();
    cy.getDataCy("data-connector-edit-next-button").click();
    cy.getDataCy("data-connector-source-path").should("be.empty").type("giab");
    cy.getDataCy("test-data-connector-button").click();
    cy.getDataCy("cloud-storage-connection-success").should("be.visible");
    cy.getDataCy("add-data-connector-continue-button").click();

    cy.getDataCy("data-connector-name-input").should("be.empty").type(name);
    cy.getDataCy("data-connector-slug-toggle").click();
    cy.getDataCy("data-connector-slug-input").should("have.value", name);
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Verify data connector appears in the group
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(name);

    // Create first project owned by the group
    createProjectIfMissingAPIV2({
      description: "First test project in group",
      name: project1Name,
      namespace: groupSlug,
      slug: project1Name,
      visibility: "private",
    }).then((project) => {
      project1Identifier.id = project.id;
    });

    // Create second project owned by the group
    createProjectIfMissingAPIV2({
      description: "Second test project in group",
      name: project2Name,
      namespace: groupSlug,
      slug: project2Name,
      visibility: "private",
    }).then((project) => {
      project2Identifier.id = project.id;
    });

    // Navigate to group page and verify projects appear
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Verify both projects are listed
    cy.contains(project1Name).should("be.visible");
    cy.contains(project2Name).should("be.visible");

    // Navigate to project1 and add a data connector
    cy.getDataCy("project-box")
      .find('[data-cy="dashboard-project-list"]')
      .contains(project1Name)
      .click();
    cy.getDataCy("project-name").should("contain", project1Name);

    // Create a data connector in project1
    const project1DataConnectorName = `project1-dc-${groupNameRandomPart}`;
    cy.getDataCy("add-data-connector").click();
    cy.getDataCy("project-data-controller-mode-create").click();
    cy.getDataCy("data-storage-s3").click();
    cy.getDataCy("data-provider-AWS").click();
    cy.getDataCy("data-connector-edit-next-button").click();

    cy.getDataCy("data-connector-source-path").should("be.empty").type("giab");
    cy.getDataCy("test-data-connector-button").click();
    cy.getDataCy("cloud-storage-connection-success").should("be.visible");
    cy.getDataCy("add-data-connector-continue-button").click();

    cy.getDataCy("data-connector-name-input")
      .should("be.empty")
      .type(project1DataConnectorName);
    cy.getDataCy("data-connector-slug-toggle").click();
    cy.getDataCy("data-connector-slug-input").should(
      "have.value",
      project1DataConnectorName,
    );
    cy.getDataCy("data-connector-edit-update-button").click();
    cy.getDataCy("data-connector-edit-success").should("be.visible");
    cy.getDataCy("data-connector-edit-close-button").click();

    // Verify data connector appears in project1
    cy.getDataCy("data-connector-box")
      .find(`[data-cy=data-connector-name]`)
      .contains(project1DataConnectorName);

    // Navigate back to group page
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    // Test group search functionality
    // Wait for search indexing to complete (group, 2 projects, 2 data connectors)
    verifySearchIndexing(groupNameRandomPart, 5);

    // Use the group search button
    cy.getDataCy("group-search-link").click();

    // By default, projects are shown - verify both projects are visible
    cy.contains(project1Name).should("be.visible");
    cy.contains(project2Name).should("be.visible");

    // Click on the Data Connectors filter to show data connectors
    cy.getDataCy("group-search-filter-type-DataConnector").last().click();

    // Verify data connectors are visible
    cy.contains(name).should("be.visible");
    cy.contains(project1DataConnectorName).should("be.visible");

    // Move project2 to user's namespace
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);

    cy.getDataCy("project-box")
      .find('[data-cy="dashboard-project-list"]')
      .contains(project2Name)
      .click();
    cy.getDataCy("project-name").should("contain", project2Name);

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
    verifySearchIndexing(groupNameRandomPart, 5);

    // Navigate back to group search page
    cy.visit("/");
    cy.getDataCy("groups-container")
      .find('[data-cy="dashboard-group-list"]')
      .contains(groupName)
      .click();
    cy.getDataCy("group-name").should("contain", groupName);
    cy.getDataCy("group-search-link").click();

    // Verify project2 no longer appears in group search
    cy.contains(project1Name).should("be.visible");
    cy.contains(project2Name).should("not.exist");

    // Cleanup: Delete projects
    deleteProjectFromAPIV2(project1Identifier);
    project2Identifier.namespace = userNamespace;
    deleteProjectFromAPIV2(project2Identifier);
  });
});
