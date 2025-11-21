---
title: Remote sessions
---

From version 2.9.0, Renku supports running remote sessions on compute infrastructure where
workloads can be started on behalf of users with an API.

At the moment, Renku only supports starting remote sessions on High-Performance Computing (HPC)
resources using the [FirecREST API](https://eth-cscs.github.io/firecrest-v2/).

The technical details of remote sessions are explained on the [Amalthea repository](https://github.com/SwissDataScienceCenter/amalthea/blob/main/new.README.md#developer-documentation-remote-sessions).

## Configuring remote sessions at CSCS

This section describes how to set up remote sessions at [CSCS](https://cscs.ch),
which uses the FirecREST API to start remote sessions.
This example can be adapted to allow users of your Renku instance to start
sessions at another HPC infrastructure.

### 1. Prerequisites

As an Renku admin, you must first create or obtain the OAuth 2.0 configuration which will
be used to connect Renku users with CSCS.

### 2. Add a new integration

The first step is to add a new integration using the OAuth 2.0 configuration by clicking "Add Service Provider".

Then, fill the "Add provider" form as follows:

* Id: `cscs.ch`
* Kind: `Generic OIDC`
* Application slug: _leave blank_
* Application slug
* Display Name: `CSCS`
* URL: `https://cscs.ch`
* Use PKCE: _leave unchecked_
* Client ID: Fill in the `CLIENT_ID` (OAuth 2.0 configuration)
* Client Secret: Fill in the `CLIENT_ID` (OAuth 2.0 configuration)
* Scope: `default offline_access openid profile email`
* Image registry URL: _leave blank_
* OpenID Connect Issuer URL: `https://auth.cscs.ch/auth/realms/cscs`

Once the new integration has been created, you should test it by clicking "Connect" on
the corresponding entry in the "Integration" page (see [testing integrations](integrations#testing-integrations)).

### 3. Add resource pools which start remote sessions

The next step is to create resource pools which start remote sessions.
At the moment, the admin panel is not up-to-date with this feature, so we
will have to use the swagger page (usually located at `https://<your-renku-domain>/swagger/`).

Under the `resource_pools` section, find the `POST /resource_pools` API endpoint and
send the following request body:


```json
{
  "quota": {
    "cpu": 2560,
    "memory": 5120,
    "gpu": 0
  },
  "classes": [
    {
      "name": "eiger",
      "cpu": 256,
      "memory": 512,
      "gpu": 0,
      "max_storage": 10,
      "default_storage": 1,
      "default": true,
      "tolerations": [],
      "node_affinities": []
    }
  ],
  "name": "CSCS - Eiger - Debug",
  "public": false,
  "default": false,
  "remote": {
    "kind": "firecrest",
    "provider_id": "cscs.ch",
    "api_url": "https://api.cscs.ch/hpc/firecrest/v2/",
    "system_name": "eiger",
    "partition": "debug"
  }
}
```

This creates a resource pool which is configured to start remote sessions with the following details:

* The `remote.system_name` field specifies that session jobs will be submitted to the `eiger` cluster (see [Eiger](https://docs.cscs.ch/clusters/eiger/#eiger)).
* The `remote.provider_id` field specifies that Renku will use the `cscs.ch` integration to submit the session job on behalf of the user.
* The `remote.api_url` field specifies the FirecREST API URL.
* The optional field `remote.partition` field specifies the SLURM partition to use (here the `debug` one).

More resource pools can be configured to give access to different HPC clusters.

### 4. Test the remote sessions

To test that the remote sessions at CSCS work properly, you will need the following:

1. Open the admin panel and add yourself to the remote resource pools we just created
2. Make sure you have an account at CSCS with at least one active project (e.g. you can start jobs with the `srun` command)

Create a new project and then create a new session launcher using the `ghcr.io/swissdatasciencecenter/renku/py-datascience-jupyterlab` image.

If the `py-datascience-jupyterlab` image is not available as one of the global environments, you can create a new external environment with
the following settings:

* Container image: `ghcr.io/swissdatasciencecenter/renku/py-datascience-jupyterlab`
* Default URL: `/`
* Mount directory: `/home/renku/work`
* Work directory: `/home/renku/work`
* Port: `8888`
* GID: `1000`
* UID: `1000`
* Command: _leave blank_
* Args: _leave blank_
* Strip path prefix: _leave unchecked_

At the next step, select "Session launcher compute resources" and pick one of the remote resource pools you created in step 3.

Now you can launch the session.

### Troubleshooting

If the session fails to start because the SLURM account is incorrect, you can specify it as an environment variable
in the session launcher.

Open the session launcher's off-canvas, then scroll down to the "Environment Variables" section and add the following:
* `SLURM_ACCOUNT`: `<your CSCS group>`
