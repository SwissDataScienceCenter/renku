import pulumi
from deepmerge import always_merger

default_chart_values = {
    "replicaCount": 1,
    "hostName": None,
    "development": True,
    "rateLimits": {
        "general": {
            "extractorfunc": "request.header.cookie",
            "period": "10s",
            "average": 20,
            "burst": 100
        }
    },
    "service": {
        "port": 80,
        "type": "ClusterIP"
    },
    "image": "traefik:v2.0.0-alpha4",
    "auth_image": "renku/renku-gateway:0.6.0",
    "ingress": {
        "enabled": False
    },
    "graph": {
        "webhookService": None
    },
    "resources": {
        "requests": {
            "cpu": "100m",
            "memory": "512Mi"
        }
    },
    "nodeSelector": {},
    "tolerations": [],
    "affinity": {}
}


def gateway_values():
    """Get gateway values config."""
    config = pulumi.Config('gateway')
    gateway_values = config.get_object('values') or {}

    gateway_values = always_merger.merge(default_chart_values, gateway_values)

    return gateway_values
