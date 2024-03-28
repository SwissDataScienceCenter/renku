.. _certificates:

Certificates
============

To enable HTTPS on the Renku website you need valid certificates for the ingress.

Certificates can be obtained in different ways:

#. Use a certificate issuer like LetsEncrypt
#. Use a certificate bought from a certificate authority like SwissSign
#. Use self-signed certificates

Options one and three can be automated through the use of `cert-manager <https://cert-manager.io>`_

This document assumes that the default ``renku-tls`` secret name is used to configure the ingress.


cert-manager
------------

If using Helm, you can deploy cert-manager using the following:

.. code-block:: console

  $ helm repo add jetstack https://charts.jetstack.io
  $ helm repo update
  $ helm install \
  cert-manager jetstack/cert-manager \
               --namespace cert-manager \
               --create-namespace \
               --version v1.12.0 \
               --set installCRDs=true

More details on the deployment can be found in the `dedicated section <https://cert-manager.io/docs/installation/helm/>`_ of the project installation documentation.


Generate manually
-----------------

For development purposes, self-signed certificates can be used to properly test that all
components are communicating securely together.

Follow the `cert-manager guide <https://cert-manager.io/docs/configuration/selfsigned/>`_ to create a self-signed certificate issuer.

You can then use it for your deployment by setting annotations such as the following in your
configuration values:

.. code-block:: yaml

  global:
    certificates:
      customCAs:
        - secret: renku-tls # must match the secretName field of the CA Certificate object.

  ingress:
    enabled: true
    annotations:
      cert-manager.io/cluster-issuer: null
      cert-manager.io/issuer: my-ca-issuer
      cert-manager.io/common-name: my-selfsigned-ca

For more details about the annotations, please check the ``values.yaml`` files.


LetsEncrypt
-----------

You can find the configuration to use Let's Encrypt with the NGINX ingress in the `dedicated chapter <https://cert-manager.io/docs/tutorials/acme/nginx-ingress/#step-6---configure-a-lets-encrypt-issuer>`_ of the cert-manager documentation.

If you create a cluster issuer named ``letsencrypt-production``, you can then use the default
values and just enable the ingress.


Certificate from authority
--------------------------

You can also use an SSL certificate issued by another certificate authority.
Add the mentioned certificate as a secret to the ``renku`` namespace.

.. code-block:: console

  $ kubectl -n renku create secret tls renku-tls --cert=certificate.crt --key=certificate.key

If GitLab is deployed as part of Renku you also need a certificate for the registry, this can be included in the ``.crt`` file.

In this case, you can disable the use of cert-manager with the following:

.. code-block:: yaml

  ingress:
    enabled: true
    annotations:
      cert-manager.io/cluster-issuer: null
