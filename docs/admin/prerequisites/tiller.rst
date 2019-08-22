.. _tiller:

TODO Re format and re factor this (taken from renku-admin-docs)
===============================================================

Setup Helm
==========

## E. Setup helm and the ingress controller

Setup helm/tiller (see also:  https://docs.helm.sh/using_helm/#role-based-access-control).
```bash
$ kubectl create -f helm-installs/tiller-rbac-config.yaml
$ helm init --override 'spec.template.spec.containers[0].command'='{/tiller,--storage=secret,--listen=localhost:44134}' --service-account tiller --upgrade
```

Setup the nodes which will run the ingress controller:
```bash
$ kubectl edit node mynode-1 # Set a label like `ingress-node: "true"` AND verify afterwords it is in place.
# one node at least, more is possible. Not sure if it would help to scale the network though.
```

Install the `nginx-ingress`:
```bash
$ helm upgrade nginx-ingress --namespace kube-system --install stable/nginx-ingress -f helm-installs/nginx-values.yaml
$ helm upgrade nginx-ingress --namespace kube-system --install stable/nginx-ingress --set controller.hostNetwork=true 
```

Verify that the configuration is as expected:
```
$ helm status nginx-ingress|grep -i port
  export HTTP_NODE_PORT=32080
  export HTTPS_NODE_PORT=32443
  export NODE_IP=$(kubectl --namespace kube-system get nodes -o jsonpath="{.items[0].status.addresses[1].address}")
  echo "Visit http://$NODE_IP:$HTTP_NODE_PORT to access your application via HTTP."
  echo "Visit https://$NODE_IP:$HTTPS_NODE_PORT to access your application via HTTPS."
                servicePort: 80
```

Let's check we can contact the ingress:
```bash
$ curl -v http://<floating-ip>/
default backend - 404
```

