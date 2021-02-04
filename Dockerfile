FROM jboss/wildfly

ARG WAR_FILE=target/*.war

ADD ${WAR_FILE} /opt/jboss/wildfly/standalone/deployments/