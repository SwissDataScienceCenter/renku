import { v4 as uuidv4 } from "uuid";

import { ProjectIdentifier } from "./projects";

export type DatasetIdentifier = ProjectIdentifier & {
  datasetName: string;
  datasetSlug: string;
};

export type DatasetNames = {
  name: string;
  slug: string;
};

export function generatorDatasetName(name: string): DatasetNames {
  if (!name) throw new Error("Project name cannot be empty");
  const datasetName = `cypress ${name.toLowerCase()} ${uuidv4().substring(24)}`;
  return {
    name: datasetName,
    slug: datasetName.replace(/ /g, "-"),
  };
}

function searchForDataset(name: string, shouldExist = true) {
  cy.visit("/search");
  cy.getDataCy("list-card").should("be.visible");
  cy.getDataCy("type-entity-dataset").should("be.visible").and("not.be.checked");
  // ? Temporary workaround to prevent a components refresh from breaking the tests
  cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
  cy.getDataCy("type-entity-dataset").should("be.visible").check();
  cy.get("input[placeholder='Search...']")
    .should("be.visible")
    .type(name)
    .type("{enter}");
  if (shouldExist)
    cy.get("[data-cy='list-card-title']").contains(name).should("be.visible");
  else cy.get(name).should("not.exist");
}

export default function registerDatasetsCommands() {
  Cypress.Commands.add("searchForDataset", searchForDataset);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      searchForDataset(name: string, shouldExist?: boolean);
    }
  }
}
