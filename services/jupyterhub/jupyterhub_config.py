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
"""Configure JupyterHub for GitLab intergration."""

import os
import sys

sys.path.insert(0, os.path.dirname(__file__))

from oauthenticator.gitlab import GitLabOAuthenticator

from spawners import RengaSpawner

#: Use GitLab OAuth Server for authentication.
c.JupyterHub.authenticator_class = GitLabOAuthenticator

#: Automatically begin the login process without showing the button.
c.Authenticator.auto_login = True

#: Enable named-servers
c.JupyterHub.allow_named_servers = True

#: Enable persisting encrypted auth_state using JUPYTERHUB_CRYPTO_KEY.
c.Authenticator.enable_auth_state = True

#: TODO Url for the database. e.g. `sqlite:///jupyterhub.sqlite`
#c.JupyterHub.db_url = 'sqlite:///jupyterhub.sqlite'

#: TODO Upgrade the database automatically on start.
#c.JupyterHub.upgrade_db = False

#: The ip or hostname for proxies and spawners to use for connecting to the Hub.
c.JupyterHub.hub_connect_ip = 'jupyterhub'

#: The ip address for the Hub process to *bind* to.
c.JupyterHub.hub_ip = '0.0.0.0'

#: Path to SSL certificate file for the public facing interface of the proxy.
#c.JupyterHub.ssl_cert = ''

#: Path to SSL key file for the public facing interface of the proxy.
#c.JupyterHub.ssl_key = ''
#
#:  When using SSL (i.e. always) this also requires a wildcard SSL certificate.
#c.JupyterHub.subdomain_host = ''

#: Configure the notebook spawner.
c.JupyterHub.spawner_class = RengaSpawner

NETWORK_NAME = 'review'

c.RengaSpawner.container_name_template = '{prefix}-{username}-{servername}'
c.RengaSpawner.use_internal_ip = True
c.RengaSpawner.network_name = NETWORK_NAME

#: Pass the network name as argument to spawned containers
c.RengaSpawner.extra_host_config = {'network_mode': NETWORK_NAME}

# Explicitly set notebook directory because we'll be mounting a host volume to
# it.  Most jupyter/docker-stacks *-notebook images run the Notebook server as
# user `jovyan`, and set the notebook directory to `/home/jovyan/work`.
# We follow the same convention.
notebook_dir = os.environ.get('DOCKER_NOTEBOOK_DIR') or '/home/jovyan'
c.RengaSpawner.notebook_dir = notebook_dir
# Mount the real user's Docker volume on the host to the notebook user's
# notebook directory in the container
# c.DockerSpawner.volumes = { 'jupyterhub-user-{username}': notebook_dir }
# volume_driver is no longer a keyword argument to create_container()
# c.DockerSpawner.extra_create_kwargs.update({ 'volume_driver': 'local' })

# Remove containers once they are stopped
c.RengaSpawner.remove_containers = True
# For debugging arguments passed to spawned containers
c.RengaSpawner.debug = True

#: Setup the service for creating named servers from GitLab projects.
env = os.environ.copy()
env['FLASK_APP'] = 'project_service.py'
env['FLASK_DEBUG'] = '1'
env['OAUTHLIB_INSECURE_TRANSPORT'] = '1'

c.JupyterHub.services = [{
    'name': 'projects',
    'command': ['flask', 'run', '-p', '9080'],
    'url': 'http://localhost:9080',
    'environment': env,
    'admin': True,
}]
