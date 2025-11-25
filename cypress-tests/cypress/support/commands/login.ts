import { TIMEOUTS } from "../../../config";

const renkuLogin = (credentials: { username: string; password: string }[]) => {
  cy.wrap(credentials, { log: false }).each(
    (credential: { password: string; username: string }) => {
      cy.get("#username").type(credential.username);
      cy.get("#password").type(credential.password, { log: false });
      cy.get("#kc-login").click();
    },
  );
  cy.url().then((url) => {
    const parsedUrl = new URL(url);
    if (
      parsedUrl.pathname.includes("gitlab") ||
      parsedUrl.host.includes("gitlab")
    ) {
      cy.window().focus();
      cy.get("#container").should("not.have.class", "gl-pointer-events-none");
      cy.contains("button", "Authorize").should("be.visible").click();
    }
  });
};

const register = (
  email: string,
  password: string,
  firstName?: string,
  lastName?: string,
) => {
  cy.visit("/api/auth/login");

  // ? wait to be assess whether tokens were refreshed automatically or we really need to register
  cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
  cy.request({ failOnStatusCode: false, url: "ui-server/api/data/user" }).then(
    (resp) => {
      if (resp.status === 200) return;

      cy.get("div#kc-registration").find("a").should("be.visible").click();
      cy.get(`input[name="firstName"]`)
        .should("be.visible")
        .click()
        .clear()
        .type(firstName ? firstName : "Renku Cypress");
      cy.get(`input[name="lastName"]`)
        .should("be.visible")
        .click()
        .clear()
        .type(lastName ? lastName : "Test");
      cy.get(`input[name="email"]`)
        .should("be.visible")
        .click()
        .clear()
        .type(email);
      cy.get(`input[name="password"]`)
        .should("be.visible")
        .click()
        .clear()
        .type(password, { log: false });
      cy.get(`input[name="password-confirm"]`)
        .should("be.visible")
        .click()
        .clear()
        .type(password, { log: false });
      cy.get(`input[type="submit"]`)
        .should("be.visible")
        .should("be.enabled")
        .click();
    },
  );
};

type RegisterAndVerifyProps = {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
};

function registerAndVerify(
  renkuVersion: "v1" | "v2",
  props: RegisterAndVerifyProps,
) {
  // Register with the CI deployment
  const { email, password, firstName, lastName } = props;
  cy.register(email, password, firstName, lastName);
  cy.url().then((url) => {
    const parsedUrl = new URL(url);
    if (parsedUrl.pathname.includes("registration")) {
      // Still on registration page, it means registration failed, so simply try to log in.
      // Usually occurs because test user already exists on the CI deployment.
      cy.contains("Back to Login").click();
      cy.renkuLogin([{ username: email, password }]);
    }
  });
  // Login with the main dev deployment after registering on the CI deployment (if required)
  cy.url().then((url) => {
    if (url.includes("auth/realms/Renku/protocol/openid-connect/auth"))
      cy.renkuLogin([{ username: email, password }]);
  });
  cy.location().should((loc) => {
    const baseURL = new URL(Cypress.config("baseUrl"));
    expect(["/", ""]).to.include(loc.pathname);
    expect(loc.search).to.eq("");
    expect(loc.hostname).to.eq(baseURL.hostname);
  });
  cy.get("header").should("be.visible");
  cy.get("footer").should("be.visible");
  // If we send a request to the user endpoint on Gitlab too quickly after we log in then
  // it sometimes randomly responds with 401 and sometimes with 200 (as expected). This wait period seems to
  // allow Gitlab to "settle" after the login and properly recognize the token and respond with 200.
  // eslint-disable-next-line cypress/no-unnecessary-waiting
  cy.wait(TIMEOUTS.short);
  cy.request("/api/data/user").its("status").should("eq", 200);
  if (renkuVersion === "v1") {
    cy.request("/api/user").then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).property("username").to.not.be.empty;
      expect(response.body).property("username").to.not.be.null;
      expect(response.body).property("state").to.equal("active");
    });
  }
}

type RobustLoginProps = {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
};

function robustLogin(renkuVersion: "v1" | "v2", props?: RobustLoginProps) {
  // Check if we are already logged in
  cy.request({ failOnStatusCode: false, url: "/api/data/user" }).then(
    (resp) => {
      // we are already logged in
      if (resp.status >= 200 && resp.status < 400) return;

      const localProps = {
        email: Cypress.env("TEST_EMAIL"),
        password: Cypress.env("TEST_PASSWORD"),
        firstName: Cypress.env("TEST_FIRST_NAME"),
        lastName: Cypress.env("TEST_LAST_NAME"),
        // any passed-in props should overwrite, so spread props last
        ...props,
      };

      return registerAndVerify(renkuVersion, localProps);
    },
  );
}

export default function registerLoginCommands() {
  Cypress.Commands.add("renkuLogin", renkuLogin);
  Cypress.Commands.add("register", register);
  Cypress.Commands.add("registerAndVerify", registerAndVerify);
  Cypress.Commands.add("robustLogin", robustLogin);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      renkuLogin(credentials: { username: string; password: string }[]);
      register(
        email: string,
        password: string,
        firstName?: string,
        lastName?: string,
      );
      registerAndVerify(
        renkuVersion: "v1" | "v2",
        props: RegisterAndVerifyProps,
      );
      robustLogin(renkuVersion: "v1" | "v2", props?: RobustLoginProps);
    }
  }
}
