# -*- coding: utf-8 -*-
#
# Copyright 2018 - Swiss Data Science Center (SDSC)
# A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
# Eidgenössische Technische Hochschule Zürich (ETHZ).
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Implement integration for using GitLab repositories."""

import hashlib
import os
import string
from urllib.parse import urlsplit, urlunsplit

import escapism
from tornado import gen, web


class SpawnerMixin():
    """Extend spawner methods."""

    @gen.coroutine
    def git_repository(self):
        """Return the URL of current repository."""
        auth_state = yield self.user.get_auth_state()

        options = self.user_options
        namespace = options.get('namespace')
        project = options.get('project')

        url = os.environ.get('GITLAB_URL', 'http://gitlab.renku.build')

        scheme, netloc, path, query, fragment = urlsplit(url)

        repository = urlunsplit((
            scheme, 'oauth2:' + auth_state['access_token'] + '@' + netloc,
            path + '/' + namespace + '/' + project + '.git', query, fragment
        ))

        return repository

    def get_env(self):
        """Extend environment variables passed to the notebook server."""
        # TODO how to get the async result here?
        #      repository = yield from self.git_repository()

        environment = super().get_env()
        environment.update({
            'CI_NAMESPACE':
                self.user_options.get('namespace', ''),
            'CI_PROJECT':
                self.user_options.get('project', ''),
            'CI_COMMIT_SHA':
                self.user_options.get('commit_sha', ''),
            'GITLAB_URL':
                os.environ.get('GITLAB_URL', 'http://gitlab.renku.build'),
            'CI_REF_NAME':
                self.user_options.get('branch', 'master'),
            'EMAIL':
                self.gl_user.email,
            'GIT_AUTHOR_NAME':
                self.gl_user.name,
            'GIT_COMMITTER_NAME':
                self.gl_user.name,
        })
        return environment

    @gen.coroutine
    def start(self, *args, **kwargs):
        """Start the notebook server."""
        import gitlab

        self.log.info(
            "starting with args: {}".format(' '.join(self.get_args()))
        )
        self.log.debug("user options: {}".format(self.user_options))

        auth_state = yield self.user.get_auth_state()
        assert 'access_token' in auth_state

        # 1. check authorization against GitLab
        options = self.user_options
        namespace = options.get('namespace')
        project = options.get('project')
        commit_sha = options.get('commit_sha')
        commit_sha_7 = commit_sha[:7]

        url = os.getenv('GITLAB_URL', 'http://gitlab.renku.build')

        gl = gitlab.Gitlab(
            url, api_version=4, oauth_token=auth_state['access_token']
        )

        try:
            gl.auth()
            gl_project = gl.projects.get('{0}/{1}'.format(namespace, project))
            self.gl_user = gl.user
            self.log.info(
                'Got user profile: {}'.format(self.gl_user)
            )
            access_level = gl_project.members.get(self.gl_user.id).access_level
        except Exception as e:
            self.log.error(e)
            raise web.HTTPError(401, 'Not authorized to view project.')
            return

        if access_level < gitlab.DEVELOPER_ACCESS:
            raise web.HTTPError(401, 'Not authorized to view project.')
            return

        # set default image
        self.image = os.getenv(
            'JUPYTERHUB_NOTEBOOK_IMAGE', 'renku/singleuser:latest'
        )

        for pipeline in gl_project.pipelines.list():
            if pipeline.attributes['sha'] == commit_sha:
                status = self._get_job_status(pipeline, 'image_build')

                if not status:
                    # there is no image_build job for this commit
                    # so we use the default image
                    self.log.info('No image_build job found in pipeline.')
                    break

                # we have an image_build job in the pipeline, check status
                while True:
                    if status == 'success':
                        # the image was built
                        # it *should* be there so lets use it
                        self.image = '{image_registry}'\
                                '/{namespace}'\
                                '/{project}'\
                                ':{commit_sha_7}'.format(
                                    image_registry=os.getenv('IMAGE_REGISTRY'),
                                    commit_sha_7=commit_sha_7,
                                    **options
                        ).lower()
                        self.log.info(
                            'Using image {image}.'.format(image=self.image)
                        )
                        break
                    elif status in {'failed', 'canceled'}:
                        self.log.info(
                            'Image build failed for project {0} commit {1} - '
                            'using {2} instead'.format(
                                project, commit_sha, self.image
                            )
                        )
                        break
                    yield gen.sleep(5)
                    status = self._get_job_status(pipeline, 'image_build')
                    self.log.debug(
                        'status of image_build job for commit '
                        '{commit_sha_7}: {status}'.format(
                            commit_sha_7=commit_sha_7, status=status
                        )
                    )

        self.cmd = 'jupyterhub-singleuser'
        try:
            result = yield super().start(*args, **kwargs)
        except docker.errors.ImageNotFound:
            self.log.info(
                'Image {0} not found - using default image.'.format(
                    self.image
                )
            )
            self.image = os.getenv(
                'JUPYTERHUB_NOTEBOOK_IMAGE', 'jupyter/minimal-notebook'
            )
            result = yield super().start(*args, **kwargs)

        return result

    @staticmethod
    def _get_job_status(pipeline, job_name):
        """Helper method to retrieve job status based on the job name."""
        status = [
            job.attributes['status'] for job in pipeline.jobs.list()
            if job.attributes['name'] == job_name
        ]
        return status.pop() if status else None


try:
    import docker
    from dockerspawner import DockerSpawner

    class RepoVolume(DockerSpawner):
        """Create and configure repo volume."""

        @gen.coroutine
        def start(self):
            """Create init container."""
            auth_state = yield self.user.get_auth_state()
            options = self.user_options
            name = self.name + '-git-repo'
            safe_username = escapism.escape(
                self.user.name,
                safe=set(string.ascii_lowercase + string.digits + '-'),
                escape_char='-'
            )
            container_name = 'init-' + safe_username + '-' + self.name
            volume_name = 'repo-' + safe_username + '-' + container_name
            volume_path = '/home/jovyan/work'

            try:
                yield self.docker(
                    'remove_container', container_name, force=True
                )
            except Exception as e:
                self.log.error(e)
            try:
                yield self.docker('remove_volume', volume_name, force=True)
            except Exception as e:
                self.log.error(e)

            host_config = yield self.docker(
                'create_host_config',
                network_mode=self.network_name,
                binds={
                    volume_name: {
                        'bind': volume_path,
                        'mode': 'rw',
                    },
                },
            )

            # make sure we have the alpine/git image
            images = yield self.docker('images')
            if not any([
                'alpine/git:latest' in i['RepoTags']
                for i in images if i['RepoTags']
            ]):
                alpine_git = yield self.docker(
                    'pull', 'alpine/git', tag='latest'
                )

            volume = yield self.docker('create_volume', name=volume_name)

            # 1. clone the repo
            # 2. checkout the environment branch and commit sha
            # 3. set jovyan as owner
            repository = yield self.git_repository()
            container = yield self.docker(
                'create_container',
                'alpine/git',
                name=container_name,
                entrypoint='sh -c',
                command=[
                    'apk update && apk add git-lfs && '
                    'git clone {repository} {volume_path} && '
                    '(git checkout {branch} || git checkout -b {branch}) && '
                    'git submodule init && git submodule update && '
                    'git reset --hard {commit_sha} && '
                    'git lfs install --local &&'
                    'chown 1000:100 -Rc {volume_path}'.format(
                        branch=options.get('branch'),
                        commit_sha=options.get('commit_sha'),
                        repository=repository,
                        volume_path=volume_path,
                    ),
                    volume_path,
                ],
                volumes=[volume_path],
                working_dir=volume_path,
                host_config=host_config,
            )
            started = yield self.docker('start', container=container.get('Id'))
            wait = yield self.docker('wait', container=container)

            # TODO remove the container?
            # yield self.docker(
            #     'remove_container', container.get('Id'), force=True)
            environment = self.get_env()
            environment['CI_REPOSITORY_URL'] = repository

            extra_create_kwargs = {
                'working_dir': volume_path,
                'environment': environment,
                'volumes': [volume_path],
            }
            extra_host_config = {
                'binds': {
                    volume_name: {
                        'bind': volume_path,
                        'mode': 'rw',
                    },
                },
            }

            result = yield super().start(
                extra_create_kwargs=extra_create_kwargs,
                extra_host_config=extra_host_config,
            )
            return result

    class RenkuDockerSpawner(SpawnerMixin, RepoVolume, DockerSpawner):
        """A class for spawning notebooks on Renku-JupyterHub using Docker."""

except ImportError:
    pass

try:
    from kubernetes import client
    from kubespawner import KubeSpawner

    class RenkuKubeSpawner(SpawnerMixin, KubeSpawner):
        """A class for spawning notebooks on Renku-JupyterHub using K8S."""

        @gen.coroutine
        def get_pod_manifest(self):
            """Include volume with the git repository."""
            auth_state = yield self.user.get_auth_state()
            repository = yield self.git_repository()
            options = self.user_options
            commit_sha_7 = options.get('commit_sha')[:7]

            # https://gist.github.com/tallclair/849601a16cebeee581ef2be50c351841
            container_name = 'git-clone'
            name = self.pod_name + '-git-repo'

            # set the notebook container image
            self.image_spec = self.image

            #: Define a new empty volume.
            self.volumes = [
                volume for volume in self.volumes if volume['name'] != name
            ]
            volume = {
                'name': name,
                'emptyDir': {},
            }
            self.volumes.append(volume)

            #: Define a volume mount for both init and notebook containers.
            mount_path = f'/home/jovyan/{options["project"]}'

            # set the repository path to the working directory
            self.working_dir = mount_path
            self.notebook_dir = mount_path

            volume_mount = {
                'mountPath': mount_path,
                'name': name,
            }

            branch = options.get('branch', 'master')

            #: Define an init container.
            self.init_containers = [
                container for container in self.init_containers
                if not container.name.startswith(container_name)
            ]
            init_container = client.V1Container(
                name=container_name,
                image='alpine/git:latest',
                command=['sh', '-c'],
                args=[
                    'rm -rf {mount_path}/* && '
                    '(rm -rf {mount_path}/.* || true) && '
                    'apk update && apk add git-lfs && '
                    'git clone {repository} {mount_path} && '
                    '(git checkout {branch} || git checkout -b {branch}) && '
                    'git submodule init && git submodule update && '
                    'git reset --hard {commit_sha} &&'
                    'git lfs install --local &&'
                    'git config push.default simple &&'
                    'chown 1000:100 -Rc {mount_path}'.format(
                        branch=options.get('branch'),
                        commit_sha=options.get('commit_sha'),
                        mount_path=mount_path,
                        repository=repository,
                    )
                ],
                volume_mounts=[volume_mount],
                working_dir=mount_path,
                security_context=client.V1SecurityContext(run_as_user=0)
            )
            self.init_containers.append(init_container)

            #: Share volume mount with notebook.
            self.volume_mounts = [
                volume_mount for volume_mount in self.volume_mounts
                if volume_mount['mountPath'] != mount_path
            ]
            self.volume_mounts.append(volume_mount)

            pod = yield super().get_pod_manifest()

            # Because repository comes from a coroutine, we can't put it simply in `get_env()`
            pod.spec.containers[0].env.append(
                client.V1EnvVar('CI_REPOSITORY_URL', repository)
            )

            # add image pull secrets
            if options.get('image_pull_secrets'):
                secrets = [client.V1LocalObjectReference(name=name) for name in options.get('image_pull_secrets')]
                pod.spec.image_pull_secrets = secrets

            return pod

        def _expand_user_properties(self, template):
            """
            Override the _expand_user_properties from KubeSpawner.

            In addition to also escaping the server name, we trim the individual
            parts of the template to ensure < 63 charactr pod names.

            Code adapted from
            https://github.com/jupyterhub/kubespawner/blob/master/kubespawner/spawner.py
            """

            # Make sure username and servername match the restrictions for DNS labels
            safe_chars = set(string.ascii_lowercase + string.digits + '-')

            # Set servername based on whether named-server initialised
            if self.name:
                servername = '-{}'.format(self.name)
            else:
                servername = ''

            legacy_escaped_username = ''.join([
                s if s in safe_chars else '-' for s in self.user.name.lower()
            ])

            safe_username = escapism.escape(
                self.user.name, safe=safe_chars, escape_char='-'
            )
            rendered = template.format(
                userid=self.user.id,
                username=safe_username[:10],
                legacy_escape_username=legacy_escaped_username[:10],
                servername=servername
            ).lower()

            # just to be sure, still trim to 63 characters
            return rendered[:63]

except ImportError:
    pass
