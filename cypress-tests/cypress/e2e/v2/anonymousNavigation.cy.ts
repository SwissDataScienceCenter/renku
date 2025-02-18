import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import { User } from "../../support/types/user.types";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
} from "../../support/utils/projectsV2.utils";

const sessionId = ["anonymousNavigation", getRandomString()];

describe("Anonymous users can only access public resources", () => {
  // Define some project details
  let randomString: string;
  let privateProjectName: string;
  let publicProjectName: string;

  function resetRequiredResources() {
    randomString = getRandomString();
    privateProjectName = `anon-private-${randomString}`;
    publicProjectName = `anon-public-${randomString}`;
  }

  function browserLogout() {
    // Logging out from the browser ensures cookies are cleared
    cy.visit("/v2");
    cy.getDataCy("navbar-toggle-user-menu").click();
    cy.getDataCy("navbar-logout").click();
    cy.getDataCy("renku-root").should("be.visible");
  }

  // Restore the session (login) and create the required projects
  beforeEach(() => {
    // Login
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLoginV2,
    );

    // Create projects with new names to be sure re-runs don't break.
    resetRequiredResources();
    getUserData().then((user: User) => {
      for (const proj of [privateProjectName, publicProjectName]) {
        createProjectIfMissingAPIV2({
          name: proj,
          namespace: user.username,
          slug: proj,
          visibility: proj.includes("public") ? "public" : "private",
        }).then((response) => {
          // eslint-disable-line max-nested-callbacks
          if (response.status >= 400) {
            throw new Error("Failed to create project");
          }
        });
      }
    });
  });

  // Cleanup the project after the test
  afterEach(() => {
    // Login -- mind we might be logged out
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLoginV2,
    );

    // Delete the projects
    getUserData().then((user: User) => {
      [privateProjectName, publicProjectName].forEach((proj) => {
        getProjectByNamespaceAPIV2({
          namespace: user.username,
          slug: proj,
          // eslint-disable-next-line max-nested-callbacks
        }).then((response) => {
          if (response.status === 200) {
            deleteProjectFromAPIV2({
              id: response.body.id,
              slug: proj,
            });
          }
        });
      });
    });
  });

  it("Navigate to projects and search for them", () => {
    // Assess the projects exist and are search-able for the logged user
    cy.visit("/v2");
    cy.getDataCy("navbar-link-search").click();
    cy.intercept(
      new RegExp(`(?:/ui-server)?/api/search/query\\?q=.*?${randomString}.*`),
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
    browserLogout();
    cy.visit("/v2");
    cy.getDataCy("navbar-login").should("be.visible");
    cy.getDataCy("navbar-link-search").click();
    cy.intercept(
      new RegExp(`(?:/ui-server)?/api/search/query\\?q=.*?${randomString}.*`),
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
      cy.robustLogin();
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

      cy.get("#project-visibility-private").should("be.checked");
      cy.getDataCy("project-visibility-public").click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject",
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");
      cy.getDataCy("project-settings-general")
        .get(".alert-success")
        .contains("The project has been successfully updated.");

      browserLogout();
    });

    // Verify the previously private project is now publicly visible
    cy.visit("/v2");
    cy.getDataCy("navbar-login").should("be.visible");
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
      .filter(`:contains("${publicProjectName}")`)
      .find(`[data-cy=search-card-entity-link]`)
      .click();

    // Login, check the public project and change visibility
    cy.url().then((url) => {
      cy.robustLogin();
      cy.visit(url);
      cy.getDataCy("project-settings-link").click();

      cy.get("#project-visibility-public").should("be.checked");
      cy.getDataCy("project-visibility-private").click();

      cy.intercept("PATCH", /(?:\/ui-server)?\/api\/data\/projects\/[^/]+/).as(
        "updateProject",
      );
      cy.getDataCy("project-update-button").click();
      cy.wait("@updateProject");
      cy.getDataCy("project-settings-general")
        .get(".alert-success")
        .contains("The project has been successfully updated.");

      browserLogout();
    });

    // Verify the previously public project is now invisible
    cy.visit("/v2");
    cy.getDataCy("navbar-login").should("be.visible");
    cy.getDataCy("navbar-link-search").click();
    cy.getDataCy("search-input").clear().type(randomString);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 1)
      .should("not.contain", publicProjectName)
      .should("contain", privateProjectName);
  });
});
