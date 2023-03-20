function getEnv(name) {
  let val = __ENV[name];
  if ((val === undefined) || (val === null)) {
      throw new Error("missing env var for " + name);
  }
  return val;
}

export const baseUrl = getEnv("BASE_URL");
export const gitUrl = getEnv("GIT_URL");
export const registryDomain = getEnv("REGISTRY_DOMAIN");
export const oldRenkuTemplateRef = __ENV["OLD_RENKU_TEMPLATE_REF"] || "0.4.0";
export const concurentUsers = __ENV["CONCURRENT_USERS"] || "3";

const credentialsFile = getEnv("CREDENTIALS_FILE");
export const credentials = JSON.parse(open(credentialsFile));
