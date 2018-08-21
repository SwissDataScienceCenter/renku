#!/bin/sh

git clone https://github.com/SwissDataScienceCenter/renku-demo.git
cd renku-demo
KEYCLOAK_URL=$RENKU_ENDPOINT GITLAB_URL=${RENKU_ENDPOINT}/gitlab ./cleanup.sh
cd ..
rm -rf renku-demo
