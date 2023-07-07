import { exec } from "child_process";

const runMatrix = {
  "public-project": "publicProject.cy.ts",
  "update-project": "updateProjects.cy.ts",
  "use-session": "useSession.cy.ts",
  "r-studio": "rstudioSession.cy.ts",
};

function getEnvValue(varName: string): string {
  if (!process.env[varName])
    throw Error(`FATAL ERROR: ${varName} is not known.`);

  return process.env[varName];
}

(async () => {
  try {
    const e2eFolder = process.env["E2E_FOLDER"] ? process.env["E2E_FOLDER"] : "";
    const runnerName = getEnvValue("RUNNER_NAME");
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
