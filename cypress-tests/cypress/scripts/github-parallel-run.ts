import { exec } from "child_process";

const runMatrix = {
  "publicProject": "publicProject.cy.ts",
  "updateProject": "updateProjects.cy.ts",
  "useSession": "useSession.cy.ts",
  "rStudio": "rstudioSession.cy.ts",
};

(async () => {
  try {
    const e2eFolder = process.env["E2E_FOLDER"] ? process.env["E2E_FOLDER"].toString() : "";
    const runnerName = process.env["RUNNER_NAME"] ? process.env["RUNNER_NAME"].toString() : "";
    const specName = e2eFolder + runMatrix[runnerName];
    const command = `npm run e2e:ci ${specName}`;
    console.log(`Running  ${runnerName}. Command: ${command}`);

    // run and pipe output
    const execCommand = exec(command);

    if (execCommand.stdout)
      execCommand.stdout.pipe(process.stdout);

    if (execCommand.stderr)
      execCommand.stderr.pipe(process.stderr);

    execCommand.on("exit", (code) => {
      process.exit(code || 0);
    });
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
})();
