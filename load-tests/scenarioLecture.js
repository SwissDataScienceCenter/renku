import { uuidv4 } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
import { describe, expect } from 'https://jslib.k6.io/k6chaijs/4.3.4.2/index.js';
import { Trend } from "k6/metrics";
import { sleep } from "k6";

import { renkuLogin } from "./utils/oauth.js";
import { getRandomInt } from "./utils/general.js";
import { getCommitShasFromProjectName, deleteProjectByName } from "./utils/git.js";
import { createProject } from "./utils/core.js";
import {
  startServer,
  waitForServerState,
  getServer,
  hibernateServer,
  resumeServer,
  stopServer,
  deleteServer,
  waitForImageToBuild
} from "./utils/servers.js";
import { credentials, baseUrl, gitUrl, registryDomain, concurentUsers } from "./config.js";

export const options = {
  scenarios: {
    lecture: {
      executor: "per-vu-iterations",
      vus: concurentUsers, // tested up to 30
      iterations: 1,
      maxDuration: '30m',
      gracefulStop: '2m',
    },
  },
};

// k6 custom metrics
const sessionStartupDuration = new Trend("session_startup_duration", true);
const sessionHibernationDuration = new Trend("session_hibernation_duration", true);

// Test code
export default function test() {
  let commitSha, server;
  const uuid = uuidv4();
  const ramdomCredentials = credentials[getRandomInt(0, credentials.length)];
  const namespace = ramdomCredentials[ramdomCredentials.length - 1].username.split("@")[0]

  describe("class load test scenario", () => {
    describe("user should login", ()=> {
      renkuLogin(baseUrl, ramdomCredentials);
    });
    describe("should create a project", () => {
      const res = createProject(baseUrl, gitUrl, "python-minimal", uuid, namespace);
      expect(res.status, 'response status').to.equal(200);
      expect(res).to.have.validJsonBody();
      expect(res.json(), 'response').to.not.have.property("error");
    });
    describe("should get the project commit sha", () => {
      const res = getCommitShasFromProjectName(baseUrl, namespace + "/" + uuid)
      expect(res.status, 'response status').to.equal(200);
      expect(res).to.have.validJsonBody();
      expect(res.json(), 'json response').to.have.nested.property("[0].id");
      commitSha = res.json()[0].id
    });
    describe("should wait for the image to build", () => {
      const res = waitForImageToBuild(baseUrl, registryDomain, namespace, uuid, commitSha);
      expect(res.imageBuilt, ".imageBuild in response").to.be.true;
    });
    describe("should launch session", () => {
      const res = startServer(baseUrl, commitSha, namespace, uuid);
      expect(res.status, "response status").to.equal(201);
      expect(res).to.have.validJsonBody();
      expect(res.json(), "response").to.have.property("name")
      server = res.json()
    });
    describe("should wait for server to become ready", () => {
      const res = waitForServerState(baseUrl, server.name, "running")
      expect(res.stateAchieved, ".stateAchieved in response").to.be.true;
      expect(res.lastResponse.status, "last state check response status code").to.equal(200);
      sessionStartupDuration.add(res.durationSeconds * 1000)
    });
    describe("should query the server 10 times", () => {
      for (let i = 0; i < 10; i++) {
        const res = getServer(baseUrl, server.name);
        expect(res.status, 'response status').to.equal(200);
        expect(res).to.have.validJsonBody();
        sleep(1)
      }
    });
    describe("should hibernate the server", () => {
      const res = hibernateServer(baseUrl, server.name);
      expect(res.status, 'response status').to.equal(204);
    });
    describe("should wait for server to hibernate", () => {
      const res = waitForServerState(baseUrl, server.name, "hibernated")
      expect(res.stateAchieved, ".stateAchieved in response").to.be.true;
      expect(res.lastResponse.status, "last state check response status code").to.equal(200);
      sessionHibernationDuration.add(res.durationSeconds * 1000)
    });
    describe("should resume the server", () => {
      const res = resumeServer(baseUrl, server.name);
      expect(res.status, 'response status').to.equal(204);
    });
    describe("should wait for server to become ready after resuming", () => {
      const res = waitForServerState(baseUrl, server.name, "running")
      expect(res.stateAchieved, ".stateAchieved in response").to.be.true;
      expect(res.lastResponse.status, "last state check response status code").to.equal(200);
      sessionStartupDuration.add(res.durationSeconds * 1000)
    });
    describe("should stop the server", () => {
      const res = deleteServer(baseUrl, server.name);
      expect(res.status, 'response status').to.equal(204);
    });
    describe("should delete the project", () => {
      const res = deleteProjectByName(baseUrl, namespace + "/" + uuid)
      expect(res.status, 'response status').to.equal(202);
    });
  });
}
