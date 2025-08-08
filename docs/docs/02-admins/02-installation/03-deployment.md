---
title: Deployment
---

## Manual

1. Make sure you have the Renku Helm repository.

```
helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
```

2. Update the repository with the newest Helm chart.

```
helm repo update renku
```

3. Read the [Configuration section](02-configuration.md) of the documentation and setup your values file.

:::info
You can merge the different values file examples from different steps of the documentation
into a single file, or use multiple values file and let Helm merge them. 
For example, you can do the following:
```
helm upgrade --install -n renku -f values1.yaml -f values2.yaml renku renku/renku
```
And Helm will merge the files with the right-most file taking precedence in the case where
there are conflicts.
:::

4. Create a namespace for Renku, for example we usually call the namespace `renku`.

```
kubectl create ns renku
```

4. Install Renku.

```
helm upgrade --install -n renku -f values.yaml renku renku/renku
```

## Flux

1. Ensure that you have [flux](https://fluxcd.io/) installed and correctly setup in your cluster.

2. Configure your `HelmRepository` in flux.

```yaml
apiVersion: source.toolkit.fluxcd.io/v1beta1
kind: HelmRepository
metadata:
  name: renku
  namespace: renku
spec:
  interval: 5m
  url: https://swissdatasciencecenter.github.io/helm-charts/
```

3. Starter `HelmRelease`, you will need to populate the values file in here for
a functional deployment. See the [Configuration section](02-configuration.md) for more details.


```yaml
apiVersion: helm.toolkit.fluxcd.io/v2beta1
kind: HelmRelease
metadata:
  name: renku
spec:
  releaseName: renku
  chart:
    spec:
      chart: renku
      sourceRef:
        kind: HelmRepository
        name: renku
        namespace: renku
  timeout: 10m
  test:
    enable: true
  values:
    ### Refer to the Configuration section of the Admin documentation for the values file.
```

You can refer to the Flux documentation about 
[specifying multiple Helm chart values files](https://fluxcd.io/flux/components/helm/helmreleases/#values)
in Kubernetes `Secrets` or `ConfigMaps` if you don't want to merge all the examples from the documentation by hand.

:::warning
You should not be committing any secrets or passwords that may be required in the values file
in a Git repository if you are using Flux. Flux has different ways to handle sensitive values 
like this. The Flux documentation proposes two possible solutions, using 
[SOPS](https://fluxcd.io/flux/guides/mozilla-sops/) or [sealed secrets](https://fluxcd.io/flux/guides/sealed-secrets/).
:::
