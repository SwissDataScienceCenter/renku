import { exec } from "child_process";

const runMatrix = {
  "0": "publicProject.cy.ts",
  "1": "updateProjects.cy.ts",
  "2": "useSession.cy.ts",
  "3": "rstudioSession.cy.ts",
};

function getEnvNumber(varName: string): number {
  if (!process.env[varName] === undefined || isNaN(Number(process.env[varName])))
    throw Error(`FATAL ERROR: ${varName} is not known.`);

  return Number(process.env[varName]);
}

(async () => {
  try {
    const e2eFolder = process.env["E2E_FOLDER"] ? process.env["E2E_FOLDER"].toString() : "";
    const runnerNumber = getEnvNumber("RUNNER_NUMBER");
    const specName = e2eFolder + runMatrix["" + runnerNumber];

    const command = `npm run e2e:ci ${specName}`;
    console.log(`Running  ${runnerNumber}. Command: ${command}`);

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
