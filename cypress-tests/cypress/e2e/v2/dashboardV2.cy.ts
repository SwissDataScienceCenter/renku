import { getRandomString, validateLogin } from "../../support/commands/general";
import { generatorProjectName } from "../../support/commands/projects";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
  getUserNamespaceAPIV2,
  ProjectIdentifierV2,
} from "../../support/utils/projectsV2.utils";
const projectTestConfig = {
  projectAlreadyExists: false,
  projectName: generatorProjectName("dashboardV2"),
};

const prefixProjectTitle = "My Renku Project";
const sessionId = ["dashboardV2", getRandomString()];

describe("Dashboard v2 - Authenticated user", () => {
  const projectIdentifier: ProjectIdentifierV2 = {
    slug: projectTestConfig.projectName,
    id: null,
    namespace: null,
  };

  after(() => {
    if (
      !projectTestConfig.projectAlreadyExists &&
      projectIdentifier.id != null
    ) {
      deleteProjectFromAPIV2(projectIdentifier);
      getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
        expect(response.status).to.equal(404);
      });
    }
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLogin,
    );
    getUserNamespaceAPIV2().then((namespace) => {
      if (namespace) {
        projectIdentifier.namespace = namespace;
        createProjectIfMissingAPIV2({
          visibility: "private",
          name: `${prefixProjectTitle} ${projectIdentifier.slug}`,
          namespace,
          slug: projectIdentifier.slug,
        }).then((project) => (projectIdentifier.id = project.id));
      } else {
        cy.log("No user namespace found, project cannot be created.");
      }
    });
  });

  it("Can see own project on the dashboard", () => {
    cy.visit("v2");
    cy.getDataCy("dashboard-project-list")
      .find("a")
      .should("have.length.at.least", 1);
    cy.getDataCy("dashboard-project-list")
      .find("a")
      .should(
        "contain.text",
        `${prefixProjectTitle} ${projectIdentifier.slug}`,
      );
    cy.getDataCy("dashboard-project-list")
      .find("a")
      .should("contain.text", projectIdentifier.slug);
  });

  it("Can find project in the search results", () => {
    cy.visit("v2");
    cy.getDataCy("view-my-projects-btn").click();
    cy.getDataCy("search-card").should("have.length.at.least", 1);
    cy.getDataCy("search-card").should(
      "contain.text",
      `${prefixProjectTitle} ${projectIdentifier.slug}`,
    );
  });
});

describe("Dashboard v2 - Non-Authenticated user", () => {
  it("Cannot see projects and groups on Dashboard when logged out", () => {
    cy.visit("v2");
    cy.getDataCy("user-container").should("be.visible");
    cy.getDataCy("user-container").should(
      "contain.text",
      "You are not logged in.",
    );
  });
});
