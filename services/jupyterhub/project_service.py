from functools import wraps
import json
import os
from urllib.parse import quote

import requests
from flask import Flask, abort, redirect, request, Response

from jupyterhub.services.auth import HubAuth

prefix = os.environ.get('JUPYTERHUB_SERVICE_PREFIX', '/')

auth = HubAuth(
    api_token=os.environ['JUPYTERHUB_API_TOKEN'],
    cookie_cache_max_age=60, )

app = Flask(__name__)


def authenticated(f):
    """Decorator for authenticating with the Hub"""

    @wraps(f)
    def decorated(*args, **kwargs):
        cookie = request.cookies.get(auth.cookie_name)
        token = request.headers.get(auth.auth_header_name)
        if cookie:
            user = auth.user_for_cookie(cookie)
        elif token:
            user = auth.user_for_token(token)
        else:
            user = None
        if user:
            return f(user, *args, **kwargs)
        else:
            # redirect to login url on failed auth
            return redirect(auth.login_url + '?next=%s' % quote(request.path))

    return decorated


@app.route(prefix)
@authenticated
def whoami(user):
    return Response(
        json.dumps(user, indent=1, sort_keys=True),
        mimetype='application/json', )


@app.route(prefix + '<namespace>/<project>/<environment_slug>')
@authenticated
def launch_notebook(user, namespace, project, environment_slug):
    # 1. check authorization against GitLab
    # TODO

    # 2. launch user server with name <namespace>_<project>_<environment_slug>
    headers = {'Authorization': 'token %s' % auth.api_token}
    r = requests.request(
        'POST',
        auth.api_url + '/users/{user[name]}/server'.format(user=user),
        json={'token': 'abcd1234'},
        headers=headers)

    # 3. redirect to launched server
    if r.status_code not in [201, 400]:
        abort(r.status_code)
    return redirect(auth.hub_prefix +
                    'user/{user[name]}?token=abcd1234'.format(user=user))
