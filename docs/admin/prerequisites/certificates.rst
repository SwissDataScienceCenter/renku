.. _certificates:

Certificates
============

To use TLS you need valid certificates for the ingress by either deploying a certificate manager like LetsEncrypt or creating certificates manually and inserting them as k8s secrets.

LetsEncrypt
------------------

Copy and edit `manifests/cert-manager-issuer.yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/manifests/cert-manager-issuer.yaml>`_ to fill in the `email` field.

.. code-block:: console

   $ helm install cert-manager jetstack/cert-manager -f helm-installs/cert-manager-values.yaml --namespace kube-system
   $ kubectl apply -f manifests/cert-manager-issuer.yaml

Now you can check that certificates can be issued automatically using a test deployment.
For more information, please check `LetsEncrypt cert-manager Helm documentation <https://hub.helm.sh/charts/jetstack/cert-manager>`_

Generate manually
--------------------

You can also use a self-signed certificate created manually.
Add the previously created certificate as a secret to the ``renku`` namespace.

.. code-block:: console

  $ kubectl -n renku create secret tls renku-tls --cert=certificate.crt --key=certificate.key
