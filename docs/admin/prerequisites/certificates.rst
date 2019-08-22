.. _certificates:

Certificates
============

A. Use LetsEncrypt
------------------

TODO Re factor this (taken from renku-admin-docs)
=================================================

## G. Deploy cert-manager, to support https

N.B. This will rely on `Let's Encrypt` being able to refresh SSL certificates, automatically.

Open and edit `manifests/cert-manager-issuer.yaml` to fill in the `email` field.

To prevent a potential failure, ensure that the contents of the file `cert-manager-issuer.yaml` have as follows:
```
---
apiVersion: certmanager.k8s.io/v1alpha1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    # Fill in the admin email for the domain names
    email: your.email@yourdomain.example.com
    http01: {}
    privateKeySecretRef:
      name: letsencrypt-prod
```

It appears that recent cert-manager versions require a trick to get deployed, due to upstream changes, so here's a workaround:
```bash
$ helm install --version 0.4.1 --name cert-manager stable/cert-manager -f helm-installs/cert-manager-values.yaml --namespace kube-system
$ helm upgrade --install cert-manager stable/cert-manager -f helm-installs/cert-manager-values.yaml --namespace kube-system
$ kubectl apply -f manifests/cert-manager-issuer.yaml
``` 

Check that we can issue certificates automatically, by installing grafana:
```bash
# Replace `grafana.internal.renku.ch` with appropriate value
$ helm upgrade grafana-test --namespace test --install stable/grafana \
--set 'ingress.enabled=true' \
--set 'ingress.hosts[0]=grafana.internal.renku.ch' \
--set 'ingress.tls[0].hosts[0]=grafana.internal.renku.ch' \
--set 'ingress.tls[0].secretName=grafana-test-tls' \
--set 'ingress.annotations.kubernetes\.io/ingress\.class=nginx' \
--set 'ingress.annotations.kubernetes\.io/tls-acme="true"'
```


After a few minutes, you should be able to open  `https://grafana.<domain>` (`https://grafana.internal.renku.ch` in my case).
You can check that the certificate is valid and issued by Let's Encrypt.

We can now remove that deployment:
```bash
$ helm del --purge grafana-test
$ kubectl delete ns test
```


B. Generate manually
--------------------

[TODO generate certificate crt]

Add the previously created certificate as a secret to Renku namespace.

```bash
kubectl -n renku create secret tls renku-tls --cert=certificate.crt --key=certificate.key
```
