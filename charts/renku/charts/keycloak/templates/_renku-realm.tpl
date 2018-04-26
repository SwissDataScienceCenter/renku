{{/* vim: set filetype=mustache: */}}
{{/*
Define the RENKU realm
TODO: Put back template strings from renku/services/keycloak/renku-realm.json.tpl
*/}}
{{- define "keycloak.renku.realm" -}}
{
  "id" : "Renku",
  "realm" : "Renku",
  "notBefore" : 0,
  "revokeRefreshToken" : false,
  "accessTokenLifespan" : 3000,
  "accessTokenLifespanForImplicitFlow" : 900,
  "ssoSessionIdleTimeout" : 1800,
  "ssoSessionMaxLifespan" : 36000,
  "offlineSessionIdleTimeout" : 2592000,
  "accessCodeLifespan" : 60,
  "accessCodeLifespanUserAction" : 300,
  "accessCodeLifespanLogin" : 1800,
  "actionTokenGeneratedByAdminLifespan" : 43200,
  "actionTokenGeneratedByUserLifespan" : 300,
  "enabled" : true,
  "sslRequired" : "external",
  "registrationAllowed" : true,
  "registrationEmailAsUsername" : true,
  "rememberMe" : false,
  "verifyEmail" : false,
  "loginWithEmailAllowed" : true,
  "duplicateEmailsAllowed" : false,
  "resetPasswordAllowed" : false,
  "editUsernameAllowed" : false,
  "bruteForceProtected" : false,
  "permanentLockout" : false,
  "maxFailureWaitSeconds" : 900,
  "minimumQuickLoginWaitSeconds" : 60,
  "waitIncrementSeconds" : 60,
  "quickLoginCheckMilliSeconds" : 1000,
  "maxDeltaTimeSeconds" : 43200,
  "failureFactor" : 30,
  "roles" : {
    "realm" : [ {
      "id" : "8e2f5b00-890f-4a94-a2ea-ca86719ca434",
      "name" : "offline_access",
      "description" : "${role_offline-access}",
      "scopeParamRequired" : true,
      "composite" : false,
      "clientRole" : false,
      "containerId" : "Renku"
    }, {
      "id" : "25387a4d-53b8-4168-9601-48e9b83a71c7",
      "name" : "uma_authorization",
      "description" : "${role_uma_authorization}",
      "scopeParamRequired" : false,
      "composite" : false,
      "clientRole" : false,
      "containerId" : "Renku"
    } ],
    "client" : {
      "realm-management" : [ {
        "id" : "49f9fb83-2994-49f0-b85b-4030a88e824b",
        "name" : "query-realms",
        "description" : "${role_query-realms}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "ec4fff8f-4e18-4ec8-841d-b4e2bf3f8963",
        "name" : "view-users",
        "description" : "${role_view-users}",
        "scopeParamRequired" : false,
        "composite" : true,
        "composites" : {
          "client" : {
            "realm-management" : [ "query-users", "query-groups" ]
          }
        },
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "968b7135-a8a0-4f91-945c-f60db6cf25f1",
        "name" : "view-identity-providers",
        "description" : "${role_view-identity-providers}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "ff24405d-76a4-4c43-a1ff-580a67f2b7d8",
        "name" : "query-users",
        "description" : "${role_query-users}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "fe60c759-1a68-4522-93d0-0ef3261b2281",
        "name" : "realm-admin",
        "description" : "${role_realm-admin}",
        "scopeParamRequired" : false,
        "composite" : true,
        "composites" : {
          "client" : {
            "realm-management" : [ "query-realms", "view-users", "view-identity-providers", "query-users", "query-groups", "manage-users", "view-realm", "create-client", "view-events", "view-authorization", "manage-authorization", "manage-realm", "impersonation", "manage-events", "view-clients", "manage-clients", "query-clients", "manage-identity-providers" ]
          }
        },
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "20f82a41-1848-4684-a178-b19d773ee309",
        "name" : "query-groups",
        "description" : "${role_query-groups}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "235087c2-1056-4ee7-a2df-d5ee82774860",
        "name" : "manage-users",
        "description" : "${role_manage-users}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "89e75ad3-02f3-458e-9c52-c7960d20fad1",
        "name" : "view-realm",
        "description" : "${role_view-realm}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "fbfe4196-f7a2-4b17-ba20-0333a6f3448f",
        "name" : "create-client",
        "description" : "${role_create-client}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "2ece4c22-2314-4e04-99d8-236edb4b4e3e",
        "name" : "view-events",
        "description" : "${role_view-events}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "74ea2bfa-0989-4ceb-8c75-40a4d948b49d",
        "name" : "view-authorization",
        "description" : "${role_view-authorization}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "b6a44f08-e5a7-41e6-b27e-8cfa0a3ea1e9",
        "name" : "manage-authorization",
        "description" : "${role_manage-authorization}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "428afb00-16a0-47b8-8632-806f6a93d1cc",
        "name" : "manage-realm",
        "description" : "${role_manage-realm}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "c00cde9d-3119-4df1-95ce-7cbd7769d0eb",
        "name" : "impersonation",
        "description" : "${role_impersonation}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "fd456a2b-088e-4141-a793-78126f2e4c30",
        "name" : "manage-events",
        "description" : "${role_manage-events}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "287039e6-74a9-4245-9e51-6f3402b9163f",
        "name" : "view-clients",
        "description" : "${role_view-clients}",
        "scopeParamRequired" : false,
        "composite" : true,
        "composites" : {
          "client" : {
            "realm-management" : [ "query-clients" ]
          }
        },
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "bad91941-cbfd-44a3-92f9-2256778dfbad",
        "name" : "manage-clients",
        "description" : "${role_manage-clients}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "e70b63e2-ef96-4073-b239-980d3b7f288b",
        "name" : "query-clients",
        "description" : "${role_query-clients}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      }, {
        "id" : "d881d526-bba3-4e5b-8a8c-107c9833ded8",
        "name" : "manage-identity-providers",
        "description" : "${role_manage-identity-providers}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "dcd909d4-22f3-4e70-b948-11944a8e0824"
      } ],
      "demo-client" : [ ],
      "security-admin-console" : [ ],
      "renku-services" : [ ],
      "admin-cli" : [ ],
      "broker" : [ {
        "id" : "d463f205-b2ea-4a83-b9f5-f72a811774c3",
        "name" : "read-token",
        "description" : "${role_read-token}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "c67e0adb-52bc-482b-8c00-ee25247041e6"
      } ],
      "account" : [ {
        "id" : "c773d5c7-6be8-4e3d-ba92-9c87a0c1f431",
        "name" : "manage-account",
        "description" : "${role_manage-account}",
        "scopeParamRequired" : false,
        "composite" : true,
        "composites" : {
          "client" : {
            "account" : [ "manage-account-links" ]
          }
        },
        "clientRole" : true,
        "containerId" : "84027d06-f768-40f0-a6c7-c1e4fb405b76"
      }, {
        "id" : "dd90ad64-c093-4ff4-b03a-7478fc364def",
        "name" : "view-profile",
        "description" : "${role_view-profile}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "84027d06-f768-40f0-a6c7-c1e4fb405b76"
      }, {
        "id" : "a4bd8ab0-891b-4789-b933-d60c697066c3",
        "name" : "manage-account-links",
        "description" : "${role_manage-account-links}",
        "scopeParamRequired" : false,
        "composite" : false,
        "clientRole" : true,
        "containerId" : "84027d06-f768-40f0-a6c7-c1e4fb405b76"
      } ]
    }
  },
  "groups" : [ ],
  "defaultRoles" : [ "offline_access", "uma_authorization" ],
  "requiredCredentials" : [ "password" ],
  "passwordPolicy" : "hashIterations(20000)",
  "otpPolicyType" : "totp",
  "otpPolicyAlgorithm" : "HmacSHA1",
  "otpPolicyInitialCounter" : 0,
  "otpPolicyDigits" : 6,
  "otpPolicyLookAheadWindow" : 1,
  "otpPolicyPeriod" : 30,
  "users" : [ {
    "id" : "e144b235-793b-4e2e-bb1f-1f8baccc321f",
    "createdTimestamp" : 1500389320603,
    "username" : "demo",
    "enabled" : true,
    "totp" : false,
    "emailVerified" : true,
    "firstName" : "John",
    "lastName" : "Doe",
    "email" : "demo@datascience.ch",
    "credentials" : [ {
      "type" : "password",
      "hashedSaltedValue" : "b55DNTA2fTf2o4Vhp+oyTI9EugUpwVqpexCAZ7b6++J40QzVWa1j/6P1iJ2F8y9nBx0eXy53u0VX9pZYhgdaAg==",
      "salt" : "VsYJarM+eZEMeGPx9L9tXg==",
      "hashIterations" : 20000,
      "counter" : 0,
      "algorithm" : "pbkdf2",
      "digits" : 0,
      "period" : 0,
      "createdDate" : 1500389338808,
      "config" : { }
    } ],
    "disableableCredentialTypes" : [ "password" ],
    "requiredActions" : [ ],
    "realmRoles" : [ "offline_access", "uma_authorization" ],
    "clientRoles" : {
      "account" : [ "manage-account", "view-profile" ]
    },
    "groups" : [ ]
  }, {
    "id" : "e2ba97b8-bfe3-4dd9-a619-92bcefb51537",
    "createdTimestamp" : 1500389156272,
    "username" : "service-account-demo-client",
    "enabled" : true,
    "totp" : false,
    "emailVerified" : false,
    "email" : "service-account-demo-client@placeholder.org",
    "serviceAccountClientId" : "demo-client",
    "credentials" : [ ],
    "disableableCredentialTypes" : [ ],
    "requiredActions" : [ ],
    "realmRoles" : [ "offline_access", "uma_authorization" ],
    "clientRoles" : {
      "account" : [ "manage-account", "view-profile" ]
    },
    "groups" : [ ]
  }, {
    "id" : "33a710fa-b061-4562-b31e-c89842c687e6",
    "createdTimestamp" : 1504268926763,
    "username" : "service-account-renku-services",
    "enabled" : true,
    "totp" : false,
    "emailVerified" : false,
    "email" : "service-account-renku-services@placeholder.org",
    "serviceAccountClientId" : "renku-services",
    "credentials" : [ ],
    "disableableCredentialTypes" : [ ],
    "requiredActions" : [ ],
    "realmRoles" : [ "offline_access", "uma_authorization" ],
    "clientRoles" : {
      "account" : [ "manage-account", "view-profile" ]
    },
    "groups" : [ ]
  } ],
  "clientScopeMappings" : {
    "realm-management" : [ {
      "client" : "admin-cli",
      "roles" : [ "realm-admin" ]
    }, {
      "client" : "security-admin-console",
      "roles" : [ "realm-admin" ]
    } ]
  },
  "clients" : [
  {
    "id": "4622f33f-e0ca-401f-b6a5-a5e55f937ffb",
    "clientId": "renku-ui",
    "adminUrl": "",
    "baseUrl": "{{ template "http" . }}://{{ .Values.global.renku.domain }}",
    "surrogateAuthRequired": false,
    "enabled": true,
    "clientAuthenticatorType": "client-secret",
    "secret": "no-secret-defined",
    "redirectUris": [
      "{{ template "http" . }}://{{ .Values.global.renku.domain }}/*"
    ],
    "webOrigins": [
      "{{ template "http" . }}://{{ .Values.global.renku.domain }}"
    ],
    "notBefore": 0,
    "bearerOnly": false,
    "consentRequired": false,
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "directAccessGrantsEnabled": false,
    "serviceAccountsEnabled": false,
    "publicClient": true,
    "frontchannelLogout": false,
    "protocol": "openid-connect",
    "attributes": {
      "saml.assertion.signature": "false",
      "saml.force.post.binding": "false",
      "saml.multivalued.roles": "false",
      "saml.encrypt": "false",
      "saml_force_name_id_format": "false",
      "saml.client.signature": "false",
      "saml.authnstatement": "false",
      "saml.server.signature": "false",
      "saml.server.signature.keyinfo.ext": "false",
      "saml.onetimeuse.condition": "false"
    },
    "fullScopeAllowed": true,
    "nodeReRegistrationTimeout": -1,
    "protocolMappers": [
      {
        "id": "7d833565-cb26-46eb-9e2c-13ffe924cddf",
        "name": "given name",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${givenName}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "firstName",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "given_name",
          "jsonType.label": "String"
        }
      },
      {
        "id": "fab105c9-aa79-4110-be5d-9e98b5801b99",
        "name": "family name",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${familyName}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "lastName",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "family_name",
          "jsonType.label": "String"
        }
      },
      {
        "id": "e967d8b0-9af5-451c-a7fc-8cc65df22554",
        "name": "full name",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-full-name-mapper",
        "consentRequired": true,
        "consentText": "${fullName}",
        "config": {
          "id.token.claim": "true",
          "access.token.claim": "true"
        }
      },
      {
        "id": "4bbc3374-3d02-482f-b69f-bf3a79d0c305",
        "name": "email",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${email}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "email",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "email",
          "jsonType.label": "String"
        }
      },
      {
        "id": "44480bbc-f397-4a43-870b-d9ed28489044",
        "name": "role list",
        "protocol": "saml",
        "protocolMapper": "saml-role-list-mapper",
        "consentRequired": false,
        "config": {
          "single": "false",
          "attribute.nameformat": "Basic",
          "attribute.name": "Role"
        }
      },
      {
        "id": "0c4ca494-2f26-4b9a-93bc-0a53c4a4f984",
        "name": "username",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${username}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "username",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "preferred_username",
          "jsonType.label": "String"
        }
      }
    ],
    "useTemplateConfig": false,
    "useTemplateScope": false,
    "useTemplateMappers": false
  }, {
    "id": "0c9dab4c-ce06-44a7-ba4e-6ce969c5e5cb",
    "clientId": "gitlab",
    "baseUrl": "{{ template "http" . }}://{{ .Values.global.gitlab.subdomain }}{{ .Values.global.renku.domain }}{{ .Values.global.gitlab.urlPrefix }}",
    "surrogateAuthRequired": false,
    "enabled": true,
    "clientAuthenticatorType": "client-secret",
    "secret": "{{ .Values.global.gitlab.clientSecret }}",
    "redirectUris": [
      "{{ template "http" . }}://{{ .Values.global.gitlab.subdomain }}{{ .Values.global.renku.domain }}{{ .Values.global.gitlab.urlPrefix }}users/auth/oauth2_generic/callback"
    ],
    "webOrigins": [],
    "notBefore": 0,
    "bearerOnly": false,
    "consentRequired": false,
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "authorizationServicesEnabled": true,
    "publicClient": false,
    "frontchannelLogout": false,
    "protocol": "openid-connect",
    "attributes": {
      "saml.assertion.signature": "false",
      "saml.force.post.binding": "false",
      "saml.multivalued.roles": "false",
      "saml.encrypt": "false",
      "saml_force_name_id_format": "false",
      "saml.client.signature": "false",
      "saml.authnstatement": "false",
      "saml.server.signature": "false",
      "saml.server.signature.keyinfo.ext": "false",
      "saml.onetimeuse.condition": "false"
    },
    "fullScopeAllowed": true,
    "nodeReRegistrationTimeout": -1,
    "protocolMappers": [
      {
        "id": "55ef06ad-d8db-4703-8cf7-6cd2a8abddba",
        "name": "full name",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-full-name-mapper",
        "consentRequired": true,
        "consentText": "${fullName}",
        "config": {
          "id.token.claim": "true",
          "access.token.claim": "true"
        }
      },
      {
        "id": "dee69859-6790-4a9d-83c1-2c1147a7351c",
        "name": "given name",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${givenName}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "firstName",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "given_name",
          "jsonType.label": "String"
        }
      },
      {
        "id": "df709193-9c41-48e8-be13-af820318ae81",
        "name": "Client Host",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usersessionmodel-note-mapper",
        "consentRequired": false,
        "consentText": "",
        "config": {
          "user.session.note": "clientHost",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "clientHost",
          "jsonType.label": "String"
        }
      },
      {
        "id": "ccc60a25-e3aa-48a0-9fea-dfa280df0333",
        "name": "username",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${username}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "username",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "preferred_username",
          "jsonType.label": "String"
        }
      },
      {
        "id": "1e4f7f5d-e947-4197-bfdd-9f694396fe51",
        "name": "role list",
        "protocol": "saml",
        "protocolMapper": "saml-role-list-mapper",
        "consentRequired": false,
        "config": {
          "single": "false",
          "attribute.nameformat": "Basic",
          "attribute.name": "Role"
        }
      },
      {
        "id": "a25924f5-8185-4d0d-aa4f-81d424b687d3",
        "name": "Client IP Address",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usersessionmodel-note-mapper",
        "consentRequired": false,
        "consentText": "",
        "config": {
          "user.session.note": "clientAddress",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "clientAddress",
          "jsonType.label": "String"
        }
      },
      {
        "id": "fddbb4a8-1052-4b68-a6b4-c433d4e00fdc",
        "name": "Client ID",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usersessionmodel-note-mapper",
        "consentRequired": false,
        "consentText": "",
        "config": {
          "user.session.note": "clientId",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "clientId",
          "jsonType.label": "String"
        }
      },
      {
        "id": "28ab5b96-66f0-4b96-be96-1916ae8799c0",
        "name": "email",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${email}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "email",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "email",
          "jsonType.label": "String"
        }
      },
      {
        "id": "c915b73d-f49f-4a58-89f3-8ebc197da44b",
        "name": "family name",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usermodel-property-mapper",
        "consentRequired": true,
        "consentText": "${familyName}",
        "config": {
          "userinfo.token.claim": "true",
          "user.attribute": "lastName",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "family_name",
          "jsonType.label": "String"
        }
      }
    ],
    "useTemplateConfig": false,
    "useTemplateScope": false,
    "useTemplateMappers": false,
    "authorizationSettings": {
      "allowRemoteResourceManagement": false,
      "policyEnforcementMode": "ENFORCING",
      "resources": [
        {
          "name": "Default Resource",
          "uri": "/*",
          "type": "urn:gitlab:resources:default"
        }
      ],
      "policies": [
        {
          "name": "Default Policy",
          "description": "A policy that grants access only for users within this realm",
          "type": "js",
          "logic": "POSITIVE",
          "decisionStrategy": "AFFIRMATIVE",
          "config": {
            "code": "// by default, grants any permission associated with this policy\n$evaluation.grant();\n"
          }
        },
        {
          "name": "Default Permission",
          "description": "A permission that applies to the default resource type",
          "type": "resource",
          "logic": "POSITIVE",
          "decisionStrategy": "UNANIMOUS",
          "config": {
            "defaultResourceType": "urn:gitlab:resources:default",
            "applyPolicies": "[\"Default Policy\"]"
          }
        }
      ],
      "scopes": []
    }
  }, {
    "id" : "84027d06-f768-40f0-a6c7-c1e4fb405b76",
    "clientId" : "account",
    "name" : "${client_account}",
    "baseUrl" : "/auth/realms/Renku/account",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "66e487de-a299-4273-bd9a-16096a82f5f3",
    "defaultRoles" : [ "manage-account", "view-profile" ],
    "redirectUris" : [ "/auth/realms/Renku/account/*" ],
    "webOrigins" : [ ],
    "notBefore" : 0,
    "bearerOnly" : false,
    "consentRequired" : false,
    "standardFlowEnabled" : true,
    "implicitFlowEnabled" : false,
    "directAccessGrantsEnabled" : false,
    "serviceAccountsEnabled" : false,
    "publicClient" : false,
    "frontchannelLogout" : false,
    "attributes" : { },
    "fullScopeAllowed" : false,
    "nodeReRegistrationTimeout" : 0,
    "protocolMappers" : [ {
      "id" : "22706acc-9600-428f-9105-34a0ad0274c2",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "3e35474f-306a-4071-bb4a-da007e45b597",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    }, {
      "id" : "7d493b20-5d11-4f77-83f5-f162969e59e7",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "fd0609a7-8858-451e-b281-e4b8c25a30bd",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "be3e003d-1f00-4f44-b299-95b1a1bb9fe2",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "userinfo.token.claim" : "true"
      }
    }, {
      "id" : "62f5c3c9-1e3e-4c95-add9-903667072d4a",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  }, {
    "id" : "d9dcaf86-2497-45f5-a70d-4461aa7e7979",
    "clientId" : "admin-cli",
    "name" : "${client_admin-cli}",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "27407cde-de37-49fc-9e08-f694bf0fad1c",
    "redirectUris" : [ ],
    "webOrigins" : [ ],
    "notBefore" : 0,
    "bearerOnly" : false,
    "consentRequired" : false,
    "standardFlowEnabled" : false,
    "implicitFlowEnabled" : false,
    "directAccessGrantsEnabled" : true,
    "serviceAccountsEnabled" : false,
    "publicClient" : true,
    "frontchannelLogout" : false,
    "attributes" : { },
    "fullScopeAllowed" : false,
    "nodeReRegistrationTimeout" : 0,
    "protocolMappers" : [ {
      "id" : "58886e81-9b9a-4256-8605-af5f0025b7fc",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "68d85806-efec-48f3-a50c-1ceef1244c11",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "78363e2e-8acf-4b1b-8d38-d4cb593a91a4",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "48d9d35a-9d5d-4106-96fa-ab4d81a56595",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "5f94a671-5d9b-4af5-88e4-3823c05fdbe6",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "userinfo.token.claim" : "true"
      }
    }, {
      "id" : "791c53e2-e472-4f92-ae80-ada7d09328a0",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  }, {
    "id" : "c67e0adb-52bc-482b-8c00-ee25247041e6",
    "clientId" : "broker",
    "name" : "${client_broker}",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "d8970f54-d546-4dc8-8b48-87635bac69ab",
    "redirectUris" : [ ],
    "webOrigins" : [ ],
    "notBefore" : 0,
    "bearerOnly" : false,
    "consentRequired" : false,
    "standardFlowEnabled" : true,
    "implicitFlowEnabled" : false,
    "directAccessGrantsEnabled" : false,
    "serviceAccountsEnabled" : false,
    "publicClient" : false,
    "frontchannelLogout" : false,
    "attributes" : { },
    "fullScopeAllowed" : false,
    "nodeReRegistrationTimeout" : 0,
    "protocolMappers" : [ {
      "id" : "fd3226d6-1009-4537-81cc-71a10ccf3f87",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "e648ac65-b3a2-4fef-98bc-d9a045340217",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "userinfo.token.claim" : "true"
      }
    }, {
      "id" : "2fe1beb5-47b6-46dd-be6e-96e6ba113008",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "ba9e6b16-fae2-4895-a46d-cd1662e9d9fd",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "a8f14c4e-ac04-4e60-9f37-2d4fa7ecfde8",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "2f0d8d55-a43e-4172-af32-ce31cd15de02",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  }, {
    "id" : "901f5949-b368-46da-9a40-ceb27bd45fc9",
    "clientId" : "demo-client",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "5294a18e-e784-4e39-a927-ce816c91c83e",
    "redirectUris" : [ "{{ template "http" . }}://{{ .Values.global.renku.domain }}/*" ],
    "webOrigins" : [ "{{ template "http" . }}://{{ .Values.global.renku.domain }}" ],
    "notBefore" : 0,
    "bearerOnly" : false,
    "consentRequired" : false,
    "standardFlowEnabled" : true,
    "implicitFlowEnabled" : true,
    "directAccessGrantsEnabled" : true,
    "serviceAccountsEnabled" : true,
    "publicClient" : true,
    "frontchannelLogout" : false,
    "protocol" : "openid-connect",
    "attributes" : {
      "saml.assertion.signature" : "false",
      "saml.force.post.binding" : "false",
      "saml.multivalued.roles" : "false",
      "saml.encrypt" : "false",
      "saml_force_name_id_format" : "false",
      "saml.client.signature" : "false",
      "saml.authnstatement" : "false",
      "saml.server.signature" : "false",
      "saml.server.signature.keyinfo.ext" : "false",
      "saml.onetimeuse.condition" : "false"
    },
    "fullScopeAllowed" : true,
    "nodeReRegistrationTimeout" : -1,
    "protocolMappers" : [ {
      "id" : "b602730c-7dea-4ab8-ae1b-fdb6396d2839",
      "name" : "Client Host",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usersessionmodel-note-mapper",
      "consentRequired" : false,
      "consentText" : "",
      "config" : {
        "user.session.note" : "clientHost",
        "userinfo.token.claim" : "true",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "clientHost",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "2edaee54-11c8-4ff0-9a8a-b522a069a10c",
      "name" : "Client IP Address",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usersessionmodel-note-mapper",
      "consentRequired" : false,
      "consentText" : "",
      "config" : {
        "user.session.note" : "clientAddress",
        "userinfo.token.claim" : "true",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "clientAddress",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "991d047d-585f-49c3-9e20-62f9979d52e6",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "cfe014d1-74ef-4b81-985e-3052792d6481",
      "name" : "Client ID",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usersessionmodel-note-mapper",
      "consentRequired" : false,
      "consentText" : "",
      "config" : {
        "user.session.note" : "clientId",
        "userinfo.token.claim" : "true",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "clientId",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "477dd365-6f3b-4bce-9039-50b6fe5b1ae9",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "6a1fd139-f904-4e48-b50e-5ba83f3f0e95",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "userinfo.token.claim" : "true"
      }
    }, {
      "id" : "c9875654-21d6-4aaf-8322-2e5bfb350d3c",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "934186fc-517f-4cb7-9f8e-79d45c64b05e",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    }, {
      "id" : "5130a8a5-21a6-4a35-afc8-af7083af3cd5",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  }, {
    "id" : "dcd909d4-22f3-4e70-b948-11944a8e0824",
    "clientId" : "realm-management",
    "name" : "${client_realm-management}",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "db06e17d-e2c4-4c52-b9cd-fbc663eb71d8",
    "redirectUris" : [ ],
    "webOrigins" : [ ],
    "notBefore" : 0,
    "bearerOnly" : true,
    "consentRequired" : false,
    "standardFlowEnabled" : true,
    "implicitFlowEnabled" : false,
    "directAccessGrantsEnabled" : false,
    "serviceAccountsEnabled" : false,
    "publicClient" : false,
    "frontchannelLogout" : false,
    "attributes" : { },
    "fullScopeAllowed" : false,
    "nodeReRegistrationTimeout" : 0,
    "protocolMappers" : [ {
      "id" : "6ca5289b-7312-4957-9877-5fbd8438b9c8",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "2b4b1c1d-0235-47aa-91f9-207704e20ee3",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "1b95421b-6273-4a15-be65-3644aeab576d",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "e28c0831-ad07-43a0-bc21-0ac5dc67574d",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "userinfo.token.claim" : "true"
      }
    }, {
      "id" : "4b5e647e-48a2-4e9b-8a52-aa787f93e1b3",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    }, {
      "id" : "d6813ae9-85ca-41ec-85b5-04e634005e45",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  }, {
    "id" : "c8fc33a7-6a6e-4a2a-b05c-b3ecea717db8",
    "clientId" : "renku-services",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "d7f63e67-e74d-4935-bc4b-7803f2b995e8",
    "redirectUris" : [ ],
    "webOrigins" : [ ],
    "notBefore" : 0,
    "bearerOnly" : false,
    "consentRequired" : false,
    "standardFlowEnabled" : false,
    "implicitFlowEnabled" : false,
    "directAccessGrantsEnabled" : false,
    "serviceAccountsEnabled" : true,
    "publicClient" : false,
    "frontchannelLogout" : false,
    "protocol" : "openid-connect",
    "attributes" : {
      "saml.assertion.signature" : "false",
      "saml.force.post.binding" : "false",
      "saml.multivalued.roles" : "false",
      "saml.encrypt" : "false",
      "saml_force_name_id_format" : "false",
      "saml.client.signature" : "false",
      "saml.authnstatement" : "false",
      "saml.server.signature" : "false",
      "saml.server.signature.keyinfo.ext" : "false",
      "saml.onetimeuse.condition" : "false"
    },
    "fullScopeAllowed" : true,
    "nodeReRegistrationTimeout" : -1,
    "protocolMappers" : [ {
      "id" : "4998692f-eee6-47ba-b8a1-e44636eed075",
      "name" : "Client Host",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usersessionmodel-note-mapper",
      "consentRequired" : false,
      "consentText" : "",
      "config" : {
        "user.session.note" : "clientHost",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "clientHost",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "641b60e7-3d38-44ae-ba4b-8e52e6db8d4a",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "9fea3f53-8f4b-44c0-92b7-b01d528e6513",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    }, {
      "id" : "4e0d5802-39ca-46a3-9996-87642a8b2e5c",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "de48e130-8f75-4a70-8ad4-2bac262fd648",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "1f0a7fe1-5cf5-4f29-8bbf-00db94dd91e2",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true"
      }
    }, {
      "id" : "be01cfe0-0419-4589-9ee7-4c0451ddded9",
      "name" : "Client IP Address",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usersessionmodel-note-mapper",
      "consentRequired" : false,
      "consentText" : "",
      "config" : {
        "user.session.note" : "clientAddress",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "clientAddress",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "9eb008cd-7160-4d44-9074-ea5b61ef852b",
      "name" : "Client ID",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usersessionmodel-note-mapper",
      "consentRequired" : false,
      "consentText" : "",
      "config" : {
        "user.session.note" : "clientId",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "clientId",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "1e4da7da-76cf-496d-b771-a92497200124",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  }, {
    "id" : "2f766f4f-1104-46cb-93c7-3b245e21eb1b",
    "clientId" : "security-admin-console",
    "name" : "${client_security-admin-console}",
    "baseUrl" : "/auth/admin/Renku/console/index.html",
    "surrogateAuthRequired" : false,
    "enabled" : true,
    "clientAuthenticatorType" : "client-secret",
    "secret" : "a58e1789-82dc-45a1-8efc-3ece38e4730a",
    "redirectUris" : [ "/auth/admin/Renku/console/*" ],
    "webOrigins" : [ ],
    "notBefore" : 0,
    "bearerOnly" : false,
    "consentRequired" : false,
    "standardFlowEnabled" : true,
    "implicitFlowEnabled" : false,
    "directAccessGrantsEnabled" : false,
    "serviceAccountsEnabled" : false,
    "publicClient" : true,
    "frontchannelLogout" : false,
    "attributes" : { },
    "fullScopeAllowed" : false,
    "nodeReRegistrationTimeout" : 0,
    "protocolMappers" : [ {
      "id" : "3fd93ced-fb33-4927-adec-812d64414c34",
      "name" : "given name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${givenName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "firstName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "given_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "0e031614-e754-4869-b0d6-5d7e1e9a0f98",
      "name" : "username",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${username}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "username",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "preferred_username",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "af9604ff-244c-4dfb-a473-de2375040552",
      "name" : "full name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-full-name-mapper",
      "consentRequired" : true,
      "consentText" : "${fullName}",
      "config" : {
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "userinfo.token.claim" : "true"
      }
    }, {
      "id" : "f577557d-4ade-4884-b0e2-b188960944c9",
      "name" : "email",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${email}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "email",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "email",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "62ded686-4f03-4703-ab0c-e77f257e2b59",
      "name" : "family name",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-property-mapper",
      "consentRequired" : true,
      "consentText" : "${familyName}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "lastName",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "family_name",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "716e81bc-d9ea-40d7-a8c7-d54229a7b1fc",
      "name" : "locale",
      "protocol" : "openid-connect",
      "protocolMapper" : "oidc-usermodel-attribute-mapper",
      "consentRequired" : false,
      "consentText" : "${locale}",
      "config" : {
        "userinfo.token.claim" : "true",
        "user.attribute" : "locale",
        "id.token.claim" : "true",
        "access.token.claim" : "true",
        "claim.name" : "locale",
        "jsonType.label" : "String"
      }
    }, {
      "id" : "ab2ea216-336a-49c4-b00c-952f2fbe5c37",
      "name" : "role list",
      "protocol" : "saml",
      "protocolMapper" : "saml-role-list-mapper",
      "consentRequired" : false,
      "config" : {
        "single" : "false",
        "attribute.nameformat" : "Basic",
        "attribute.name" : "Role"
      }
    } ],
    "useTemplateConfig" : false,
    "useTemplateScope" : false,
    "useTemplateMappers" : false
  } ],
  "clientTemplates" : [ ],
  "browserSecurityHeaders" : {
    "xContentTypeOptions" : "nosniff",
    "xRobotsTag" : "none",
    "xFrameOptions" : "SAMEORIGIN",
    "xXSSProtection" : "1; mode=block",
    "contentSecurityPolicy" : "frame-src 'self'"
  },
  "smtpServer" : { },
  "eventsEnabled" : false,
  "eventsListeners" : [ "jboss-logging" ],
  "enabledEventTypes" : [ ],
  "adminEventsEnabled" : false,
  "adminEventsDetailsEnabled" : false,
  "components" : {
    "org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy" : [ {
      "id" : "6735802a-24f2-404c-978b-0c10723add93",
      "name" : "Allowed Client Templates",
      "providerId" : "allowed-client-templates",
      "subType" : "authenticated",
      "subComponents" : { },
      "config" : { }
    }, {
      "id" : "1d85411a-ed90-4723-a443-a7a959d51120",
      "name" : "Allowed Client Templates",
      "providerId" : "allowed-client-templates",
      "subType" : "anonymous",
      "subComponents" : { },
      "config" : { }
    }, {
      "id" : "b8d68e32-3c1f-4863-8dd7-6460508d441e",
      "name" : "Consent Required",
      "providerId" : "consent-required",
      "subType" : "anonymous",
      "subComponents" : { },
      "config" : { }
    }, {
      "id" : "08b6d92c-e5f9-40f3-be56-e80c24115dc4",
      "name" : "Max Clients Limit",
      "providerId" : "max-clients",
      "subType" : "anonymous",
      "subComponents" : { },
      "config" : {
        "max-clients" : [ "200" ]
      }
    }, {
      "id" : "c4b7a4af-9829-4c1f-bced-edf7c4cb1065",
      "name" : "Full Scope Disabled",
      "providerId" : "scope",
      "subType" : "anonymous",
      "subComponents" : { },
      "config" : { }
    }, {
      "id" : "09003b6e-8926-4889-bd9a-3fd87fd9f514",
      "name" : "Trusted Hosts",
      "providerId" : "trusted-hosts",
      "subType" : "anonymous",
      "subComponents" : { },
      "config" : {
        "host-sending-registration-request-must-match" : [ "true" ],
        "client-uris-must-match" : [ "true" ]
      }
    }, {
      "id" : "9d1e69fc-0f0e-4722-8abc-0ad96166ec6a",
      "name" : "Allowed Protocol Mapper Types",
      "providerId" : "allowed-protocol-mappers",
      "subType" : "authenticated",
      "subComponents" : { },
      "config" : {
        "allowed-protocol-mapper-types" : [ "saml-role-list-mapper", "saml-user-attribute-mapper", "oidc-usermodel-attribute-mapper", "oidc-address-mapper", "oidc-usermodel-property-mapper", "oidc-full-name-mapper", "saml-user-property-mapper", "oidc-sha256-pairwise-sub-mapper" ],
        "consent-required-for-all-mappers" : [ "true" ]
      }
    }, {
      "id" : "b020950f-cc06-4230-a51c-9f3c8dcd9ea0",
      "name" : "Allowed Protocol Mapper Types",
      "providerId" : "allowed-protocol-mappers",
      "subType" : "anonymous",
      "subComponents" : { },
      "config" : {
        "allowed-protocol-mapper-types" : [ "saml-role-list-mapper", "oidc-address-mapper", "oidc-usermodel-attribute-mapper", "saml-user-property-mapper", "oidc-usermodel-property-mapper", "oidc-full-name-mapper", "oidc-sha256-pairwise-sub-mapper", "saml-user-attribute-mapper" ],
        "consent-required-for-all-mappers" : [ "true" ]
      }
    } ],
    "org.keycloak.keys.KeyProvider" : [ {
      "id" : "9a8b439e-fa32-4343-a378-b6087189979d",
      "name" : "rsa-generated",
      "providerId" : "rsa-generated",
      "subComponents" : { },
      "config" : {
        "privateKey" : [ "MIIEowIBAAKCAQEAjw2hj0o98jYxT0Z8hbd6INgJkVs2JvT6zXhHkN0UUWjGcRHF3e7Sc9GNI9wYljnBw47jqSmy2EftZ0UkGNjLENmGuLVC5r6vTneXfUht5t0+e5VelnM7yF7m9V3w/ms4wc0vDmSMa7pO5Vb+qjsTHgLTjQVqBIhGshxmZzKk6XDDVRlXe3SfLqwLX1biBmmvEOadU+2RhsHVW4rYMEaEHO1tCvRTsqiD7gVfk0XzZQg6KBEr2pDhz7hdWAfWK+/k1JiK/MNTs3FCfOqoBlTa6dZB/XRrKx0Pbi7y5Cr6BqqPUJzW5dbCYRzjmjL5CYx4KYHAjSSoCCpUjCHLILPPJQIDAQABAoIBAHVM/RxNCWySKW7S4oqW/4rs+zN4snfRS9Kt/Sj5T47NtmLC6xfnnCQiQXSVJogQhiUABwgQTDlzCWUz2bySEDkZ1ZGh+GDTHVbfU5YEjTHQW0rlGXGOvge4YfPy+wj0DM9Fm39WzZoMZGNEKYCW/j8OnD+3n85lnGqyRPn8GiPb96PhCZK4rithYwIwynFxUCzahXU94GYY7A5b22jGPsMXDcd+rQhxb8QpAoq6CtJT9WF7xavBDUAzSOjF8luwQOtuKJWpZsI7SoWkJJP5H031XG1UKwc2KDpZx/lcwSPM53b96J9qRIpGGBK8TYUI/gDPctq9ZQBwPlh6q6xA+UUCgYEA4z4FD9aaJwo4X3YDq0l+LRPrTUXKhk0b5DoNqHQVeSKLVsao2AEEaZlYaW8NF2lQEvSe8uTV/emuDpn0uc6HG55Cq8MGnFMPOP0M4CKeDvzFcvvaKIweTWmlccWqvtQ7lPfWc8M78SNJoBys7A0sy3AcBgNkNDSvgvofl3V8+DsCgYEAoSgikaocg8GTxc8sRZNz2TskCCqW2kq6w3uq4bEt7MFQaf36wP8hhuw3gyQDtRXautx+EIIcKVpkhyx8Ma85XVQpN2Ov3JaITjx17MeQ/qoeBrcYLd0Q7fc/BXckPsu3jEB7lDNOmiG2FENe3N7E7aj4QkkT5Bqk/Q9hnVRZQB8CgYAwQlWU3YBzVgpOy6NP1xXCwP4xtOajyvBncaTA55Y/2YQ584qcBOtI+dX63iirliP5QpYXA14mxF/AnhWI+EFdLij+jTZ0MHeFJMh9ORzUFf5gwve7sLYmvo7yMIZobG4S3udeQlISjenu5OgcYVfoBHEPHd1D8QRtDm1zHvJhTQKBgEr84PEExdXtGjS+DOynCw32vfJq1tzmTvctMTtpdWQoI6HeQ3LQhKpCeiY0eOEnNSsj9Cj8UdxQKlzXr0ZzMW8i/Ta/1RIAnZZB/eFajzZgtC4NvluA9SK1nIaPhLcPle3WB2rTYOzJaO8O1jJSGjI4IKS/WlkWqNTz9kj9+bWjAoGBAOG+5JmJomw/51m3kCE5kq7y7aZaB9HBkuCDS0t3LH8fSkTc3E6llrStrBYhrEbkuPmIGetUYxbhsnkZwMmyL2A67DrLaADweGOdqS6zo4r3N5XM+cG8hdojws1/b4L8Fw7WKtMSP/FyfXkAC4/j7lBfzpQRmvaXBNkjJXWMknhw" ],
        "certificate" : [ "MIICmTCCAYECBgFePWfDJjANBgkqhkiG9w0BAQsFADAQMQ4wDAYDVQQDDAVSZW5nYTAeFw0xNzA5MDExMjI0MjNaFw0yNzA5MDExMjI2MDNaMBAxDjAMBgNVBAMMBVJlbmdhMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjw2hj0o98jYxT0Z8hbd6INgJkVs2JvT6zXhHkN0UUWjGcRHF3e7Sc9GNI9wYljnBw47jqSmy2EftZ0UkGNjLENmGuLVC5r6vTneXfUht5t0+e5VelnM7yF7m9V3w/ms4wc0vDmSMa7pO5Vb+qjsTHgLTjQVqBIhGshxmZzKk6XDDVRlXe3SfLqwLX1biBmmvEOadU+2RhsHVW4rYMEaEHO1tCvRTsqiD7gVfk0XzZQg6KBEr2pDhz7hdWAfWK+/k1JiK/MNTs3FCfOqoBlTa6dZB/XRrKx0Pbi7y5Cr6BqqPUJzW5dbCYRzjmjL5CYx4KYHAjSSoCCpUjCHLILPPJQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQARL3CB1iaT9bQSf+XYN69HK6mIL6celrZxoWF5ugKXfioIadAK/Pm76zT0QHHjlgfC/ln6sjU9pnVEbIyioRM0zgVuegSogt18qw3tRLTp3swA71v7EwGjGc79c+5q2JgTpkjMaf38lebm6E9CRye4chY9Ku9+GpZlQRR3L1L0OtdQAWvapBoIWdQJuJjI1KYIH+iKybSCKE3IVM2llWWg0K78PuWMm7euSYO3PLtR+FzdpS0YqDMHZNXIZtFPbvgeO6AfYXV0pSjM/q4OYEEYM7EKnaCHx1+/QcxrVVjcyck/n2tUE9AblAHot3oypy1u7YFDPg2W8QH+UediubTv" ],
        "priority" : [ "100" ]
      }
    }, {
      "id" : "c8c8848d-ed9f-467a-a561-a187f5302fb4",
      "name" : "hmac-generated",
      "providerId" : "hmac-generated",
      "subComponents" : { },
      "config" : {
        "kid" : [ "085601df-2f4e-4d92-bf4c-f560741f52aa" ],
        "secret" : [ "BbpeX7WpCGvu5Su5sBN2Ii4glS3lIPz8CDfl7WUKB_c" ],
        "priority" : [ "100" ]
      }
    } ]
  },
  "internationalizationEnabled" : false,
  "supportedLocales" : [ ],
  "authenticationFlows" : [ {
    "id" : "84d14e96-411b-43a8-bdd8-074b3e7dbda2",
    "alias" : "Handle Existing Account",
    "description" : "Handle what to do if there is existing account with same email/username like authenticated identity provider",
    "providerId" : "basic-flow",
    "topLevel" : false,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "idp-confirm-link",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "idp-email-verification",
      "requirement" : "ALTERNATIVE",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "requirement" : "ALTERNATIVE",
      "priority" : 30,
      "flowAlias" : "Verify Existing Account by Re-authentication",
      "userSetupAllowed" : false,
      "autheticatorFlow" : true
    } ]
  }, {
    "id" : "fadd596c-4970-400f-8fc2-a6edad4b6e31",
    "alias" : "Verify Existing Account by Re-authentication",
    "description" : "Reauthentication of existing account",
    "providerId" : "basic-flow",
    "topLevel" : false,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "idp-username-password-form",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "auth-otp-form",
      "requirement" : "OPTIONAL",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "54bae87c-52c5-4fbc-b419-a0db030eec5c",
    "alias" : "browser",
    "description" : "browser based authentication",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "auth-cookie",
      "requirement" : "ALTERNATIVE",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "auth-spnego",
      "requirement" : "DISABLED",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "identity-provider-redirector",
      "requirement" : "ALTERNATIVE",
      "priority" : 25,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "requirement" : "ALTERNATIVE",
      "priority" : 30,
      "flowAlias" : "forms",
      "userSetupAllowed" : false,
      "autheticatorFlow" : true
    } ]
  }, {
    "id" : "994aa3fb-76b1-458b-86c7-6cfd2cc0b302",
    "alias" : "clients",
    "description" : "Base authentication for clients",
    "providerId" : "client-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "client-secret",
      "requirement" : "ALTERNATIVE",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "client-jwt",
      "requirement" : "ALTERNATIVE",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "8f14bcaf-7b7e-4e85-937f-fef884566575",
    "alias" : "direct grant",
    "description" : "OpenID Connect Resource Owner Grant",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "direct-grant-validate-username",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "direct-grant-validate-password",
      "requirement" : "REQUIRED",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "direct-grant-validate-otp",
      "requirement" : "OPTIONAL",
      "priority" : 30,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "437d2009-400b-4d8e-a7c3-0ddd36b46550",
    "alias" : "docker auth",
    "description" : "Used by Docker clients to authenticate against the IDP",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "docker-http-basic-authenticator",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "e53ce67d-a431-48cd-bcd1-00f9b63682cf",
    "alias" : "first broker login",
    "description" : "Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticatorConfig" : "review profile config",
      "authenticator" : "idp-review-profile",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticatorConfig" : "create unique user config",
      "authenticator" : "idp-create-user-if-unique",
      "requirement" : "ALTERNATIVE",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "requirement" : "ALTERNATIVE",
      "priority" : 30,
      "flowAlias" : "Handle Existing Account",
      "userSetupAllowed" : false,
      "autheticatorFlow" : true
    } ]
  }, {
    "id" : "84c88b6a-ebde-4dbf-a516-4a3738242942",
    "alias" : "forms",
    "description" : "Username, password, otp and other auth forms.",
    "providerId" : "basic-flow",
    "topLevel" : false,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "auth-username-password-form",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "auth-otp-form",
      "requirement" : "OPTIONAL",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "f0aa0eb2-a111-4f46-99d4-52b1630e7928",
    "alias" : "registration",
    "description" : "registration flow",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "registration-page-form",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "flowAlias" : "registration form",
      "userSetupAllowed" : false,
      "autheticatorFlow" : true
    } ]
  }, {
    "id" : "f18baa9a-5652-40e1-9c40-ff8c4d2a078e",
    "alias" : "registration form",
    "description" : "registration form",
    "providerId" : "form-flow",
    "topLevel" : false,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "registration-user-creation",
      "requirement" : "REQUIRED",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "registration-profile-action",
      "requirement" : "REQUIRED",
      "priority" : 40,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "registration-password-action",
      "requirement" : "REQUIRED",
      "priority" : 50,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "registration-recaptcha-action",
      "requirement" : "DISABLED",
      "priority" : 60,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "f8e93cb2-e405-44a8-8053-89ea4a9f926a",
    "alias" : "reset credentials",
    "description" : "Reset credentials for a user if they forgot their password or something",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "reset-credentials-choose-user",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "reset-credential-email",
      "requirement" : "REQUIRED",
      "priority" : 20,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "reset-password",
      "requirement" : "REQUIRED",
      "priority" : 30,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    }, {
      "authenticator" : "reset-otp",
      "requirement" : "OPTIONAL",
      "priority" : 40,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  }, {
    "id" : "e90b619f-db99-4b73-8fe1-f9f6555c17bf",
    "alias" : "saml ecp",
    "description" : "SAML ECP Profile Authentication Flow",
    "providerId" : "basic-flow",
    "topLevel" : true,
    "builtIn" : true,
    "authenticationExecutions" : [ {
      "authenticator" : "http-basic-authenticator",
      "requirement" : "REQUIRED",
      "priority" : 10,
      "userSetupAllowed" : false,
      "autheticatorFlow" : false
    } ]
  } ],
  "authenticatorConfig" : [ {
    "id" : "e252753c-272c-4241-8a9a-431f548179cb",
    "alias" : "create unique user config",
    "config" : {
      "require.password.update.after.registration" : "false"
    }
  }, {
    "id" : "b018f75e-c1ed-4d41-a0b2-a10ab575b866",
    "alias" : "review profile config",
    "config" : {
      "update.profile.on.first.login" : "missing"
    }
  } ],
  "requiredActions" : [ {
    "alias" : "CONFIGURE_TOTP",
    "name" : "Configure OTP",
    "providerId" : "CONFIGURE_TOTP",
    "enabled" : true,
    "defaultAction" : false,
    "config" : { }
  }, {
    "alias" : "UPDATE_PASSWORD",
    "name" : "Update Password",
    "providerId" : "UPDATE_PASSWORD",
    "enabled" : true,
    "defaultAction" : false,
    "config" : { }
  }, {
    "alias" : "UPDATE_PROFILE",
    "name" : "Update Profile",
    "providerId" : "UPDATE_PROFILE",
    "enabled" : true,
    "defaultAction" : false,
    "config" : { }
  }, {
    "alias" : "VERIFY_EMAIL",
    "name" : "Verify Email",
    "providerId" : "VERIFY_EMAIL",
    "enabled" : true,
    "defaultAction" : false,
    "config" : { }
  }, {
    "alias" : "terms_and_conditions",
    "name" : "Terms and Conditions",
    "providerId" : "terms_and_conditions",
    "enabled" : false,
    "defaultAction" : false,
    "config" : { }
  } ],
  "browserFlow" : "browser",
  "registrationFlow" : "registration",
  "directGrantFlow" : "direct grant",
  "resetCredentialsFlow" : "reset credentials",
  "clientAuthenticationFlow" : "clients",
  "dockerAuthenticationFlow" : "docker auth",
  "attributes" : {
    "_browser_header.xXSSProtection" : "1; mode=block",
    "_browser_header.xFrameOptions" : "SAMEORIGIN",
    "quickLoginCheckMilliSeconds" : "1000",
    "permanentLockout" : "false",
    "_browser_header.xRobotsTag" : "none",
    "maxFailureWaitSeconds" : "900",
    "minimumQuickLoginWaitSeconds" : "60",
    "failureFactor" : "30",
    "actionTokenGeneratedByUserLifespan" : "300",
    "maxDeltaTimeSeconds" : "43200",
    "_browser_header.xContentTypeOptions" : "nosniff",
    "actionTokenGeneratedByAdminLifespan" : "43200",
    "bruteForceProtected" : "false",
    "_browser_header.contentSecurityPolicy" : "frame-src 'self'",
    "waitIncrementSeconds" : "60"
  },
  "keycloakVersion" : "3.2.0.Final"
}
{{- end -}}
