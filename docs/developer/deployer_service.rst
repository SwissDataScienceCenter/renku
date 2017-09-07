.. _deployer:

Deployer
========

Overview
--------

The deployer service is responsible for launching work on compute resources
associated with the platform. The service is responsible for dispatching
work to various backends (engines) depending on load, availability, user
access, and workload requirements.

"Deployments" consist of two parts, an execution *context* and the *execution*
itself. The deployer service keeps a record of the contexts and executions in a
local database. However, the service optionally pushes the relevant vertices
and edges in the Renga :ref:`knowledge_graph` if the graph mutation service is
configured.


Contexts
^^^^^^^^

The execution context encapsulates all the user requirements for the execution,
e.g. the image to use, the software dependencies, hardware requirements etc.
Importantly, no deployment takes place upon the creation of the context - the
user (the client) has to subsequently request the creation of an execution
from a context to trigger a deployment.


Executions
^^^^^^^^^^

The deployer dispatches the work based on the execution context to a specific
backend that then carries out the actual execution. Currently supported
backends are ``k8s`` and ``docker``.


Authentication/Authorization
----------------------------

All deployer endpoints require a JWT bearer token to be supplied with the
``Authorization`` header. The tokens are verified using the public key of the
JWT issuer provided to the deployer at service initialization. If the :ref:`resource_manager` is configured, the deployer exchanges the bearer token for a
resource manager-signed token that is used to ascertain the user's access to
the requested resources. The token exchange happens on behalf of the
user/client without any required action.


Configuration
-------------

A number of configuration options are available for the deployer service. These
are specified as environment variables to allow for container orchestration.


*DEPLOYER_APP_NAME*: Application name. (default ``demo-client``)

*DEPLOYER_AUTHORIZATION_URL*: OpenID-Connect authorization endpoint. (default ``http://localhost:8080/auth/realms/Renga/protocol/openid-connect/auth``)

*DEPLOYER_BASE_PATH*: Base path for the API. (default ``/v1``)

*DEPLOYER_BASE_TEMPLATE*: Default base template for the demo page. (default ``renga_deployer/base.html``)

*DEPLOYER_CLIENT_ID*: Client identifier used for OIDC authentication. (default ``demo-client``)

*DEPLOYER_CLIENT_SECRET*: Client credentials used for OIDC authentication. (default ``None``)

*DEPLOYER_JWT_ISSUER*: JWT issuer used for token verification. (default ``http://localhost:8080/auth/realms/Renga``)

*DEPLOYER_JWT_KEY*: Public key used to verify JWT tokens. (default ``None``)

*DEPLOYER_SWAGGER_UI*:  Enable Swagger UI. (default ``False``)

*DEPLOYER_TOKEN_SCOPE_KEY*: Key inside JWT containing scopes. Use ``https://rm.datascience.ch/scope`` in combination with the resource manager.
(default ``None``)

*DEPLOYER_TOKEN_URL*: OpenID-Connect token endpoint. (default ``http://localhost:8080/auth/realms/Renga/protocol/openid-connect/token``)

*DEPLOYER_URL*: Base URL for the service. (default ``http://localhost:5000``)

*KNOWLEDGE_GRAPH_URL*:  If set, push contexts and executions to the KnowledgeGraph. (default ``None``)

*RENGA_AUTHORIZATION_CLIENT_ID*:  Client id for fetching the service access token. (default ``None``)

*RENGA_AUTHORIZATION_CLIENT_SECRET*:  Client secret for fetching the service access token. (default ``None``)

*RENGA_ENDPOINT*: URL for other platform services. (default ``http://localhost/api``)

*RESOURCE_MANAGER_URL*: If set, obtain and validate ResourceManager authorization tokens. (default ``None``)

*SQLALCHEMY_DATABASE_URI*: The URI of the database to be used for preserving internal state. (default ``sqlite:///deployer.db``)

*SQLALCHEMY_TRACK_MODIFICATIONS*:  Should Flask-SQLAlchemy track modifications of objects. (default ``False``)

*WSGI_NUM_PROXIES*:  The number of proxy servers in front of the app. Disable proxy fixer by setting value evaluating to ``False``. (default ``None``)
