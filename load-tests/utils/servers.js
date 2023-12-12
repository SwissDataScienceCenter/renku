import http from "k6/http";
import { Trend } from "k6/metrics";
import { sleep } from "k6";

const notebooksGetHttpReqDuration = new Trend("http_req_duration_get_notebooks", true);
const notebooksGetImagesHttpReqDuration = new Trend("http_req_duration_get_notebooks_images", true);
const notebooksPostHttpReqDuration = new Trend("http_req_duration_post_notebooks", true);
const notebooksPatchHttpReqDuration = new Trend("http_req_duration_patch_notebooks", true);
const notebooksDeleteHttpReqDuration = new Trend("http_req_duration_delete_notebooks", true);

export function stopServer(baseUrl, serverName) {
  const res = http.del(
    `${baseUrl}/ui-server/api/notebooks/servers/${serverName}`
  )
  notebooksDeleteHttpReqDuration.add(res.timings.duration)
  return res;
}

export function getServer(baseUrl, serverName) {
  const res = http.get(`${baseUrl}/ui-server/api/notebooks/servers/${serverName}`);
  notebooksGetHttpReqDuration.add(res.timings.duration)
  return res
}

export function startServer(baseUrl, commitSha, namespace, projectName, serverOptions = {}) {
  const payload = {
    commit_sha: commitSha,
    namespace: namespace,
    project: projectName,
    serverOptions: serverOptions,
  };

  const res = http.post(
    `${baseUrl}/ui-server/api/notebooks/servers`,
    JSON.stringify(payload),
    { headers: { "Content-Type": "application/json" } }
  );
  notebooksPostHttpReqDuration.add(res.timings.duration)
  return res
}

export function hibernateServer(baseUrl, serverName) {
  const payload = {
    state: "hibernated",
  };

  const res = http.patch(
    `${baseUrl}/ui-server/api/notebooks/servers/${serverName}`,
    JSON.stringify(payload),
    { headers: { "Content-Type": "application/json" } }
  );
  notebooksPatchHttpReqDuration.add(res.timings.duration)
  return res
}

export function resumeServer(baseUrl, serverName) {
  const payload = {
    state: "running",
  };

  const res = http.patch(
    `${baseUrl}/ui-server/api/notebooks/servers/${serverName}`,
    JSON.stringify(payload),
    { headers: { "Content-Type": "application/json" } }
  );
  notebooksPatchHttpReqDuration.add(res.timings.duration)
  return res
}

export function waitForServerState(baseUrl, serverName, state, secondsTimeout = 600, callback = null) {
  let resJson, res;
  const start = Date.now();
  do {
    sleep(1);
    res = getServer(baseUrl, serverName)
    notebooksGetHttpReqDuration.add(res.timings.duration)
    resJson = res.json()
    if (callback) {
      callback(res)
    }
    if ((Date.now() - start) / 1000 > secondsTimeout) {
      console.log(`Waiting for server ${serverName} to reach state ${state} timed out with response: ${res.body} and status ${res.status}`);
      break;
    }
  } while (
    resJson.status === undefined ||
    resJson.status.state != state
  );
  return { lastResponse: res, stateAchieved: resJson.status.state == state, durationSeconds: (Date.now() - start) / 1000 };
}

export function waitForImageToBuild(baseUrl, registryDomain, namespace, name, commitSha, secondsTimeout = 600) {
  const imageName = `${registryDomain}/${namespace}/${name}:${commitSha.substring(0, 7)}`
  let res;
  const start = Date.now();
  do {
    sleep(1);
    res = http.get(`${baseUrl}/ui-server/api/notebooks/images?image_url=${encodeURIComponent(imageName)}`)
    notebooksGetImagesHttpReqDuration.add(res.timings.duration)
    if ((Date.now() - start) / 1000 > secondsTimeout) {
      console.log(`Waiting for image ${imageName} to become ready timed out with response: ${res.body} and status ${res.status}`)
      break;
    }
  } while (
    res.status != 200
  );
  return { imageBuilt: res.status == 200, durationSeconds: (Date.now() - start) / 1000 };
}
