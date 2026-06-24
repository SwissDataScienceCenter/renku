import { getRandomString, getUserData } from "../../support/commands/general";
import { generatorProjectName } from "../../support/commands/projects";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { login } from "../../support/utils/general";
import { User } from "../../support/types/user";
import { validateLoginV2 } from "../../support/commands/general";
import type { Project } from "../../support/types/projects";

const projectTestConfig = {
  projectAlreadyExists: false,
  projectName: `project-dashboard-test-${getRandomString()}`,
};

const anonymousSession = {
  id: ["dashboard-anonymousUser", getRandomString()],
  setup: () => {
    cy.log("Anonymous session");
  },
};
const loggedSession = {
  id: ["dashboard-loggedUser", getRandomString()],
  setup: () => {
    cy.log("Logged in session");
    cy.robustLogin("v2");
  },
};

describe("Dashboard v2 - Authenticated user", () => {
  let projectId: string = "";

  before(() => {
    cy.session(loggedSession.id, loggedSession.setup, validateLoginV2);

    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        name: projectTestConfig.projectName,
        namespace: user.username,
        slug: projectTestConfig.projectName,
        description: "Test project for dashboard tests",
        visibility: "private",
      }).then((response) => {
        projectId = response.body.id;
      });
    });
  });

  after(() => {
    if (!projectTestConfig.projectAlreadyExists) {
      deleteProject(projectId);
    }
  });

  beforeEach(() => {
    cy.session(loggedSession.id, loggedSession.setup, validateLoginV2);
  });

  it("Can see own project on the dashboard or in the search results", () => {
    let projectIsOnDashboard: boolean = false;
    cy.intercept("/api/data/projects?direct_member=true*", (request) => {
      request.on("after:response", (response) => {
        expect(response.statusCode).to.eq(200);
        const projects = response.body as Project[];
        projectIsOnDashboard = projects.some(({ id }) => id === projectId);
      });
    }).as("projectList");

    cy.visit("/");

    cy.wait("@projectList").then(() => {
      cy.getDataCy("dashboard-project-list")
        .find("a")
        .should("have.length.at.least", 1);

      if (projectIsOnDashboard) {
        cy.getDataCy("dashboard-project-list")
          .find("a")
          .should("contain.text", projectTestConfig.projectName);
        cy.getDataCy("dashboard-project-list")
          .find("a")
          .should("contain.text", projectTestConfig.projectName);
      } else {
        cy.getDataCy("view-my-Projects-btn")
          .contains(new RegExp("View all my [0-9]+ projects"))
          .click();
        cy.getDataCy("search-list-item").should("have.length.at.least", 1);
        cy.getDataCy("search-query-input")
          .clear()
          .type(projectTestConfig.projectName);
        cy.getDataCy("search-query-button").click();
        cy.getDataCy("search-list-item")
          .contains(projectTestConfig.projectName)
          .should("be.visible");
      }
    });
  });

  it("Can find project in the search results", () => {
    cy.visit("/");
    cy.getDataCy("view-my-Projects-btn").click();
    cy.getDataCy("search-list-item").should("have.length.at.least", 1);
    cy.getDataCy("search-query-input")
      .clear()
      .type(projectTestConfig.projectName);
    cy.getDataCy("search-query-button").click();
    cy.getDataCy("search-list-item")
      .contains(projectTestConfig.projectName)
      .should("be.visible");
  });
});

describe("Dashboard v2 - Non-Authenticated user", () => {
  beforeEach(() => {
    cy.session(anonymousSession.id, anonymousSession.setup);
  });

  it("Cannot see projects and groups on Dashboard when logged out", () => {
    cy.visit("/");
    cy.get("#rk-anon-home-frame").should("be.visible");
    cy.getDataCy("user-container").should("not.exist");
  });
});
