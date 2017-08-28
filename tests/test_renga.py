# -*- coding: utf-8 -*-
#
# Copyright 2017 Swiss Data Science Center
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
"""Renga integration tests."""


import docker
import pytest
import requests


@pytest.fixture
def token():
    """Get keycloak access token."""
    response = requests.post(
        'http://localhost/auth/realms/Renga/'
        'protocol/openid-connect/token',
        data={
            'grant_type': 'password',
            'client_id': 'demo-client',
            'username': 'demo',
            'password': 'demo',
        })
    return response.json().get('access_token')


def test_keycloak_token(token):
    """Test keycloak setup."""
    assert token


def test_resource_manager(token):
    """Test obtaining an authorization from the resource manager."""
    r = requests.post(
        'http://localhost/api/resource-manager/authorize',
        headers={'Authorization': 'Bearer {0}'.format(token)},
        json={
            "scope": ["storage:bucket_create"],
            "service_claims": {
                "custom": 42
            }
        })

    assert r.status_code == 200
    assert r.json().get('access_token')


def test_deployer_context_create(token):
    """Test creation of a deployment context."""
    r = requests.post(
        'http://localhost/api/deployer/contexts',
        headers={'Authorization': 'Bearer {0}'.format(token)},
        json={"image": "hello-world"})

    assert r.status_code == 201
    assert r.json().get('identifier')
