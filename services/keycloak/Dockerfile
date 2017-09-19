FROM jboss/keycloak-postgres:3.2.0.Final

USER root
COPY . /data
RUN chown jboss:jboss -R /data
USER jboss

ENTRYPOINT ["/data/docker-entrypoint.sh"]
