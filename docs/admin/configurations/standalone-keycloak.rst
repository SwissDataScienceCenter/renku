.. _standalone-keycloak:

Stand alone Keycloak: Renku configuration
=========================================


If you have your own keycloak instance you need to:

- Create a Realm named Renku
- Setup the prefered parameters regarding user registration, token lifetime and activation of OTP.
- Import the four clients from their respective json files in the keycloak-clients folder: gateway.json, gitlab.json, renku-services.json, renku-ui.json
- For the gitlab client copy the secret from the Credentials tab and paste it at the dedicated place in renku-values.yaml.
- Same as above for gateway client.

