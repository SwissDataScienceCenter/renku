const renkuLogin = (credentials: { username: string; password: string }[]) => {
  for (const { username, password } of credentials) {
    cy.get("#username").type(username);
    cy.get("#password").type(password, { log: false });
    cy.get("#kc-login").click().should("not.exist");
  }
  cy.url().then((url) => {
    const parsedUrl = new URL(url);
    if (
      parsedUrl.pathname.includes("gitlab") ||
      parsedUrl.host.includes("gitlab")
    ) {
      cy.get(".doorkeeper-authorize >>>> .btn-danger")
        .should("be.visible")
        .should("be.enabled")
        .click();
    }
  });
};

const register = (
  email: string,
  password: string,
  firstName?: string,
  lastName?: string
) => {
  cy.visit("/login");

  // ? wait to be assess whether tokens were refreshed automatically or we really need to register
  cy.wait(1000); // eslint-disable-line cypress/no-unnecessary-waiting
  cy.request({ failOnStatusCode: false, url: "ui-server/api/user" }).then(
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
    }
  );
};

type RegisterAndVerifyProps = {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
};

function registerAndVerify(props: RegisterAndVerifyProps) {
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
  const httpBaseUrl = Cypress.config("baseUrl").replace(
    /^https:\/\//,
    "http://"
  );
  const httpsBaseUrl = Cypress.config("baseUrl").replace(
    /^http:\/\//,
    "https://"
  );
  cy.url().should("be.oneOf", [
    httpBaseUrl,
    httpsBaseUrl,
    httpBaseUrl + "/",
    httpsBaseUrl + "/",
  ]);
  cy.request("ui-server/api/user").its("status").should("eq", 200);
}

type RobustLoginProps = {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
};

function robustLogin(props?: RobustLoginProps) {
  // Check if we are already logged in
  cy.request({ failOnStatusCode: false, url: "ui-server/api/user" }).then(
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

      return registerAndVerify(localProps);
    }
  );
}

function logout() {
  //  cy.visit("/");
  cy.get("#profile-dropdown").should("be.visible").click();
  cy.get("#logout-link").should("be.visible").click();
}

export default function registerLoginCommands() {
  Cypress.Commands.add("logout", logout);
  Cypress.Commands.add("renkuLogin", renkuLogin);
  Cypress.Commands.add("register", register);
  Cypress.Commands.add("registerAndVerify", registerAndVerify);
  Cypress.Commands.add("robustLogin", robustLogin);
}

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      logout();
      renkuLogin(credentials: { username: string; password: string }[]);
      register(
        email: string,
        password: string,
        firstName?: string,
        lastName?: string
      );
      registerAndVerify(props: RegisterAndVerifyProps);
      robustLogin(props?: RobustLoginProps);
    }
  }
}
