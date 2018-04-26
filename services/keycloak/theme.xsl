<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template xmlns="urn:jboss:domain:keycloak-server:1.1" xmlns:ut="urn:jboss:domain:keycloak-server:1.1" match="//ut:subsystem/ut:theme">
        <theme>
            <xsl:apply-templates select="node()|@*"/>
            <modules>
                <module>ch.datascience.theme</module>
            </modules>
        </theme>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
