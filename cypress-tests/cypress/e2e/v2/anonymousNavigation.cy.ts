import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import { User } from "../../support/types/user";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { verifySearchIndexing } from "../../support/utils/search";

const anonymousSession = {
  id: ["anonymousNavigation-anonymousUser", getRandomString()],
  setup: () => {
    cy.log("Anonymous session");
  },
};
const loggedSession = {
  id: ["anonymousNavigation-loggedUser", getRandomString()],
  setup: () => {
    cy.log("Logged in session");
    cy.robustLogin("v2");
  },
};

describe("Anonymous users can only access public resources", () => {
  // Define some project details
  let randomString: string;
  let privateProjectName: string;
  let publicProjectName: string;
  let privateProjectId: string | null = null;
  let publicProjectId: string | null = null;

  function resetRequiredResources() {
    randomString = getRandomString();
    privateProjectName = `anon-private-${randomString}`;
    publicProjectName = `anon-public-${randomString}`;
    privateProjectId = null;
    publicProjectId = null;
  }

  // Restore the session (login) and create the required projects
  beforeEach(() => {
    // Login
    cy.session(loggedSession.id, loggedSession.setup, validateLoginV2);

    // Create projects with new names to be sure re-runs don't break.
    resetRequiredResources();
    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        name: privateProjectName,
        namespace: user.username,
        slug: privateProjectName,
        visibility: "private",
      }).then((response) => {
        if (response.status >= 400) {
          throw new Error("Failed to create private project");
        }
        privateProjectId = response.body.id;
      });

      createProjectIfMissingV2({
        name: publicProjectName,
        namespace: user.username,
        slug: publicProjectName,
        visibility: "public",
      }).then((response) => {
        if (response.status >= 400) {
          throw new Error("Failed to create public project");
        }
        publicProjectId = response.body.id;
      });
    });

    // Verify the resources are searchable
    verifySearchIndexing(randomString, 2);
  });

  // Cleanup the project after the test
  afterEach(() => {
    // Login -- mind we might be logged out
    cy.session(loggedSession.id, loggedSession.setup, validateLoginV2);

    // Delete the projects
    deleteProject(privateProjectId);
    deleteProject(publicProjectId);
  });

  it("Navigate to projects and search for them", () => {
    // Assess the projects exist and are search-able for the logged user
    cy.visit("/v2");
    cy.getDataCy("navbar-link-search").click();
    cy.intercept(
      new RegExp(`/api/data/search/query\\?(?:.*&)*q=.*?${randomString}.*`),
    ).as("searchQuery");
    cy.getDataCy("search-input").clear().type(randomString);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 2)
      .each((card) => {
        cy.wrap(card).should("contain", randomString);
      });

    // Log out and search for the projects as an anonymous user
    cy.session(anonymousSession.id, anonymousSession.setup);
    cy.visit("/search");
    cy.getDataCy("navbar-login").should("be.visible");
    cy.intercept(
      new RegExp(`/api/data/search/query\\?(?:.*&)*q=.*?${randomString}.*`),
    ).as("searchQuery");
    cy.getDataCy("search-input").clear().type(randomString);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card").should("have.length", 1).contains(randomString);
    cy.getDataCy("search-card").getDataCy("search-card-entity-link").click();
    cy.getDataCy("project-name").should("contain", publicProjectName);
    cy.getDataCy("project-settings-link").click();
    cy.getDataCy("project-update-button").should("not.exist");

    // Login, check the private project and change visibility
    cy.url().then((url) => {
      cy.session(loggedSession.id, loggedSession.setup, validateLoginV2);
      cy.visit(url);
      cy.getDataCy("project-update-button").should("be.visible");

      cy.getDataCy("navbar-link-search").click();
      cy.getDataCy("search-input").clear().type(randomString);
      cy.getDataCy("search-button").click();
      cy.wait("@searchQuery");
      cy.getDataCy("search-card")
        .should("have.length", 2)
        .each((card) => {
          cy.wrap(card).should("contain", randomString);
        });
      cy.getDataCy("search-card")
        .filter(`:contains("${privateProjectName}")`)
        .find(`[data-cy=search-card-entity-link]`)
        .click();
      cy.getDataCy("project-name").should("contain", privateProjectName);
      cy.getDataCy("project-settings-link").click();

      cy.get("#project-settings-form-project-visibility-private").should(
        "be.checked",
      );
      cy.getDataCy("project-visibility-public").click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject",
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");
      cy.getDataCy("project-settings-general")
        .get(".alert-success")
        .contains("The project has been successfully updated.");
    });

    // Verify the previously private project is now publicly visible
    cy.session(anonymousSession.id, anonymousSession.setup);
    cy.visit("/search");
    cy.getDataCy("navbar-login").should("be.visible");
    cy.getDataCy("search-input").clear().type(randomString);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 2)
      .each((card) => {
        cy.wrap(card).should("contain", randomString);
      });

    cy.getDataCy("search-card")
      .filter(`:contains("${publicProjectName}")`)
      .find(`[data-cy=search-card-entity-link]`)
      .click();

    // Login, check the public project and change visibility
    cy.url().then((url) => {
      cy.session(loggedSession.id, loggedSession.setup, validateLoginV2);
      cy.visit(url);
      cy.getDataCy("project-settings-link").click();

      cy.get("#project-settings-form-project-visibility-public").should(
        "be.checked",
      );
      cy.getDataCy("project-visibility-private").click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject",
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");
      cy.getDataCy("project-settings-general")
        .get(".alert-success")
        .contains("The project has been successfully updated.");
    });

    // Verify the previously public project is now invisible
    cy.session(anonymousSession.id, anonymousSession.setup);
    cy.visit("/search");
    cy.getDataCy("navbar-login").should("be.visible");
    cy.getDataCy("search-input").clear().type(randomString);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 1)
      .should("not.contain", publicProjectName)
      .should("contain", privateProjectName);
  });
});
