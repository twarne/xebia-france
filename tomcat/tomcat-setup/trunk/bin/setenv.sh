#!/bin/bash

CATALINA_HOME=/usr/local/apache-tomcat-6.0.20/
CATALINA_BASE=/usr/local/tomcat-instance-1

CATALINA_OPTS=" \
	-server \
	-Xmx512m \
	-Dcom.sun.management.jmxremote.port=$JMX_PORT \
	-Dcom.sun.management.jmxremote.ssl=false \
	-Dcom.sun.management.jmxremote.authenticate=false"

export CATALINA_HOME CATALINA_BASE CATALINA_OPTS