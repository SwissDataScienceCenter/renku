import { defineConfig } from "cypress";
import { TIMEOUTS } from "./config";

export default defineConfig({
  e2e: {
    baseUrl: process.env.BASE_URL || "https://dev.renku.ch",
    // specPattern: [
    //   // Verify infrastructure before running any other tests
    //   "cypress/e2e/verifyInfrastructure.cy.ts",
    //   "cypress/e2e/*.{js,jsx,ts,tsx}"
    // ],
  },
  env: {
    TEST_EMAIL: process.env.TEST_EMAIL,
    TEST_PASSWORD: process.env.TEST_PASSWORD,
    TEST_FIRST_NAME: process.env.TEST_FIRST_NAME,
    TEST_LAST_NAME: process.env.TEST_LAST_NAME,
    TEST_USERNAME: process.env.TEST_USERNAME,
  },
  defaultCommandTimeout: TIMEOUTS.standard,
  chromeWebSecurity: false,
  viewportWidth: 1024,
  viewportHeight: 768
});
