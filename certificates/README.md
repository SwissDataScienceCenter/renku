# CA Certificates Handling for Renku

This is directly inspired and copied from the Gitlab Cloud Native Helm chart. See https://gitlab.com/gitlab-org/build/CNG/-/tree/master/alpine-certificates.

The image built from this repo should be added as an `initContainer` to all k8s deployments, pods, statefulsets, etc from the Renku Helm chart. 