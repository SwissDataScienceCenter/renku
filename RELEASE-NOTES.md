# Preparing a Renku release

These are the steps that need to be followed to make a new release of Renku.

1. Make a list of new component versions to include in the release including *renku-python*.

2. If a new *renku-python* version is needed:

   a. Check if images in  are built using the desired version of *renku-python*. If not, modify `.travis.yml` file to include the desired version. Keep one line per minor version with the latest patch.

  ```
  env:
    - RENKU_VERSION=""
    - RENKU_VERSION="0.5.2"
    - RENKU_VERSION="0.6.1"
    - RENKU_VERSION="0.7.2"
    - RENKU_VERSION="0.8.2"
  ```

  b. Verify if the latest version of *renku-python* has a tag, otherwise make a new tag:

  ```
  $ git tag 0.4.2
  $ git push --tags
  ```

  c. After b. wait for travis to build the new images, verify they are present on dockerhub. For instance check that: https://hub.docker.com/r/renku/singleuser has a tag `0.4.2-renku-0.8.2`.

  d. Use this new image in a project, the gitlab CI/CD should be successful and when a notebook is launched the right version of renku should be displayed.

  e. If needed, update *renku-project-templates* to use the newly tagged images. For now, the two projects to look for are: `R-minimal` and `python-minimal`. Create a tag and update it in the renku `values.yaml` file (in a release branch).

3. Verify and update if necessary `requirements.yaml` with all the relevant versions and run `$ helm dep update renku` to update `requirements.lock`

4. Test a deployment with these dependencies in the `candidate.dev.renku.ch` deployment. Check that all the new features introduced after the last release work fine.

5. Tag release candidate locally `$ git tag 0.5.0-rc1`. Do `$ chartpress --tag 0.5.0-rc1 --push --publish-chart` then do `$ helm repo update` to get the latest charts. Alternatively, push the tag with `$ git push --tags` and let travis do the build.

6. Verify the release candidate chart by upgrading the stable dev deployment `stable.dev.renku.ch` (use the newly created version `--version 0.5.0-rc1`). This is to make sure that all the versions of everything are bundled correctly with the release.

7. Make a PR with the changes. Updates typically include requirements files and CHANGELOG.md.

8. After merging the PR, tag and make a github release. Notify Renku community about this.

9. If necessary, restore newer versions of components not included yet in the release with a post-release PR.
