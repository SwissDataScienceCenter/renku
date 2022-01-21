# CA Certificates Handling for Renku

This is directly inspired and copied from the Gitlab Cloud Native Helm chart. See https://gitlab.com/gitlab-org/build/CNG/-/tree/master/alpine-certificates.

The image built from this repo should be added as an `initContainer` to all k8s deployments, pods, statefulsets, etc from the Renku Helm chart. 

Releasing, building and publishing this library Helm chart is all done manually.

Please follow the steps below to publish a new certificates version:
1. Navigate to the `certificates` folder and build the image, tagging with the same tag (i.e. `X.X.X`) as the certificates chart.

```
docker build -t renku/certificates:X.X.X .
docker push renku/certificates:X.X.X
```

2. Publish the certificates chart: `chartpress --publish-chart --tag X.X.X`
3. Update the tag of the certificates image and the certificates chart in the main Renku Helm chart and all dependent Helm charts 
