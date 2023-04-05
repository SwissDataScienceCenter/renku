import http from "k6/http";
import { uuidv4 } from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
import { Trend } from "k6/metrics";

const gitHttpReqDuration = new Trend("http_req_duration_git", true);

export function deleteProjectById(baseUrl, projectId) {
  const res = http.del(`${baseUrl}/ui-server/api/projects/${projectId}`)
  gitHttpReqDuration.add(res.timings.duration)
  return res;
}

export function deleteProjectByName(baseUrl, namespace_name) {
  const res = http.del(`${baseUrl}/ui-server/api/projects/${encodeURIComponent(namespace_name)}`)
  gitHttpReqDuration.add(res.timings.duration)
  return res;
}

export function getCommitShasFromProjectId(baseUrl, projectId) {
  const res = http.get(
    `${baseUrl}/ui-server/api/projects/${projectId}/repository/commits`
  );
  gitHttpReqDuration.add(res.timings.duration)
  return res;
}

export function getCommitShasFromProjectName(baseUrl, namespace_name) {
  const res = http.get(
    `${baseUrl}/ui-server/api/projects/${encodeURIComponent(namespace_name)}/repository/commits`
  );
  gitHttpReqDuration.add(res.timings.duration)
  return res;
}

export function getGitProjectInfo(baseUrl, projectId) {
  let projectData = http.get(
    `${baseUrl}/ui-server/api/projects/${projectId}`
  );
  gitHttpReqDuration.add(projectData.timings.duration)
  return projectData
}

export function forkProject(baseUrl, projectId) {
  const uuid = uuidv4();
  let projectData = getGitProjectInfo(baseUrl, projectId)
  check(projectData, {
    "response code for getting project info was 2XX": (res) =>
      res.status >= 200 && res.status < 300,
  });
  projectData = projectData.json();
  let forkName = `test-forked-project-${uuid}`;
  const payload = {
    id: gitlabProjectId,
    name: forkName,
    namespace_id: projectData.namespace.id,
    path: forkName,
    visibility: projectData.visibility,
  };
  let forkRes = http.post(
    `${baseUrl}/ui-server/api/projects/${gitlabProjectId}/fork`,
    payload
  );
  gitHttpReqDuration.add(forkRes.timings.duration)
  return forkRes;
}
