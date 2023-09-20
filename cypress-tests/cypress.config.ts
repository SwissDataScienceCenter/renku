import { defineConfig } from "cypress";
import { TIMEOUTS } from "./config";

export default defineConfig({
  e2e: {
    baseUrl: process.env.BASE_URL || "https://dev.renku.ch"
  },
  env: {
    TEST_EMAIL: process.env.TEST_EMAIL,
    TEST_PASSWORD: process.env.TEST_PASSWORD,
    TEST_FIRST_NAME: process.env.TEST_FIRST_NAME,
    TEST_LAST_NAME: process.env.TEST_LAST_NAME,
    TEST_USERNAME: process.env.TEST_USERNAME,
  },
  retries: {
    runMode: 2,
    openMode: null
  },
  defaultCommandTimeout: TIMEOUTS.standard,
  chromeWebSecurity: false,
  viewportWidth: 1280,
  viewportHeight: 960,
  videoUploadOnPasses: false
});
