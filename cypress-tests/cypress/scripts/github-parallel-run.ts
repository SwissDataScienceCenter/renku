import { exec } from "child_process";
import * as core from "@actions/core";

function getFileName(): string {
  const e2eSuffix = process.env["E2E_SUFFIX"] ? process.env["E2E_SUFFIX"] : "";
  const e2eTest = process.env["E2E_TEST"] ? process.env["E2E_TEST"] : "";
  const e2eTestName = e2eSuffix + e2eTest;

  if (!e2eTestName) core.setFailed(`FATAL ERROR: test name is empty.`);

  const e2eFolder = process.env["E2E_FOLDER"]
    ? process.env["E2E_FOLDER"].endsWith("/")
      ? process.env["E2E_FOLDER"]
      : process.env["E2E_FOLDER"] + "/"
    : "";
  const e2eFullName = e2eFolder + e2eTestName;
  core.debug(`Target test: ${e2eFullName}`);
  return e2eFullName;
}

(async () => {
  try {
    const specName = getFileName();
    core.info(`Working on "${specName}"`);
    const command = `npm run e2e:ci ${specName}`;
    core.info(`Running the following command: ${command}`);

    // run and pipe output
    const execCommand = exec(command);
    if (execCommand.stdout) execCommand.stdout.pipe(process.stdout);
    if (execCommand.stderr) execCommand.stderr.pipe(process.stderr);

    execCommand.on("exit", (code) => {
      core.info(`Command  with exit code ${code}.`);
      process.exit(code || 0);
    });
  }
  catch (err) {
    core.setFailed(`UNEXPECTED ERROR: ${err}`);
    process.exit(1);
  }
})();
