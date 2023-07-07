import { exec } from "child_process";

const runMatrix = {
  "public-project": "publicProject.cy.ts",
  "update-project": "updateProjects.cy.ts",
  "use-session": "useSession.cy.ts",
  "r-studio": "rstudioSession.cy.ts",
};

(async () => {
  try {
    const e2eFolder = process.env["E2E_FOLDER"] ? process.env["E2E_FOLDER"].toString() : "";
    const runnerName = process.env["RUNNER_NAME"].toString();
    const specName = runMatrix[runnerName];
    const command = `npm run e2e:ci ${e2eFolder}${specName}`;
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
