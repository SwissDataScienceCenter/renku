"""Script to deploy Renku on minikube"""

import hashlib
import os
from datetime import datetime
from distutils.dir_util import copy_tree
from glob import glob
from io import StringIO
from subprocess import run, PIPE, CalledProcessError
from tempfile import TemporaryDirectory

from ruamel.yaml import YAML

dependencies = [
    {
        'repo_name': 'renku-ui',
        'chartpress_dir': 'helm-chart',
    },
    {
        'repo_name': 'renku-notebooks',
        'chartpress_dir': 'helm-chart',
    },
]


def main():
    renku_chartpress_dir = '{}/charts'.format(renku_repo_dir())
    # TODO: make these options
    namespace = 'renku'
    release = 'renku'

    yaml = YAML(typ='rt')
    yaml.indent(mapping=2, offset=2, sequence=4)

    # 1. Check minikube status
    if not bool(os.environ.get('SKIP_MINIKUBE_STATUS')):
        status_minikube()

    # 2. Build Docker images and update chart version with chartpress
    get_minikube_docker_env()
    for dep in dependencies:
        chartpress_dir = os.path.join(dependency_dir(dep['repo_name']), dep['chartpress_dir'])
        update_charts(chartpress_dir)
    update_charts(renku_chartpress_dir)

    # 3. Init helm
    kubectl_use_minikube_context()
    helm_init()

    # 4. Package renku chart, with local versions of dependencies
    with TemporaryDirectory() as tmp:
        copy_tree(os.path.join(renku_chartpress_dir, 'renku'), os.path.join(tmp, 'renku'))

        with open(os.path.join(tmp, 'renku', 'requirements.yaml'), 'rt') as f:
            renku_requirements = yaml.load(f)

        for dep in dependencies:
            chartpress_dir = os.path.join(dependency_dir(dep['repo_name']), dep['chartpress_dir'])
            chart_name = dep.get('chart_name', dep['repo_name'])

            with open(os.path.join(chartpress_dir, chart_name, 'Chart.yaml'), 'rt') as f:
                chart = yaml.load(f)
            version = chart.get('version')

            req = next(filter(lambda x: x.get('name') == chart_name, renku_requirements.get('dependencies')))
            req['version'] = version
            req['repository'] = 'file://{}'.format(os.path.abspath(os.path.join(chartpress_dir, chart_name)))

        with open(os.path.join(tmp, 'renku', 'requirements.yaml'), 'wt') as f:
            yaml.dump(renku_requirements, f)

        run(['cat', 'renku/requirements.yaml'], cwd=tmp).check_returncode()

        package_chart('renku', tmp, tmp)
        renku_chart = os.path.abspath(glob(os.path.join(tmp, 'renku-*.tgz'))[0])

        helm_deploy_cmd = [
            'helm', 'upgrade', release,
            renku_chart,
            '--install',
            '--namespace', namespace,
            '-f', os.path.join(renku_chartpress_dir, 'minikube-values.yaml'),
            '--set', 'global.renku.domain={mip}'.format(mip=minikube_ip()),
            '--set', 'ui.gitlabUrl=http://{mip}/gitlab'.format(mip=minikube_ip()),
            '--set', 'ui.jupyterhubUrl=http://{mip}/jupyterhub'.format(mip=minikube_ip()),
            '--set', 'jupyterhub.hub.extraEnv.GITLAB_URL=http://{mip}/gitlab'.format(mip=minikube_ip()),
            '--set', 'gitlab.registry.externalUrl=http://{mip}:30105/'.format(mip=minikube_ip()),
            '--timeout', '1800',
        ]

        print('Running: {}'.format(' '.join(helm_deploy_cmd)))
        run(helm_deploy_cmd).check_returncode()


def renku_base_dir():
    """Returns the path to the renku base directory"""
    renku_base_dir = os.environ.get('RENKU_BASE_DIR', '..')
    if not os.path.isabs(renku_base_dir):
        renku_base_dir = os.path.join(os.path.dirname(__file__), '..', renku_base_dir)
    return os.path.normpath(renku_base_dir)


def renku_repo_dir():
    """Returns the path to this (Renku) repository"""
    return os.path.join(renku_base_dir(), 'renku')


def dependency_dir(repo_name):
    """Returns the path to the given dependency"""
    return os.path.join(renku_base_dir(), repo_name)


def package_chart(chart, chartpress_dir, dest_dir):
    """Packages a chart in tarball archive"""
    cmd = ['helm', 'package', '-u', chart, '-d', dest_dir]
    run(cmd, cwd=chartpress_dir).check_returncode()


def update_charts(chartpress_dir):
    """Runs chartpress to build Docker images and update Chart.yaml"""
    chartpress_tag = get_chartpress_tag(chartpress_dir)

    cmd = ['chartpress']
    if chartpress_tag:
        cmd = cmd + ['--tag', chartpress_tag]
    run(cmd, cwd=chartpress_dir).check_returncode()


def get_minikube_docker_env():
    """Source the Docker env in the minikube host"""
    result = run(['minikube', 'docker-env'], stdout=PIPE)
    result.check_returncode()
    env = result.stdout.decode('utf-8')
    for line in StringIO(env):
        line = line.strip()
        if line.startswith('export'):
            key, value = line[len('export')+1:].split('=')
            os.environ[key] = value.strip('"')


def get_chartpress_tag(chartpress_dir):
    """Returns a unique tag if the git repository in chartpress_dir is not clean."""
    result = run(['git','status', '--porcelain'], stdout=PIPE, cwd=chartpress_dir)
    result.check_returncode()
    if result.stdout.decode('utf-8'):
        m = hashlib.sha256()
        m.update(datetime.now().isoformat().encode('utf-8'))
        return '0.0-unclean-{}'.format(m.hexdigest()[:7])
    else:
        return None


def status_minikube():
    """Checks that minikube is running."""
    result = run(['minikube', 'status'], stdout=PIPE)
    try:
        result.check_returncode()
    except CalledProcessError:
        raise RuntimeError('Minikube is not running:\n{}'.format(result.stdout.decode('utf-8')))


def minikube_ip():
    """Returns minikube ip"""
    result = run(['minikube', 'ip'], stdout=PIPE)
    result.check_returncode()
    return result.stdout.decode('utf-8').strip()


def helm_init():
    """Run helm init to make sure we can deploy the chart"""
    result = run(['helm', 'init'], stdout=PIPE)
    try:
        result.check_returncode()
    except CalledProcessError:
        raise RuntimeError('Could not run helm init:\n{}'.format(result.stdout.decode('utf-8')))


def kubectl_use_minikube_context():
    """Make sure we use the minikube context to deploy"""
    result = run(['kubectl', 'config', 'use-context', 'minikube'], stdout=PIPE)
    try:
        result.check_returncode()
    except CalledProcessError:
        raise RuntimeError('Could not run kubectl config use-context minikube:\n{}'.format(result.stdout.decode('utf-8')))


if __name__ == '__main__':
    main()
