:orphan:

.. _json_web_tokens:

JSON Web Tokens
===============

In the platform, Json Web Tokens (JWTs) are used extensively for securing
service interfaces.

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

Introduction
------------

In the scope of the platform, JWTs are used as bearers of proof. In simple
terms, a valid token holds the proof of the claims it contains.

`Access Tokens <https://tools.ietf.org/html/rfc6749#section-1.4>`_ from the
identity manager (keycloak) are used to prove:

- the user id of its bearer (``sub`` claim)

- the platform global access attributes associated with that user, coming both
  from what the user requested (consented scope) and what the identity manager
  granted (stored roles, and other global attributes)

Any additional piece of information about identity must be recovered using
OIDC `identity tokens <http://openid.net/specs/openid-connect-core-
1_0.html#CodeIDToken>`_ and/or the `userinfo endpoint <http://openid.net/specs
/openid-connect-core-1_0.html#UserInfo>`_.

Access Tokens from the resource manager (RM) are used to prove:

- the user id of its bearer (``sub`` claim)
- the access permissions granted by the RM on a particular request
- the content of said request
