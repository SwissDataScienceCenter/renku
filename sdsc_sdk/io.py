"""Read/Write data to platform storage"""
import json
import logging
import os
import shutil
import tempfile
from functools import wraps

import requests

from .graph_elements import Vertex

TOKEN = APP_ID = None
_is_setup = False

logger = logging.getLogger(__name__)

__all__ = ["get_resource", "find_resource", "get_or_create_resource", "read", "write"]


def ensure_setup(f):
    """Function decorator to ensure TOKEN and APP_ID are defined"""
    @wraps(f)
    def wrapper(*args, **kwargs):
        setup()
        return f(*args, **kwargs)
    return wrapper


def setup():
    """Loads TOKEN and APP_ID from environment variables"""
    global _is_setup
    if not _is_setup:
        global TOKEN, APP_ID
        TOKEN = os.environ.get('SDSC_TOKEN', None)
        APP_ID = os.environ.get('SDSC_GRAPH_ID', None)
        logger.info('Retrieved token: {}'.format(TOKEN))
        logger.info('Retrieved app_id: {}'.format(APP_ID))
        if TOKEN is None:
            logger.warning('No token')
        _is_setup = True


@ensure_setup
def get_resource(resource_id):
    req = requests.get('https://internal.datascience.ch/api/navigation/vertex/{}'.format(resource_id))
    obj = json.loads(req.content)
    vertex = Vertex(**obj)
    if 'resource:file' in vertex.types:
        return vertex
    else:
        return None


@ensure_setup
def find_resource(filename):
    def parse(s):
        try:
            return json.loads(s)
        except json.decoder.JSONDecodeError:
            return None

    req = requests.get('https://internal.datascience.ch/api/navigation/vertex')
    objs = [parse(s) for s in req.content.decode('utf-8').split('\r')]
    vertices = [Vertex(**obj) for obj in objs if obj]
    data_vertices = filter(lambda v: 'resource:file' in v.types, vertices)
    candidates = filter(lambda v: filename in map(lambda p: p['value'], v.properties['resource:file_name'].values), data_vertices)
    candidates = list(candidates)

    logger.info('Found: {}'.format(candidates))

    if candidates:
        candidate = candidates[0]
        return candidate
    else:
        return None


@ensure_setup
def get_or_create_resource(filename):
    """Ensures `filename` exists as a file"""

    vertex = find_resource(filename)

    if vertex:
        return vertex
    else:
        if TOKEN is None:
            raise RuntimeError('No token')
        body = {
            'target': {
                'type': 'filename',
                'filename': filename
            }
        }
        if APP_ID:
            body['app_id'] = APP_ID
        req = requests.post(
            'https://testing.datascience.ch:9003/authorize/storage/write',
            json=body,
            headers=dict(Authorization=TOKEN)
        )
        resource_id = json.loads(req.content)['id']
        vertex = get_resource(resource_id)
        if vertex is None:
            raise RuntimeError('Cannot find back vertex {}'.format(resource_id))
        return vertex


@ensure_setup
def read(filename):
    """Reads `filename`"""

    vertex = find_resource(filename)

    if vertex is None:
        raise FileNotFoundError('{} does not exists'.format(filename))

    resource_id = vertex.id

    if TOKEN is None:
        raise RuntimeError('No token')

    body = {
        'resource_id': resource_id
    }
    if APP_ID:
        body['app_id'] = APP_ID
    req = requests.post(
        'https://testing.datascience.ch:9003/authorize/storage/read',
        json=body,
        headers=dict(Authorization=TOKEN)
    )
    response = json.loads(req.content)
    logger.info('Got response: {}'.format(response))

    req = requests.get(
        'https://testing.datascience.ch:9000/read/{}'.format(filename),
        headers=dict(Authorization=TOKEN),
        stream=True
    )

    if req.status_code == 200:
        req.raw.decode_content = True
        f = tempfile.TemporaryFile()
        shutil.copyfileobj(req.raw, f)
        f.seek(0)
        return f
    else:
        raise RuntimeError(req.status_code, req.content)


@ensure_setup
def write(filename, file):
    """Writes file-like object `file` to `filename`"""

    vertex = get_or_create_resource(filename)

    resource_id = vertex.id

    if TOKEN is None:
        raise RuntimeError('No token')

    # RM fails if no app_id
    if APP_ID:
        body = {
            'target': {
                'type': 'resource',
                'resource_id': id
            }
        }
        if APP_ID:
            body['app_id'] = APP_ID
        req = requests.post(
            'https://testing.datascience.ch:9003/authorize/storage/write',
            json=body,
            headers=dict(Authorization=TOKEN)
        )
        response = json.loads(req.content)
        logger.info('Got response: {}'.format(response))

    file.seek(0)
    req = requests.post(
        'https://testing.datascience.ch:9000/write/{}'.format(filename),
        headers=dict(Authorization=TOKEN),
        data=file
    )

    if 200 <= req.status_code < 300:
        return True
    else:
        raise RuntimeError(req.status_code, req.content)
