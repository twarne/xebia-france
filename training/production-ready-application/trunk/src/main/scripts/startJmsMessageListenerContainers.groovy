#!/usr/bin/env groovy

import javax.management.ObjectName;
import javax.management.Query;

import groovy.jmx.builder.JmxBuilder;


def jmx = new JmxBuilder()

def jmxClient = jmx.connectorClient (host:"localhost",port:6969)
println("Start JMS message listeners on $jmxClient")

jmxClient.connect()

def server = jmxClient.getMBeanServerConnection()

// javax.jms:destination=my-destination,name="fr.xebia.springframework.jms.ManagedDefaultMessageListenerContainer#0",type=MessageListenerContainer,host=localhost,path=/production-ready-application
server.queryNames(new ObjectName('javax.jms:type=MessageListenerContainer,*'), null).each { name ->
    def jmsMessageListenerContainer = new GroovyMBean(server, name)
    jmsMessageListenerContainer.start()
    if (jmsMessageListenerContainer.Running) {
        println("jmsMessageListenerContainer $name successfully started")
    } else {
        println("FAILURE to start jmsMessageListenerContainer $name !")
    }
}
jmxClient.close()
println "JMS message listeners successfully started. Bye"
