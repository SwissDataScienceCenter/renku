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
