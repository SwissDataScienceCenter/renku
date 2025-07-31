## Manual

1. Make sure you have the Renku Helm repository.

```
helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
```

2. Update the repo with the newest Helm chart.

```
helm repo update renku
```

3. Read the configuration section of the documentation and setup your values file.

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
a functional deployment.


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
