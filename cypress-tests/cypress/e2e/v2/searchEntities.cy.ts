import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import { User } from "../../support/types/user.types";
import {
  createGroupIfMissingAPI,
  deleteGroupFromAPI,
  getGroupFromAPI,
} from "../../support/utils/group.utils";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
} from "../../support/utils/projectsV2.utils";

const sessionId = ["searchEntities", getRandomString()];

describe("Search for resources: groups, projects, users", () => {
  // Define projects and groups details
  // ? Random string is currently the only way to get good results from search
  // ? since searching for multiple words may return a bunch a unexpected results.
  const stringRandomOne = getRandomString();
  const stringRandomTwo = getRandomString();
  const stringFirst = "first";
  const stringSecond = "second";
  const stringProject = "project";
  const stringGroup = "group";
  const projects = {
    first: `${stringProject}-${stringFirst}-${stringRandomOne}-${getRandomString()}`,
    second: `${stringProject}-${stringSecond}-${stringRandomTwo}-${getRandomString()}`,
  };
  const groups = {
    first: `${stringGroup}-${stringFirst}-${stringRandomTwo}-${getRandomString()}`,
    second: `${stringGroup}-${stringSecond}-${stringRandomOne}-${getRandomString()}`,
  };

  // Create the requires resources
  before(() => {
    // Login
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLoginV2,
    );

    // Create groups and projects -- fail early if any of the resources are not created
    getUserData().then((user: User) => {
      for (const proj of [projects.first, projects.second]) {
        createProjectIfMissingAPIV2({
          name: proj,
          namespace: user.username,
          slug: proj,
        }).then((response) => {
          // eslint-disable-line max-nested-callbacks
          if (response.status >= 400) {
            throw new Error("Failed to create project");
          }
        });
      }
      for (const grp of [groups.first, groups.second]) {
        createGroupIfMissingAPI({
          name: grp,
          slug: grp,
        }).then((response) => {
          // eslint-disable-line max-nested-callbacks
          if (response.status >= 400) {
            throw new Error("Failed to create group");
          }
        });
      }
    });
  });

  // Restore the session (login)
  beforeEach(() => {
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLoginV2,
    );
  });

  // Cleanup the resources after the test
  after(() => {
    [projects.first, projects.second].forEach((proj) => {
      getProjectByNamespaceAPIV2({
        slug: proj,
      }).then((response) => {
        if (response.status === 200) {
          deleteProjectFromAPIV2(response.body.id);
        }
      });
    });
    [groups.first, groups.second].forEach((grp) => {
      getGroupFromAPI(grp).then((response) => {
        if (response.status === 200) {
          deleteGroupFromAPI(grp);
        }
      });
    });
  });

  it("Can search for entities and filter works", () => {
    cy.visit("/v2");

    // Search for string
    cy.getDataCy("navbar-link-search").click();
    cy.intercept(
      new RegExp(
        `(?:/ui-server)?/api/search/query\\?q=.*?${stringRandomOne}.*`,
      ),
    ).as("searchQueryOne");
    cy.getDataCy("search-input").clear().type(stringRandomOne);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQueryOne");
    cy.getDataCy("search-card").should("have.length", 2);
    cy.intercept(
      new RegExp(
        `(?:/ui-server)?/api/search/query\\?q=.*?${stringRandomTwo}.*`,
      ),
    ).as("searchQueryTwo");
    cy.getDataCy("search-input").clear().type(stringRandomTwo);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQueryTwo");
    cy.getDataCy("search-card").should("have.length", 2);

    // Filter for projects and groups
    cy.getDataCy("search-filter-type-group").filter(":visible").click();
    cy.wait("@searchQueryTwo");
    cy.getDataCy("search-card").should("have.length", 1);

    cy.getDataCy("search-input").clear().type(stringRandomOne);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQueryOne");
    cy.getDataCy("search-card").should("have.length", 1);

    cy.getDataCy("search-filter-type-group").filter(":visible").click();
    cy.getDataCy("search-filter-type-project").filter(":visible").click();
    cy.wait("@searchQueryOne");
    cy.getDataCy("search-card").should("have.length", 1);

    // Search with filters
    const complexSearch = `type:group,project ${stringRandomOne} ${stringRandomTwo}`;
    cy.getDataCy("search-input").clear().type(complexSearch);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQueryTwo");
    cy.getDataCy("search-card").should("have.length", 4);

    // Filter by date
    cy.getDataCy("search-filter-created-older-than-90-days")
      .filter(":visible")
      .click();
    cy.wait("@searchQueryTwo");
    cy.getDataCy("search-card").should("have.length", 0);
    cy.getDataCy("search-filter-created-last-week").filter(":visible").click();

    // Can search for users -- can't guarantee we match an exact number here
    const userSearch = "type:user";
    cy.getDataCy("search-filter-created-all").filter(":visible").click();
    cy.intercept(
      new RegExp(`(?:/ui-server)?/api/search/query\\?q=.*user.*`),
    ).as("searchQueryUsers");
    cy.getDataCy("search-input").clear().type(userSearch);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQueryUsers");
    cy.getDataCy("search-card").should("have.length.at.least", 1);
  });
});
