import { exec } from "child_process";
import * as core from "@actions/core";

function getFileName(): string {
  core.info(`Input variables:
  E2E_FOLDER: ${process.env["E2E_FOLDER"]},
  E2E_TEST: ${process.env["E2E_TEST"]},
  E2E_SUFFIX: ${process.env["E2E_SUFFIX"]}`);

  const e2eTest = process.env["E2E_TEST"];
  if (!e2eTest) core.setFailed(`FATAL ERROR: test name is empty.`);
  const e2eSuffix = process.env["E2E_SUFFIX"] ? process.env["E2E_SUFFIX"] : "";
  const e2eTestName = e2eTest + e2eSuffix;
  const e2eFolder = process.env["E2E_FOLDER"]
    ? process.env["E2E_FOLDER"].endsWith("/")
      ? process.env["E2E_FOLDER"]
      : process.env["E2E_FOLDER"] + "/"
    : "";
  const e2eFullName = e2eFolder + e2eTestName;
  core.info(`Target test: ${e2eFullName}`);
  return e2eFullName;
}

(async () => {
  try {
    const specName = getFileName();
    const command = `npm run e2e:ci ${specName}`;
    core.info(`Running the following command: ${command}`);

    // run and pipe output
    const execCommand = exec(command);
    if (execCommand.stdout) execCommand.stdout.pipe(process.stdout);
    if (execCommand.stderr) execCommand.stderr.pipe(process.stderr);

    execCommand.on("exit", (code) => {
      core.info(`Command ran with exit code ${code}.`);
      process.exit(code || 0);
    });
  }
  catch (err) {
    core.setFailed(`UNEXPECTED ERROR: ${err}`);
    process.exit(1);
  }
})();
