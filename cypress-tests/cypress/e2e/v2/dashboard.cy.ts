import { getRandomString, getUserData } from "../../support/commands/general";
import { generatorProjectName } from "../../support/commands/projects";
import {
  createProjectIfMissingV2,
  deleteProject,
} from "../../support/utils/projects";
import { login } from "../../support/utils/general";
import { User } from "../../support/types/user";

const projectTestConfig = {
  projectAlreadyExists: false,
  projectName: generatorProjectName("dashboardV2"),
};

const prefixProjectTitle = "My Renku Project";
const sessionId = ["dashboard", getRandomString()];

describe("Dashboard v2 - Authenticated user", () => {
  const projectSlug = projectTestConfig.projectName;
  let projectId: string | null = null;

  after(() => {
    if (!projectTestConfig.projectAlreadyExists) {
      deleteProject(projectId);
    }
  });

  beforeEach(() => {
    login(sessionId);

    getUserData().then((user: User) => {
      createProjectIfMissingV2({
        description: "Test project for dashboard tests",
        name: `${prefixProjectTitle} ${projectSlug}`,
        namespace: user.username,
        slug: projectSlug,
        visibility: "private",
      }).then((response) => {
        projectId = response.body.id;
      });
    });
  });

  it("Can see own project on the dashboard", () => {
    cy.visit("/");
    cy.getDataCy("dashboard-project-list")
      .find("a")
      .should("have.length.at.least", 1);
    cy.getDataCy("dashboard-project-list")
      .find("a")
      .should("contain.text", `${prefixProjectTitle} ${projectSlug}`);
    cy.getDataCy("dashboard-project-list")
      .find("a")
      .should("contain.text", projectSlug);
  });

  it("Can find project in the search results", () => {
    cy.visit("/");
    cy.getDataCy("view-my-Projects-btn").click();
    cy.getDataCy("search-list-item").should("have.length.at.least", 1);
    cy.getDataCy("search-list-item").should(
      "contain.text",
      `${prefixProjectTitle} ${projectSlug}`,
    );
  });
});

describe("Dashboard v2 - Non-Authenticated user", () => {
  it("Cannot see projects and groups on Dashboard when logged out", () => {
    cy.visit("/");
    cy.getDataCy("user-container").should("not.exist");
  });
});
