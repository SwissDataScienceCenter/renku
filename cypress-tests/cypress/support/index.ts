/// <reference types="cypress" />

import registerGeneralCommands from "./commands/general";
import registerLoginCommands from "./commands/login";
import registerProjectCommands from "./commands/projects";
import registerSessionCommands from "./commands/sessions";
import "cypress-localstorage-commands";

registerGeneralCommands();
registerLoginCommands();
registerProjectCommands();
registerSessionCommands();
