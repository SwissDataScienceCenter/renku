.. _certificates:

Certificates
============

You need a certificate for the ingress, you can deploy a certificate manager like LetsEncrypt (A.) or create manually a certificate and instert it as a k8s secret (B.).

A. Use LetsEncrypt
------------------

Copy and edit `manifests/cert-manager-issuer.yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/manifests/cert-manager-issuer.yaml>`_ to fill in the `email` field.


.. code-block:: console

   $ helm upgrade --install cert-manager stable/cert-manager -f helm-installs/cert-manager-values.yaml --namespace kube-system

   $ kubectl apply -f manifests/cert-manager-issuer.yaml


Note: recent cert-manager versions require a trick to get deployed, the workaround is to execute the following command before installing cert-manager:

.. code-block:: console

   $ helm install --version 0.4.1 --name cert-manager stable/cert-manager -f helm-installs/cert-manager-values.yaml --namespace kube-system


Check that we can issue certificates automatically.

[TODO add simple deployment to test certificates and steps to do it]


B. Generate manually
--------------------

[TODO generate certificate crt]

Add the previously created certificate as a secret to Renku namespace.

```bash
kubectl -n renku create secret tls renku-tls --cert=certificate.crt --key=certificate.key
```
