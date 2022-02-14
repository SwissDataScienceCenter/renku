# CA Certificates Handling for Renku

This is directly inspired and copied from the Gitlab Cloud Native Helm chart. See https://gitlab.com/gitlab-org/build/CNG/-/tree/master/alpine-certificates.

The image built from this repo should be added as an `initContainer` to all k8s deployments, pods, statefulsets, etc from the Renku Helm chart. 

Releasing, building and publishing this library Helm chart is all done manually.

Please follow the steps below to publish a new certificates version:
1. Tag with a proper semver tag that indicates that this is a new release of the certificates helm chart.
For this use a tag with the following format `X.X.X-certificates`. Please note that having something like
`certificates-X.X.X` is not a valid semver tag and will cause the CI pipeline to fail because Chartpress uses the tag
to name the Helm chart and the Helm chart version has to be proper semver. This tag does not trigger any automated
CI pipelines, its use is only so that we know when a new certificates chart was released.

```
git tag X.X.X-certificates
git push --tags
```

2. Navigate to the `certificates` folder and build the image, tagging with the same tag (i.e. `X.X.X`) as the certificates chart.

```
docker build -t renku/certificates:X.X.X .
docker push renku/certificates:X.X.X
```

3. Publish the certificates chart: `chartpress --publish-chart --tag X.X.X`
4. Update the tag of the certificates image and the certificates chart in the main Renku Helm chart and all dependent Helm charts 
