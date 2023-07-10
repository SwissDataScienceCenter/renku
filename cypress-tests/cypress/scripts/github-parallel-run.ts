import { exec } from "child_process";
import * as core from "@actions/core";

function getFileName(): string {
  const name = process.argv[2];
  core.info(`Name from argument: ${name}`);
  core.info(`Name from env: ${process.env["E2E_TEST"]}`);

  const e2eTest = process.env["E2E_TEST"];
  if (!e2eTest) core.setFailed(`FATAL ERROR: test name is empty.`);

  const e2eSuffix = process.env["E2E_SUFFIX"] ? process.env["E2E_SUFFIX"] : "";
  const e2eTestName = e2eTest + e2eSuffix;

  // TODO: remove me - from here
  core.info(`testing: full name "${e2eTestName}"`);
  core.info(`testing: env direct "${process.env["E2E_TEST"]}"`);
  core.info(`testing: argument "${name}"`);
  core.info(`testing: argument direct "${process.argv[2]}"`);
  // TODO: remove me - to here

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
