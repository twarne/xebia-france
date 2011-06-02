#!/usr/bin/env groovy

def activeMqUrl = "tcp://localhost:61616"
def activeMqUsername = null
def activeMqPassword = null
def queueName = 'hello-world-queue'

println "Send a jms text message to url=$activeMqUrl username=$activeMqUsername queue=$queueName"

// IMPORTS JARS
@GrabResolver(name="central",root="http://repo1.maven.org/maven2/",m2compatible=true)
@Grab(group="org.apache.geronimo.specs", module="geronimo-jms_1.1_spec", version="1.1.1")
@Grab(group='org.apache.activemq', module='activemq-core', version='5.5.0')

// JMS STUFF

import javax.jms.Connection
import javax.jms.DeliveryMode
import javax.jms.Message
import javax.jms.MessageProducer
import javax.jms.Queue
import javax.jms.Session
import javax.jms.TextMessage

import org.apache.activemq.ActiveMQConnectionFactory

ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMqUrl)

Connection connection
if(activeMqUsername) {
    connection = connectionFactory.createConnection(activeMqUsername, activeMqPassword)
} else {
    connection = connectionFactory.createConnection()
}

Session session = connection.createSession (false, Session.AUTO_ACKNOWLEDGE )

Queue queue = session.createQueue(queueName)

TextMessage message = session.createTextMessage ('hello Groovy world !')

MessageProducer messageProducer = session.createProducer(queue)

long messageTimeToLiveInMillis = 5 * 60 * 1000

messageProducer.send(message, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, messageTimeToLiveInMillis)

println "Sent: $message"

messageProducer.close()
session.close()
connection.close()

println "Bye"