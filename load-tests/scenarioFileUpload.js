import { uuidv4 } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
import { describe, expect } from 'https://jslib.k6.io/k6chaijs/4.3.4.2/index.js';

import { renkuLogin } from "./utils/oauth.js";
import { getRandomInt } from "./utils/general.js";
import { uploadRandomFile } from "./utils/core.js";
import { credentials, baseUrl, concurentUsers } from "./config.js";

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
  const fileName = `${uuid}.bin`;
  const numChunks = 100
  const chunkSizeBytes = 1e6

  describe("file upload load test scenario", () => {
    describe("user should login", () => {
      renkuLogin(baseUrl, ramdomCredentials);
    });
    describe("file should upload", () => {
      const fileUploadResponses = uploadRandomFile(
        baseUrl,
        uuid,
        fileName,
        numChunks,
        chunkSizeBytes,
      );
      expect(fileUploadResponses, "responses").to.have.lengthOf(numChunks);
      expect(fileUploadResponses, "responses to all have status codes 200").to.satisfy((responses) => responses.every((res) => res.status === 200));
      expect(fileUploadResponses, "responses to all not have errors").to.satisfy((responses) => responses.every((res) => res.json().error === undefined));
      const lastResponse = fileUploadResponses[fileUploadResponses.length - 1]
      expect(lastResponse.json(), "last response").to.have.nested.property("result.files[0].file_name", fileName);
    });
  });
}
