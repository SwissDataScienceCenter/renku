import {
  getRandomString,
  getUserData,
  validateLoginV2,
} from "../../support/commands/general";
import { ProjectIdentifierV2 } from "../../support/types/project.types";
import { User } from "../../support/types/user.types";
import {
  createProjectIfMissingAPIV2,
  deleteProjectFromAPIV2,
  getProjectByNamespaceAPIV2,
} from "../../support/utils/projectsV2.utils";

// !const sessionId = ["sessionBasics", getRandomString()];
const sessionId = ["sessionBasics", 1];

describe("Start a session that consumes project resources", () => {
  // Define required resources
  let projectNameRandomPart: string;
  let projectName;
  let projectPath;
  let projectIdentifier: ProjectIdentifierV2;

  function resetRequiredResources() {
    projectNameRandomPart = getRandomString();
    projectName = `session-basics-${projectNameRandomPart}`;
    projectIdentifier = {
      slug: projectPath,
      id: null,
      namespace: null,
    };
  }

  beforeEach(() => {
    // Restore the session (login)
    cy.session(
      sessionId,
      () => {
        cy.robustLogin();
      },
      validateLoginV2
    );

    // Create a project
    resetRequiredResources();
    getUserData().then((user: User) => {
      projectIdentifier.namespace = user.username;
      createProjectIfMissingAPIV2({
        name: projectName,
        namespace: user.username,
        slug: projectName,
      }).then((project) => (projectIdentifier.id = project.id));
    });
  });

  // Cleanup the project after the test
  after(() => {
    getProjectByNamespaceAPIV2(projectIdentifier).then((response) => {
      if (response.status === 200) {
        projectIdentifier.id = response.body.id;
        projectIdentifier.namespace = response.body.namespace;
        deleteProjectFromAPIV2(projectIdentifier);
      }
    });
  });

  it("Start a session with VSCode and check it has the necessary resources", () => {
    cy.visit("/v2");
  });
});
