---
title: Configuration
---

The main purpose of this portion of the documentation is to guide administrators
on how to configure Renku prior to installation via the Helm chart values file.

## Values file

The source code for the Helm chart is located [here](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/renku). 
We strongly recommend checking the `values.yaml` as well as all templates if you are unsure how things are 
templated or how some portion of the values file affects what is created in your cluster.

:::info
When looking at the source code for the Helm chart make sure you have selected the right
version of Renku. The link above leads to the latest on our main branch which may not match what you
have deployed.
:::

### Resource requests and limits for Renku services

One of the most important setting in your values file is the amount of resources (i.e. CPU and memory)
for the different Renku services that will be deployed. We strongly recommend setting resource requests
for CPU and memory, and resource limits for memory that match the request. In addition to this, you should have
alarms on the memory usage for each service so that more memory can be provisioned when the memory usage
is high (e.g. >80% or 90% of the limit). Without tracking this or acting when memory usage spikes to 
do a sudden increase in users your Renku deployment will not run reliably and crucial services will
keep restarting or be permanently unavailable.

Here is an example values for a deployment that should handle approximately 50-100 active users per day.


```yaml
amalthea-sessions:
  controllerManager:
    manager:
      resources:
        limits:
          cpu: 1000m
          memory: 512Mi
        requests:
          cpu: 1000m
          memory: 512Mi
csi-rclone:
  csiControllerRclone:
    rclone:
      resources:
        limits:
          memory: 600Mi
        requests:
          cpu: 100m
          memory: 600Mi
  csiNodepluginRclone:
    rclone:
      resources:
        limits:
          memory: 128Mi
        requests:
          cpu: 100m
          memory: 128Mi
dataService:
  autoscaling:
    enabled: true
    maxReplicas: 5
    minReplicas: 2
    targetCPUUtilizationPercentage: false
    targetMemoryUtilizationPercentage: 75
  resources:
    limits:
      memory: 2048Mi
    requests:
      cpu: 1.3
      memory: 2048Mi
  dataTasks:
    resources:
      limits:
        memory: 300Mi
      requests:
        cpu: 500m
        memory: 300Mi
  k8sWatcher:
    resources:
      limits:
        memory: 500Mi
      requests:
        cpu: 400m
        memory: 500Mi
gateway:
  resources:
    limits:
      memory: 100Mi
    requests:
      cpu: 250m
      memory: 100Mi
  autoscaling:
    enabled: true
    maxReplicas: 7
    targetCPUUtilizationPercentage: 75
    targetMemoryUtilizationPercentage: 75
secretsStorage:
  replicaCount: 2
  resources:
    limits:
      memory: 750Mi
    requests:
      cpu: 10m
      memory: 750Mi
ui:
  client:
    replicaCount: 3
    resources:
      limits:
        memory: 512Mi
      requests:
        cpu: 200m
        memory: 512Mi
  server:
    autoscaling:
      cpuUtilization: 85
      enabled: true
      maxReplicas: 5
      minReplicas: 2
    resources:
      limits:
        memory: 512Mi
      requests:
        cpu: 1000m
        memory: 512Mi
```

:::info
This section discusses the needs for Renku services to handle a specific volume of users, excluding the
compute needs of the sessions that users will be creating.
You still need to provision enough resource to handle the compute requirement for the session(s)
of every user. This is done via resource pools and providing enough nodes in your Kubernetes cluster
and/or with node autoscaling.
:::

### Resource requests and limits for 3rd party services

These services are not developed by Renku but are used by the services we develop and maintain.

```yaml
postgresql:
  resources:
    requests:
      cpu: 3
      memory: 6000Mi
    limits:
      memory: 6000Mi
keycloakx:
  resources:
    limits:
      cpu: 1000m
      memory: 2Gi
    requests:
      cpu: 1000m
      memory: 2Gi
redis:
  replica:
    resources:
      limits:
        cpu: 2
        memory: 3.0Gi
      requests:
        cpu: 2
        memory: 3.0Gi
  sentinel:
    resources:
      requests:
        cpu: 1
        memory: 64Mi
solr:
  resources:
    limits:
      memory: 1536Mi
    requests:
      cpu: 1
      memory: 1536Mi
authz:
  resources:
    limits:
      memory: 500Mi
    requests:
      cpu: 100m
      memory: 500Mi
```
:::info
You may be able to run some of these services with fewer resources. The memory and CPU shown
here are just guides and good starting points for running Renku. Once you deploy and run Renku
for some time you can look at trends and modify things accordingly.
:::


## Harbor and Shipwright

As stated in the [Requirements section](requirements) both of these need to be installed
separately from Renku prior to installing Renku. You may also deploy Renku without Harbor and Shipwright
but then your users will not have the ability to build images from a code repository automatically.

### Configuration with Harbor and Shipwright

1. Create a Harbor project and a secret

This assumes that you have successfully installed Harbor and Shipwright and now you just
have to configure a Harbor project and a robot account that Renku will use.
You can create the project and robot account manually or utilize the scripts that can be found 
[here](https://github.com/SwissDataScienceCenter/renku/tree/master/scripts/harbor-init) 
in the Renku repository.

This is what is required:
- Public Harbor project.
- Robot account with permissions `list`, `pull`, `push`, `read`.
- Kubernetes secret of type `kubernetes.io/dockerconfigjson` which contains the
  credentials for the robot account and will allow Renku to push images into the Harbor repository.

2. Modify the Helm chart values file

The following section should be added to the values file you are using to install Renku.
Make sure you merge this into the proper sections of an existing file and you do not
end up duplicating sections that already exist. Also there are more options available for customization
such as which nodes should be used for builds and many others, for this prefer to the
[values file](https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml) in the Renku repository.

```yaml
dataService:
  imageBuilders:
    enabled: true
    outputImagePrefix: harbor.dev.renku.ch/renku-build/
    pushSecretName: renku-build-docker-secret
```

The file above is just an example you will have to modify the options shown as follows:
- `outputImagePrefix`: Should contain the harbor domain name and the name of the Harbor project
  you created in step 1 above. Please make sure you add a trailing `/` at the end. The example
  in the yaml snippet above is for Harbor deployed at the domain `harbor.dev.renku.ch` and for
  a Harbor project called `renku-build`.
- `pushSecretName`: The name the `kubernetes.io/dockerconfigjson` Kubernetes secret that you
  created in step 1 above. This secret will be used by Renku to push images in the Harbor repository. 
  The example in the yaml snippet above is for a secret called `renku-build-docker-secret` located
  in the same namespace as where Renku is installed.

- Label the node(s) you want to use for the builds with `renku.io/node-purpose: image-build`

### Build strategy

The last action required to have your system ready is to deploy the [BuildStrategy
for Shipwright](https://github.com/SwissDataScienceCenter/renku-data-services/blob/main/components/renku_pack_builder/manifests/buildstrategy.yaml).

### Configuration without Harbor and Shipwright

This is the default and no further steps are needed.
