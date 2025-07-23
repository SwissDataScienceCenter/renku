import {
  getRandomString,
  validateLoginV2,
} from "../../support/commands/general";
import {
  deleteGroupFromAPI,
  getGroupFromAPI,
} from "../../support/utils/group.utils";

const sessionId = ["groupBasics", getRandomString()];

describe("Group - create, edit and delete", () => {
  // Define some group details
  const groupDescription = "This is a test group from Cypress";
  let groupNameRandomPart;
  let groupName;
  let groupSlug;

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
    cy.visit("/v2");
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
});
