# -*- coding: utf-8 -*-
#
# Copyright 2017 - Swiss Data Science Center (SDSC)
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
"""Combine and serve Swagger specifications."""

import os
import json

import requests
from werkzeug.wrappers import Request, Response

SERVICES = [
    service.strip()
    for service in os.environ.get(
        'SERVICES', '').split(',')
]

WSGI_NUM_PROXIES = int(os.environ.get('WSGI_NUM_PROXIES', 0))


def merge(*specs):
    """Merge paths and definitions from given specifications."""
    composed = {
        'definitions': {},
        'paths': {},
        'schemes': [],
        'securityDefinitions': {},
        'tags': [],
    }

    for definition in specs:
        # combine only v2 schemas
        composed.setdefault('swagger', definition['swagger'])
        assert composed['swagger'] == definition['swagger']

        # extend tags
        composed['tags'].extend(definition.get('tags', []))

        # schemes
        composed['schemes'] = list(
            set(composed['schemes'] + definition.get('schemes', [])))

        # combines paths
        for key, path in definition['paths'].items():
            # FIXME temporary workaround for trailing slashes
            if key.endswith('/'):
                key = key.rstrip('/')
            composed['paths'][definition['basePath'] + key] = path

        # combines definitions
        for key, defs in definition.get('definitions', {}).items():
            assert key not in composed['definitions']
            composed['definitions'][key] = defs

        for key, defs in definition.get('securityDefinitions', {}).items():
            if key in composed['securityDefinitions']:
                security_defs = composed['securityDefinitions'][key]
                same_keys = ('authorizationUrl', 'type', 'flow')
                for check_key in same_keys:
                    assert security_defs[check_key] == defs[
                        check_key], (check_key, definition)
                composed['securityDefinitions'][key]['scopes'].update(
                    **defs['scopes'])
            else:
                composed['securityDefinitions'][key] = defs

    return composed


def swagger(endpoint, *services):
    """Return merged specification."""
    specs = (requests.get(service).json() for service in services)
    spec = merge(*specs)
    spec['host'] = endpoint
    # TODO add title and other spec details
    return spec


def application(environ, start_response):
    """Simple swagger serving app."""
    request = Request(environ)
    api_root_url = os.environ.get('API_ROOT_URL', request.host_url).rstrip('/')
    services = [
        '/'.join((api_root_url, service, 'swagger.json'))
        for service in SERVICES
    ]
    text = json.dumps(swagger(request.host, *services))
    response = Response(text, mimetype='application/json')
    return response(environ, start_response)


if WSGI_NUM_PROXIES:
    from werkzeug.contrib.fixers import ProxyFix
    application = ProxyFix(application, num_proxies=WSGI_NUM_PROXIES)

if __name__ == '__main__':
    api_root_url = os.environ.get('API_ROOT_URL', 'http://localhost/api').rstrip('/')
    services = [
        '/'.join((api_root_url, service, 'swagger.json'))
        for service in SERVICES
    ]
    print(json.dumps(swagger(api_root_url, *services)))
