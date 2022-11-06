import { defineConfig } from 'cypress'

export default defineConfig({
  e2e: {
    baseUrl: process.env.BASE_URL,
    supportFile: "cypress/support/index.ts",
  },
  env: {
    TEST_EMAIL: process.env.TEST_EMAIL,
    TEST_PASSWORD: process.env.TEST_PASSWORD,
    TEST_FIRST_NAME: process.env.TEST_FIRST_NAME,
    TEST_LAST_NAME: process.env.TEST_LAST_NAME,
    TEST_USERNAME: process.env.TEST_USERNAME,
  },
  defaultCommandTimeout: 10000,
  chromeWebSecurity: false,
})
