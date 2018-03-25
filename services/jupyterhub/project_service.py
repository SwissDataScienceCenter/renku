from functools import wraps
import json
import os
from urllib.parse import quote

import gitlab
import requests
from flask import Flask, abort, redirect, request, Response, make_response

from jupyterhub.services.auth import HubOAuth

prefix = os.environ.get('JUPYTERHUB_SERVICE_PREFIX', '/')

auth = HubOAuth(
    api_token=os.environ['JUPYTERHUB_API_TOKEN'],
    cookie_cache_max_age=60,
)

app = Flask(__name__)


def authenticated(f):
    """Decorator for authenticating with the Hub"""

    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.cookies.get(auth.cookie_name)
        if token:
            user = auth.user_for_token(token)
        else:
            user = None
        if user:
            return f(user, *args, **kwargs)
        else:
            # redirect to login url on failed auth
            state = auth.generate_state(next_url=request.path)
            response = make_response(
                redirect(auth.login_url + '&state=%s' % state))
            response.set_cookie(auth.state_cookie_name, state)
            return response

    return decorated


@app.route(prefix)
@authenticated
def whoami(user):
    return Response(
        json.dumps(user, indent=1, sort_keys=True),
        mimetype='application/json',
    )


@app.route(prefix + '<namespace>/<project>/<environment_slug>')
@authenticated
def launch_notebook(user, namespace, project, environment_slug):
    """Launch user server with name."""
    headers = {'Authorization': 'token %s' % auth.api_token}
    # 1. launch using spawner that checks the access
    r = requests.request(
        'POST',
        auth.api_url + '/users/{user[name]}/server'.format(user=user),
        json={
            'token': 'abcd1234',
            'namespace': namespace,
            'project': project,
            'environment_slug': environment_slug,
        },
        headers=headers)

    # 2. redirect to launched server
    if r.status_code not in {201, 400}:
        abort(r.status_code)
    return redirect(
        auth.hub_prefix + 'user/{user[name]}?token=abcd1234'.format(user=user))


@app.route(prefix + 'oauth_callback')
def oauth_callback():
    code = request.args.get('code', None)
    if code is None:
        return 403

    # validate state field
    arg_state = request.args.get('state', None)
    cookie_state = request.cookies.get(auth.state_cookie_name)
    if arg_state is None or arg_state != cookie_state:
        # state doesn't match
        return 403

    token = auth.token_for_code(code)
    app.logger.info(token)
    next_url = auth.get_next_url(cookie_state) or prefix
    response = make_response(redirect(next_url))
    response.set_cookie(auth.cookie_name, token)
    return response
