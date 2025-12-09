import { getRandomString, getUserData } from "../../support/commands/general";
import { User } from "../../support/types/user";
import { createGroupIfMissing, deleteGroup } from "../../support/utils/groups";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { verifySearchIndexing } from "../../support/utils/search";
import { login } from "../../support/utils/general";

const sessionId = ["search", getRandomString()];

describe("Search for resources: groups, projects, users", () => {
  // Define projects and groups details
  // ? Random string is currently the only way to get good results from search
  // ? since searching for multiple words may return a bunch an unexpected results.
  const stringRandomOne = getRandomString();
  const stringRandomTwo = getRandomString();
  const stringFirst = "first";
  const stringSecond = "second";
  const stringProject = "project";
  const stringGroup = "group";
  const projects = {
    first: `${stringProject}-${stringFirst}-${stringRandomOne}`,
    second: `${stringProject}-${stringSecond}-${stringRandomTwo}`,
  };
  const groups = {
    first: `${stringGroup}-${stringFirst}-${stringRandomTwo}`,
    second: `${stringGroup}-${stringSecond}-${stringRandomOne}`,
  };
  let projectIdFirst: string | null = null;
  let projectIdSecond: string | null = null;

  // Create the required resources
  before(() => {
    login(sessionId);

    // Create groups and projects -- fail early if any of the resources are not created
    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        name: projects.first,
        namespace: user.username,
        slug: projects.first,
      }).then((response) => {
        if (response.status >= 400) {
          throw new Error("Failed to create first project");
        }
        projectIdFirst = response.body.id;
      });

      createProjectIfMissingV2({
        name: projects.second,
        namespace: user.username,
        slug: projects.second,
      }).then((response) => {
        if (response.status >= 400) {
          throw new Error("Failed to create second project");
        }
        projectIdSecond = response.body.id;
      });

      for (const grp of [groups.first, groups.second]) {
        createGroupIfMissing({
          name: grp,
          slug: grp,
        }).then((response) => {
          if (response.status >= 400) {
            throw new Error("Failed to create group");
          }
        });
      }
    });

    // Verify the resources are searchable
    verifySearchIndexing(`${stringRandomOne} ${stringRandomTwo}`, 4);
  });

  // Restore the session (login)
  beforeEach(() => {
    login(sessionId);
  });

  // Cleanup the resources after the test
  after(() => {
    deleteProject(projectIdFirst);
    deleteProject(projectIdSecond);
    deleteGroup(groups.first);
    deleteGroup(groups.second);
  });

  it("Can search for entities and filter works", () => {
    cy.visit("/v2");

    // Search for string
    cy.getDataCy("navbar-link-search").click();
    cy.intercept(new RegExp(`/api/data/search/query.*`)).as("searchQuery");
    cy.getDataCy("search-input").clear().type(stringRandomOne);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 2)
      .each((card) => {
        cy.wrap(card).should("contain", stringRandomOne);
      });
    cy.getDataCy("search-input").clear().type(stringRandomTwo);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 2)
      .each((card) => {
        cy.wrap(card).should("contain", stringRandomTwo);
      });

    // Filter for projects and groups
    cy.getDataCy("search-filter-type-group").filter(":visible").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card").should("have.length", 1).contains(groups.first);

    cy.getDataCy("search-input").clear().type(stringRandomOne);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 1)
      .contains(groups.second);

    cy.getDataCy("search-filter-type-group").filter(":visible").click();
    cy.getDataCy("search-filter-type-project").filter(":visible").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 1)
      .contains(projects.first);

    // Search with filters
    const complexSearch = `type:group,project ${stringRandomOne} ${stringRandomTwo}`;
    cy.getDataCy("search-input").clear().type(complexSearch);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card")
      .should("have.length", 4)
      .each((card) => {
        cy.wrap(card).should(($card) => {
          const text = $card.text();
          expect(text).to.match(
            new RegExp(`${stringRandomOne}|${stringRandomTwo}`),
          );
        });
      });

    // Filter by date
    cy.getDataCy("search-filter-created-older-than-90-days")
      .filter(":visible")
      .click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card").should("have.length", 0);
    cy.getDataCy("search-filter-created-last-week").filter(":visible").click();

    // Can search for users -- can't guarantee we match an exact number here
    const userSearch = "type:user";
    cy.getDataCy("search-filter-created-all").filter(":visible").click();
    cy.getDataCy("search-input").clear().type(userSearch);
    cy.getDataCy("search-button").click();
    cy.wait("@searchQuery");
    cy.getDataCy("search-card").should("have.length.at.least", 1);
  });
});
