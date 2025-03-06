import { TIMEOUTS } from "../../../config";

type Matcher = "eq" | "gte";

export function verifySearchIndexing(
  query: string,
  expectedItems: number, // ? mind there is a limit from paging!
  {
    maxAttempts = 5,
    loopDelay = TIMEOUTS.shorter,
    matcher = "eq",
  }: { maxAttempts?: number; loopDelay?: number; matcher?: Matcher } = {},
): Cypress.Chainable<boolean> {
  cy.log(
    `Verifying the search engine has indexed resources matching "${query}".`,
  );

  function attempt(tries: number): Cypress.Chainable<boolean> {
    return cy.request(`/api/search/query?q=${query}`).then((response) => {
      const success =
        matcher === "eq"
          ? response.body.items && response.body.items.length === expectedItems
          : response.body.items && response.body.items.length >= expectedItems;
      if (success) {
        cy.log("Search indexing complete.");
        return cy.wrap(true);
      } else if (tries <= maxAttempts) {
        cy.wait(loopDelay).then(() => attempt(tries + 1));
      } else {
        cy.log(
          `Expected ${matcher === "gte" ? "at least " : ""}${expectedItems} items but found ${response.body.items ? response.body.items.length : "none"}.`,
        );
        throw new Error(
          `Search indexing faild after ${maxAttempts} attempts every ${Math.floor(loopDelay / 1_000)} seconds.`,
        );
      }
    });
  }
  return attempt(1);
}
