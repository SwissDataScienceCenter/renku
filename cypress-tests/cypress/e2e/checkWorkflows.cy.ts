import { TIMEOUTS } from "../../config";
import { ProjectIdentifier } from "../support/commands/projects";
import { validateLogin } from "../support/commands/general";

const project: ProjectIdentifier = {
  namespace: "renku-ui-tests",
  name: "composite-workflows",
};

describe("Workflows pages", () => {
  before(() => {
    // Use a session to preserve login data
    cy.session(
      "login-updateProjects",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
  });

  beforeEach(() => {
    // Restore the session
    cy.session(
      "login-updateProjects",
      () => {
        cy.robustLogin();
      },
      validateLogin
    );
  });

  it("Check workflows", () => {
    cy.visitAndLoadProject(project);

    // Go the the workflows page and check the details of a workflow
    cy.getProjectSection("Workflows").click();
    cy.get("[data-cy=workflows-page]", { timeout: TIMEOUTS.long }).should(
      "be.visible"
    );
    cy.dataCy("workflows-browser")
      .should("be.visible")
      .children()
      .should("have.length", 9)
      .contains("useless-workflow-with-kw")
      .should("be.visible")
      .click();

    cy.dataCy("workflow-details")
      .should("be.visible")
      .contains("This is defintely a useless workflow.")
      .should("be.visible");
    cy.dataCy("workflow-details").contains("keyword1,").should("be.visible");
    cy.dataCy("workflow-details")
      .contains("renku workflow execute useless-workflow-with-kw")
      .should("be.visible");

    // Play with the workflow browser and check composite workflows link other workflows
    cy.dataCy("workflows-browser")
      .should("be.visible")
      .children()
      .should("have.length", 9);
    cy.dataCy("workflows-browser")
      .get(".rk-tree-item--children")
      .should("not.exist");
    cy.dataCy("workflows-browser")
      .children()
      .contains("parent-composite")
      .should("be.visible")
      .click();
    cy.dataCy("workflows-browser").children().should("have.length", 11);
    cy.dataCy("workflows-browser")
      .get(".rk-tree-item--children")
      .should("be.visible");

    cy.dataCy("workflow-details").within(() => {
      cy.root().contains("Workflow (Composite)").should("be.visible");
      cy.root().contains("div h5", "composite1").should("be.visible").click();
    });

    cy.dataCy("workflow-details").within(() => {
      cy.root()
        .contains("renku workflow execute composite1")
        .should("be.visible");
      cy.root().contains("Workflow (Composite)").should("be.visible");
      cy.root().contains("step1_input-1").should("be.visible");
      cy.root()
        .contains("div h5", "so-many-steps")
        .should("be.visible")
        .click();
    });

    cy.dataCy("workflow-details")
      .contains("div", "input-2")
      .should("be.visible")
      .click();

    // Check links to the file browser
    cy.dataCy("workflow-details")
      .contains("td", "Default value")
      .should("be.visible")
      .siblings("td")
      .contains("span", "m")
      .should("be.visible");
    cy.dataCy("workflow-details")
      .get("a#icon-link-5")
      .should("be.visible")
      .click();
    cy.get("#file-card-header")
      .should("be.visible")
      .siblings("ul")
      .contains("Commit: badf2582")
      .should("be.visible");
  });
});
