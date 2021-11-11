# CA Certificates Handling for Renku

This is directly inspired and copied from the Gitlab Cloud Native Helm chart. See https://gitlab.com/gitlab-org/build/CNG/-/tree/master/alpine-certificates.

The image built from this repo should be added as an `initContainer` to all k8s deployments, pods, statefulsets, etc from the Renku Helm chart. 

Releasing, building and publishing the library Helm chart that specifies necessary `yaml` snippets are all done manually.

Please follow the steps below to publish a new certificates version:
1. Tag the commit from which the release is based with: `git tag certificates-X.X.X`
2. Push the tag: `git push --tags`
3. Navigate to the `certificates` folder from the root of this repository: `cd certificates`
4. Publish the chart: `chartpress --push --publish-chart --tag X.X.X`

The image that is used to generate the certificates is controlled by the other `chartpress.yaml` file that defines the Renku helm chart and is located at the root of this repository.