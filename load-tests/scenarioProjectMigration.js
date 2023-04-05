import { describe, expect } from 'https://jslib.k6.io/k6chaijs/4.3.4.2/index.js';
import { uuidv4 } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";

import { renkuLogin } from "./utils/oauth.js";
import { getRandomInt } from "./utils/general.js";
import { createProject, migrateProject } from "./utils/core.js";
import { credentials, baseUrl, gitUrl, oldRenkuTemplateRef, concurentUsers } from "./config.js";
import { deleteProjectByName } from "./utils/git.js";

export const options = {
  scenarios: {
    testUploads: {
      executor: "per-vu-iterations",
      vus: concurentUsers,
      iterations: 1,
    },
  },
};

export default function test() {
  const uuid = uuidv4();
  const ramdomCredentials = credentials[getRandomInt(0, credentials.length)];
  const namespace = ramdomCredentials[ramdomCredentials.length - 1].username.split("@")[0]
  let repoUrl;

  describe("Create a project and show info with the core service", () => {
    describe("user should login", () => {
      renkuLogin(baseUrl, ramdomCredentials);
    });
    describe("should create a project from an old template", () => {
      const res = createProject(baseUrl, gitUrl, "python", uuid, namespace, oldRenkuTemplateRef);
      expect(res.status, 'response status').to.equal(200);
      expect(res).to.have.validJsonBody();
      expect(res.json(), 'response').to.not.have.property("error");
      expect(res.json(), 'response').to.have.nested.property("result.url");
      repoUrl = res.json().result.url;
    });
    describe("should be able to migrate the project", () => {
      const res = migrateProject(baseUrl, repoUrl)
      expect(res.status, 'response status').to.equal(200);
      expect(res).to.have.validJsonBody();
      expect(res.json(), 'response').to.not.have.property("error");
      expect(res.json(), 'response').to.have.nested.property("result.docker_migrated", true);
    });
    describe("should delete the project", () => {
      const res = deleteProjectByName(baseUrl, namespace + "/" + uuid)
      expect(res.status, 'response status').to.equal(202);
    });
  });
}
