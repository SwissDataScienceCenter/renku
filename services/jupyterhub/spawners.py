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

import os

from tornado import web


class SpawnerMixin():
    """Extend spawner methods."""

    def get_env(self):
        """Extend environment variables passed to the notebook server."""
        environment = super().get_env()
        environment.update({
            'CI_COMMIT_REF_NAME':
            self.user_options.get('branch', 'master'),
            'CI_REPOSITORY_URL':
            self.user_options.get('repo_url', ''),
            'CI_PROJECT_PATH':
            self.user_options.get('project_path', ''),
            'CI_ENVIRONMENT_SLUG':
            self.user_options.get('env_slug', ''),
            # TODO 'ACCESS_TOKEN': access_token,
        })
        return environment

    async def start(self):
        """Start the notebook server."""
        self.log.info(
            "starting with args: {}".format(' '.join(self.get_args())))
        self.log.info("user options: {}".format(self.user_options))

        auth_state = await self.user.get_auth_state()
        assert 'access_token' in auth_state
        self.log.info(auth_state)

        # 1. check authorization against GitLab
        options = self.user_options
        namespace = options.get('namespace')
        project = options.get('project')
        env_slug = options.get('environment_slug')

        url = os.environ.get('GITLAB_HOST', 'http://gitlab.renga.local')

        import gitlab
        gl = gitlab.Gitlab(
            url, api_version=4, oauth_token=auth_state['access_token'])

        try:
            gl_project = gl.projects.get('{0}/{1}'.format(namespace, project))
            gl_user = gl.users.list(username=self.user.name)[0]
            access_level = gl_project.members.get(gl_user.id).access_level
        except Exception as e:
            self.log.error(e)
            raise web.HTTPError(401, 'Not authorized to view project.')
            return

        if access_level < gitlab.DEVELOPER_ACCESS:
            raise web.HTTPError(401, 'Not authorized to view project.')
            return

        if not any(gl_env.slug for gl_env in gl_project.environments.list()
                   if gl_env.slug == env_slug):
            raise web.HTTPError(404, 'Environment does not exist.')
            return

        return await super().start()

try:
    from dockerspawner import DockerSpawner

    class RengaDockerSpawner(SpawnerMixin, DockerSpawner):
        """A class for spawning notebooks on Renga-JupyterHub using Docker."""

except ImportError:
    pass


try:
    from dockerspawner import DockerSpawner

    class RengaDockerSpawner(SpawnerMixin, DockerSpawner):
        """A class for spawning notebooks on Renga-JupyterHub using Docker."""

except ImportError:
    pass


try:
    from kubespawner import KubeSpawner

    class RengaKubeSpawner(SpawnerMixin, KubeSpawner):
        """A class for spawning notebooks on Renga-JupyterHub using K8S."""

except ImportError:
    pass
