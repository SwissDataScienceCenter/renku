:orphan:

.. _json_web_tokens:

JSON Web Tokens
===============

Keycloak issues identity and access tokens in the form of Json Web Tokens (JWTs).

Relevant documentation
----------------------

- `RFC7519 <https://tools.ietf.org/html/rfc7519>`_ JWT definition

- `jwt.io <https://jwt.io>`_, auth0's site about JWTs. Useful for its online
  JWT parser  and listing of JWT libraries.

- `OpenID Connect Core <http://openid.net/specs/openid-connect-core-
  1_0.html>`_ Specification for the identity management

- `RFC6749 <https://tools.ietf.org/html/rfc6749>`_ OAuth2

- `RFC6750 <https://tools.ietf.org/html/rfc6750>`_ Bearer token specifics

- `Keycloak documentation <https://keycloak.gitbooks.io>`_ Keycloak
  documentation.

- `OIDC in Keycloak
  <https://keycloak.gitbooks.io/documentation/content/server_admin/topics/sso-
  protocols/oidc.html>`_ for a description of Keycloak's implementation of
  OpenID Connect.

- `Auth0 articles <https://auth0.com/docs/apis>`_ describing their own
  services. Auth0 is a certified OIDC provider and their documentation is more
  accessible than the OIDC specification.


Usage in Renku
--------------

Keycloak serves as an identity provider to Renku by acting as OpenID connect
provider to the Renku GitLab instance. Personal `access
<https://tools.ietf.org/html/rfc6749#section-1.4>`_- and `identity tokens
<http://openid.net/specs/openid-connect-core-1_0.html#CodeIDToken>`_ from
Keycloak for a user are collected by the :ref:`api_gateway` service during the
log-in process. While there is currently no Renku backend service which uses the
Keycloak access token for authorizing access to its resources, this is likely
to change in the future.

Note that the access tokens issued by JupyterHub and GitLab are **not** JWTs.
