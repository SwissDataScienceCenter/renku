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
    const runnerNumber = getEnvNumber("RUNNER_NUMBER");
    const specName = runMatrix["" + runnerNumber];

    console.log("running " + specName);
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
})();
