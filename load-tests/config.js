export const baseUrl = __ENV["BASE_URL"] || "https://dev.renku.ch";
export const gitUrl = __ENV["GIT_URL"] || "https://gitlab.dev.renku.ch";
export const registryDomain = __ENV["REGISTRY_DOMAIN"] || "repository.dev.renku.ch";
export const oldRenkuTemplateRef = __ENV["OLD_RENKU_TEMPLATE_REF"] || "0.4.0";
export const concurentUsers = __ENV["CONCURRENT_USERS"] || "3";

const credentialsFile = __ENV["CREDENTIALS_FILE"] || "/credentials.json";
export const credentials = JSON.parse(open(credentialsFile));
