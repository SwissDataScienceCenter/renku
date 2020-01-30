import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts
from deepmerge import always_merger

default_chart_values = {
    "test": {
        "enabled": False
    },
    "createDemoUser": False,
    "initRealm": {
        "image": {
            "repository": "renku/init-realm",
            "tag": "latest"
        }
    },
    "keycloak": {
        "existingSecret": "keycloak-password-secret",
        "existingSecretKey": "keycloak-password",
        "service": {
            "type": "ClusterIP",
            "port": 80
        },
        "test": {
            "enabled": False
        },
        "livenessProbe": {
            "initialDelaySeconds": 180
        },
        "readinessProbe": {
            "initialDelaySeconds": 120
        },
        "extraInitContainers": """- name: theme-provider
          image: renku/keycloak-theme:v1.2
          imagePullPolicy: IfNotPresent
          command:
            - sh
          args:
            - -c
            - |
              echo \"Copying theme...\"
              cp -Rfv /renku_theme/* /theme
          volumeMounts:
            - name: theme
              mountPath: /theme""",
        "username": "admin",
        "persistence": {
            "deployPostgres": False,
            "dbVendor": "postgres",
            "dbName": "keycloak",
            "dbPort": 5432,
            "dbUser": "keycloak",
            "existingSecret": "renku-keycloak-postgres",
            "existingSecretKey": "keycloak-postgres-password"
        },
        "extraEnv": """
        - name: PROXY_ADDRESS_FORWARDING
          value: \"true\"""",
        "extraVolumes": """
        - name: theme
          emptyDir: {{}}""",
        "extraVolumeMounts": """
        - name: theme
          mountPath: /opt/jboss/keycloak/themes/renku-theme""",
        "ingress": {
            "enabled": False
        }
    },
    "resource": {
        "requests": {
            "cpu": "1000m",
            "memory": "2Gi"
        }
    }
}


def keycloak(config, global_config, dependencies=[]):
    keycloak_config = pulumi.Config('keycloak')
    values = keycloak_config.get_object('values') or {}

    values = always_merger.merge(default_chart_values, values)

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    global_config['keycloak'] = values

    values['global'] = global_values

    dependencies = [d for d in dependencies if d]
    return Chart(
        '{}-keycloak'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='keycloak',
            repo='stable',
            version='4.10.1',
            values=values
        ),
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )
