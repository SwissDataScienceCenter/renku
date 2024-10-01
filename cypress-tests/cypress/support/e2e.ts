import "cypress-localstorage-commands";

import registerDatasetsCommands from "./commands/datasets";
import registerGeneralCommands from "./commands/general";
import registerLoginCommands from "./commands/login";
import registerProjectCommands from "./commands/projects";
import registerSessionCommands from "./commands/sessions";

registerGeneralCommands();
registerLoginCommands();
registerProjectCommands();
registerSessionCommands();
registerDatasetsCommands();

Cypress.on("uncaught:exception", (err) => {
  return false;
});
