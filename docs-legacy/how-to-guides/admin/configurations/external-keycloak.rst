.. _external-keycloak:

External Keycloak: Renku configuration
=========================================

If you have your own Keycloak instance that you want to use for authentication
with Renku you need to:

- Create a Realm named Renku
- Setup the preferred parameters regarding user registration, token lifetime and
  activation of OTP.
- Import the four clients from their respective json files in the `helm chart
  keycloak configuration.
  <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/templates/_keycloak-clients-users.tpl>`_
  You will need to replace the templated renku domain values in the JSON. If you are deploying against an external
  GitLab, omit the GitLab client.

The secrets between the clients defined in Keycloak must match the client
secrets in the values file. The mapping between Keycloak clients and Renku
values is as follows:

=======================   =====================================
Keycloak client           Renku values file
=======================   =====================================
``renku``                 ``global.gateway.clientSecret``
``renku-cli``             ``global.gateway.cliClientSecret``
``renku-ui``              ``global.uiserver.clientSecret``
``renku-jupyterserver``   ``notebooks.oidc.clientSecret``
``gitlab``                ``global.gitlab.clientSecret``
=======================   =====================================

Note that the ``renku-jupyterserver`` client ID can be configured in the values
file via ``.notebooks.oidc.clientId``.
