import "cypress-localstorage-commands";

import registerDatasetsCommands from "./commands/datasets";
import registerGeneralCommands from "./commands/general";
import registerLoginCommands from "./commands/login";
import registerProjectCommands from "./commands/projects";
import registerSessionCommands from "./commands/sessions";
import registerProjectV2Commands from "./commands/projectsV2";

registerGeneralCommands();
registerLoginCommands();
registerProjectCommands();
registerSessionCommands();
registerDatasetsCommands();
registerProjectV2Commands();

Cypress.on("uncaught:exception", (err) => {
  return false;
});
