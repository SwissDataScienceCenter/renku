---
title: Remote Clusters - Connection
---

:::warning[Under construction]
This is a first write-up, we welcome any constructive feedback to improve this documentation.
:::

The following describes how to connect a [Remote Cluster](./50-remote.md) with a Renku portal.

# Cluster

The first step is to define the remote cluster parameters, which are stored using the `/cluster` API endpoint.

The following paramters are defined by the remote cluster administrator, with the example values used below:

- protocol (HTTP or HTTPS)
- host: `sessions.example.org`
- port: `443`
- session_path: `/sessions`
- session_ingress_class_name: `renku-user-session-ingress-class`
- session_ingress_annotations
- session_storage_class: `renku-user-session-storage-class`
- service_account_name: `renku-session-manager`
- session_tls_secret_name, or if the default settings need to be used

Here is an example payload, assuming `remote-cluster.yaml` as the name of the secret containing the connection settings for the Kubernetes API endpoint. Adapt as required the values:

```json
{
  "name": "Remote Cluster",
  "config_name": "remote-cluster.yaml",
  "session_protocol": "https",
  "session_host": "sessions.example.org",
  "session_port": 443,
  "session_path": "/sessions",
  "session_ingress_class_name": "renku-user-session-ingress-class",
  "session_ingress_annotations": {},
  "session_storage_class": "renku-user-session-storage-class",
  "service_account_name": "renku-session-manager",
  "session_tls_secret_name": "",
  "session_ingress_use_default_cluster_tls_cert": true
}
```

### Notes

- When using `"session_ingress_use_default_cluster_tls_cert": true`,
  _we have to set_ `"session_tls_secret_name": ""` as well, otherwise the API call will fail.

# Resource Pool

Once the cluster connection has been defined, you can use the GET operation to retrieve the cluster connection descriptor, and from there retrieve the associated ULID and create a resource pool which is linked to it.

The following paramters are defined by the remote cluster administrator, with the example values used below:

- quota.id: `renku-user-sessions-priority`, which is the priority class name to use.

Quota parameters & classes should also be discussed with / provided by the remote cluster administrators as well, so that it matches the resources of the remote cluster.

Again, adapt the following example resource pool description as needed:

```json
{
  "quota": {
    "cpu": 5,
    "memory": 250,
    "gpu": 0,
    "id": "renku-user-sessions-priority"
  },
  "public": false,
  "default": false,
  "classes": [
    {
      "name": "remote-default",
      "cpu": 0.1,
      "memory": 1,
      "gpu": 0,
      "max_storage": 4,
      "default_storage": 1,
      "default": true
    },
    {
      "name": "remote-small",
      "cpu": 0.1,
      "memory": 1,
      "gpu": 0,
      "max_storage": 4,
      "default_storage": 1,
      "default": false
    },
    {
      "name": "remote-medium",
      "cpu": 0.5,
      "memory": 2,
      "gpu": 0,
      "max_storage": 4,
      "default_storage": 1,
      "default": false
    }
  ],
  "cluster_id": "${REMOTE_CLUSTER_ULID}",
  "name": "Remote Cluster"
}
```

# Update Keycloak configuration

Adapt the keycloak callback URL in the `renku-jupyterserver` client in the keycloack admin panel of the Renku realm by adding the base url of the external cluster, in both `Valid redirect URLs` and `Web origins`. This should look like:

```
https://www.example.org/*
```

# Kubeconfig Secret

Create or update a secret containing a map of Kubeconfigs, one per remote clusters like below.

- Update the namespace in the metadata of the secret
- The config map key, which is exposed as the filename once mounted, should match the value provided above in the Cluster parameters, in our example `remote-cluster.yaml`.
- Add the renku user sessions namespace (provided by the remote cluster administrators) in the context key

```yaml
context:
  cluster: a-cluster
  namespace: ${RENKU_USER_SESSIONS_NAMESPACE}
  user: renku-service-account-user
```

Full example, assuming the renku web portal runs under the `renku` namespace:

```yaml
kind: Secret
apiVersion: v1
metadata:
  name: "data-service-secret"
  namespace: "renku"
stringData:
  remote-cluster.yaml: |
    apiVersion: v1
    kind: Config
    clusters:
    - name: a-cluster
      cluster:
        server: ${K8S_REMOTE_HOST_ENDPOINT}
        certificate-authority-data: ${CERT_DATA}
    users:
    - name: renku-service-account-user
      user:
        token: ${TOKEN}
    contexts:
    - name: a-context
      context:
        cluster: a-cluster
        namespace: renku-user-sessions
        user: renku-service-account-user
    current-context: a-context
```

# Update the core deployments

The `renku-data-service`, `renku-k8s-watcher`, and `renku-secrets-storage` deployments need to be adapted to mount the secret containing the kube_configs in the corresponding containers for:

- data-service
- k8s-watcher
- secrets-storage

### `volumeMounts` array:

```yaml
spec:
  template:
    spec:
      containers:
        name: data-service
        volumeMounts:
          - mountPath: /secrets/kube_configs
            name: kube-configs
            readOnly: true
```

### `volumes` array:

```yaml
  spec:
    template:
      spec:
        volumes:
    	  - name: kube-configs
            secret:
              secretName: data-service-secret
```

As a check, you may watch the logs as each service will announce all the kubeconfigs being loaded as they restart, or during their first remote connections.
