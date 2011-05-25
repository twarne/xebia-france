#!/usr/bin/groovy


import javax.management.ObjectName;
import javax.management.Query;

import groovy.jmx.builder.JmxBuilder;

/**
 * 
 */
class startJmsMessageListenerContainers {

    static void main(String[] args) {
        def jmx = new JmxBuilder()

        def jmxClient = jmx.connectorClient (host:"localhost",port:6969)
        println("Establish JMX Connection to $jmxClient")

        jmxClient.connect()

        def server = jmxClient.getMBeanServerConnection()

        // javax.jms:destination=my-destination,name="fr.xebia.springframework.jms.ManagedDefaultMessageListenerContainer#0",type=MessageListenerContainer,host=localhost,path=/production-ready-application
        server.queryNames(new ObjectName('javax.jms:type=MessageListenerContainer,*'), null).each { name ->
            println("Start JMX Message Listener $name")
            def jmsMessageListenerContainer = new GroovyMBean(server, name)
            jmsMessageListenerContainer.start()
            if (jmsMessageListenerContainer.Running == false) {
                println("FAILURE to start jmsMessageListenerContainer ! $name")
            }
        }
        jmxClient.close()
        println "JMS message listeners are started. Bye"
    }
}
