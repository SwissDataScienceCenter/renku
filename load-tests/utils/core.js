import http from "k6/http";
import { Trend } from "k6/metrics";
import { URL } from "https://jslib.k6.io/url/1.0.0/index.js";
import crypto from "k6/crypto";

const coreHttpReqDuration = new Trend("http_req_duration_core", true);

export function createProject(baseUrl, repoUrl, templateId, projectName, projectNamespace, ref = null) {
  const payload = {
    project_repository: repoUrl,
    project_namespace: projectNamespace,
    project_name: projectName,
    identifier: templateId,
    url: "https://github.com/SwissDataScienceCenter/renku-project-template",
    ref: ref,
  };
  let res = http.post(
    `${baseUrl}/ui-server/api/renku/templates.create_project`,
    JSON.stringify(payload),
    { headers: { "Content-Type": "application/json" } },
  );
  coreHttpReqDuration.add(res.timings.duration)
  return res;
}

export function migrateProject(baseUrl, repoUrl) {
  const migratePayload = {
    force_template_update: true,
    git_url: repoUrl,
    is_delayed: false,
    skip_docker_update: false,
    skip_migrations: false,
    skip_template_update: false,
  };
  const res = http.post(
    `${baseUrl}/ui-server/api/renku/cache.migrate`,
    JSON.stringify(migratePayload),
    { headers: { "Content-Type": "application/json" } }
  );
  coreHttpReqDuration.add(res.timings.duration)
  return res
}

export function projectInfo(baseUrl, repoUrl) {
  const payload = {
    git_url: repoUrl,
    is_delayed: false,
    migrate_project: false,
  };
  const res = http.post(
    `${baseUrl}/ui-server/api/renku/project.show`,
    JSON.stringify(payload),
    { headers: { "Content-Type": "application/json" } }
  );
  coreHttpReqDuration.add(res.timings.duration)
  return res;
}

export function getTemplates(baseUrl, ref=null) {
  let res = http.get(
    `${baseUrl}/ui-server/api/renku/templates.read_manifest?url=https%3A%2F%2Fgithub.com%2FSwissDataScienceCenter%2Frenku-project-template`
  );
  if (ref) res = res + `&ref=${ref}`;
  coreHttpReqDuration.add(res.timings.duration)
  return res
}

export function uploadRandomFile(baseUrl, uuid, fileName, numChunks, chunkSizeBytes) {
  const responses = [];
  for (let i = 0; i < numChunks; i++) {
    const url = new URL(`ui-server/api/renku/cache.files_upload`, baseUrl);
    url.searchParams.append("dzuuid", uuid);
    url.searchParams.append("dzchunkindex", i);
    url.searchParams.append("dztotalfilesize", numChunks * chunkSizeBytes);
    url.searchParams.append("dzchunksize", chunkSizeBytes);
    url.searchParams.append("dztotalchunkcount", numChunks);
    url.searchParams.append("dzchunkbyteoffset", i * chunkSizeBytes);
    url.searchParams.append("chunked_content_type", "application/octet-stream");
    const res = http.post(url.toString(), {
      file: http.file(
        crypto.randomBytes(chunkSizeBytes),
        fileName,
        "application/octet-stream"
      ),
    });
    coreHttpReqDuration.add(res.timings.duration)
    responses.push(res);
  }
  console.log(responses.length)
  return responses;
}
