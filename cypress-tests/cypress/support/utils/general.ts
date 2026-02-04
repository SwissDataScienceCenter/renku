import { validateLoginV2 } from "../commands/general";

/** Login using `cy.session` with the provided session ID. */
export function login(sessionId: string | string[]) {
  cy.session(
    sessionId,
    () => {
      cy.robustLogin("v2");
    },
    validateLoginV2,
  );
}
