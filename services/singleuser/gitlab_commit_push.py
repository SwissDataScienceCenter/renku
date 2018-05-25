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
"""Commit and push notebook to a GitLab server."""

import json
import os
import urllib
from subprocess import check_output
from urllib.parse import urlsplit, urlunsplit

import gitlab
from notebook.base.handlers import IPythonHandler
from notebook.utils import url_path_join


def _jupyter_server_extension_paths():
    return [{'module': 'gitlab_commit_push'}]


def load_jupyter_server_extension(nb_server_app):
    """Call when the extension is loaded.

    :param nb_server_app: Handle to the Notebook webserver instance.
    """
    web_app = nb_server_app.web_app
    host_pattern = '.*$'

    route_pattern = url_path_join(web_app.settings['base_url'], '/git/commit')
    web_app.add_handlers(host_pattern, [(route_pattern, GitCommitHandler)])


class GitCommitHandler(IPythonHandler):
    """Commit a file to GitLab using the REST API."""

    def put(self):
        """Handle a user PUT request."""
        #: Obtain filename and msg for commit
        data = self.get_json_body()
        msg = data['msg']
        filename = urllib.parse.unquote(data['filename'])

        #: Process environment variables.
        repository_url = urllib.parse.urlparse(os.getenv('CI_REPOSITORY_URL'))
        access_token = repository_url.password
        namespace = os.getenv('CI_NAMESPACE')
        project = os.getenv('CI_PROJECT')
        #: Use username as a prefix until we work with "forks".
        branch = self.get_current_user()['name'
                                         ] + '-' + os.getenv('CI_REF_NAME')
        ref = os.getenv('CI_COMMIT_SHA')
        url = os.environ.get('GITLAB_URL', 'http://gitlab.renku.build')

        #: Use GitLab REST API.
        gl = gitlab.Gitlab(url, api_version=4, oauth_token=access_token)

        try:
            gl_project = gl.projects.get('{0}/{1}'.format(namespace, project))
        except Exception as e:
            self.log.error(e)
            raise web.HTTPError(401, 'Not authorized to view project.')
            return

        try:
            gl_branch = gl_project.branches.get(branch)
        except Exception:
            gl_branch = gl_project.branches.create({
                'branch': branch,
                'ref': ref,
            })

        with open(self.contents_manager._get_os_path(filename), 'r') as f:
            content = f.read()

        try:
            gl_file = gl_project.files.get(file_path=filename, ref=branch)
            gl_file.content = content
            gl_file.save(branch=branch, commit_message=msg)
        except:
            gl_file = gl_project.files.create({
                'file_path': filename,
                'branch': branch,
                'content': content,
                'commit_message': data['msg'],
            })

        self.write({
            'status':
                200,
            'statusText':
                'Notebook was pushed to branch {}.'.format(gl_branch.name)
        })
