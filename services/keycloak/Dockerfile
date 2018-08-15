FROM jboss/keycloak-postgres:3.0.0.Final

USER root
COPY . /data
RUN chown jboss:jboss -R /data

ADD changeProxy.xsl theme.xsl /opt/jboss/keycloak/

# Install theme
ADD https://github.com/jirikuncar/keycloak-sdsc-theme/archive/master.zip /opt/jboss/keycloak/keycloak-sdsc-theme.zip

RUN cd /opt/jboss/keycloak && \
    chown jboss:jboss keycloak-sdsc-theme.zip && \
    chmod 644 keycloak-sdsc-theme.zip && \
    bin/jboss-cli.sh --command="module add --name=ch.datascience.theme --resources=keycloak-sdsc-theme.zip" && \
    java -jar /usr/share/java/saxon.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone.xml \
    -xsl:/opt/jboss/keycloak/theme.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone.xml && \
    java -jar /usr/share/java/saxon.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone-ha.xml \
    -xsl:/opt/jboss/keycloak/theme.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone-ha.xml
USER jboss

RUN java -jar /usr/share/java/saxon.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone.xml -xsl:/opt/jboss/keycloak/changeProxy.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone.xml; java -jar /usr/share/java/saxon.jar -s:/opt/jboss/keycloak/standalone/configuration/standalone-ha.xml -xsl:/opt/jboss/keycloak/changeProxy.xsl -o:/opt/jboss/keycloak/standalone/configuration/standalone-ha.xml; rm /opt/jboss/keycloak/changeProxy.xsl

ENTRYPOINT ["/data/docker-entrypoint.sh"]
