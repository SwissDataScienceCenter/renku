---
name: New release
about: Release a new version of Renku
title: Prep new release
labels: ''
assignees: ''

---

To release a new version of Renku:

- [ ] update `requirements.yaml` and run `helm dep update renku` to update `requirements.lock`
- [ ] test the deployment with these dependencies in a dev environment
- [ ] tag release candidate
- [ ] verify release candidate chart with a dev deployment
- [ ] make PR with changes
- [ ] after merging the PR, tag and make github release
