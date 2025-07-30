.. _external-gitlab:

External GitLab
===============

To use an external GitLab as a backend for a Renku deployment you must register
the Renku application in GitLab. If you are not an admin user, this is done under
your personal user preferences. It is possible that the GitLab instance you are
using prevents users from registering external applications - in this case you
must contact your GitLab server administrator for assistance.


Renku GitLab application details
--------------------------------

Name: renku

Callback URLs:

.. code-block:: console

  https://<your-renku-dns>/api/auth/callback
  https://<your-renku-dns>/api/auth/gitlab/token

Scopes:

.. code-block:: console

    api (Access the authenticated user's API)
    read_user (Read the authenticated user's personal information)
    read_repository (Allows read-access to the repository)
    read_registry (Grants permission to read container registry images)
    openid (Authenticate using OpenID Connect)

Copy the secret and client ID and paste it to the dedicated places in renku-values.yaml (step 3)
