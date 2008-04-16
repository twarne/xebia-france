/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.jms;

import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import junit.framework.Assert;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class ActiveMqTest {

    protected Connection connection;

    private void dumpMessageHeaders(Message message) throws JMSException {
        System.out.println("> dumpMessageHeaders");
        Enumeration<?> propertyNames = message.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            String name = (String) propertyNames.nextElement();
            Object value = message.getObjectProperty(name);
            System.out.println(name + "=" + value);
        }
        System.out.println("< dumpMessageHeaders");
    }

    @Before
    public void setUp() throws Exception {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false");

        this.connection = connectionFactory.createConnection();
        this.connection.start();
    }

    @After
    public void tearDown() throws Exception {
        this.connection.close();
    }

    @Test
    public void testBrowse() throws Exception {
        Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("default");

        QueueBrowser queueBrowser = session.createBrowser(queue);

        Enumeration<?> enumeration = queueBrowser.getEnumeration();
        while (enumeration.hasMoreElements()) {
            Message message = (Message) enumeration.nextElement();
            String msg;
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                msg = textMessage.toString();
            } else {
                msg = message.toString();
            }
            System.out.println("browse " + msg);
        }

    }

    @Test
    public void testDumpConnectionMetadata() throws JMSException {
        ConnectionMetaData connectionMetaData = this.connection.getMetaData();

        System.out.println("> connectionMetaData");
        System.out.println("properties");
        Enumeration<String> jmsxPropertyNames = connectionMetaData.getJMSXPropertyNames();
        while (jmsxPropertyNames.hasMoreElements()) {
            String propertyName = jmsxPropertyNames.nextElement();
            System.out.println(propertyName);
        }
        System.out.println("< connectionMetaData");
    }

    @Test
    public void testReceive() throws Exception {
        Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("default");

        MessageConsumer messageConsumer = session.createConsumer(queue, null);

        Message message;
        while ((message = messageConsumer.receive(1000)) != null) {
            System.out.println("receive " + message);
        }

    }

    @Test
    public void testSendAndReceive() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        MessageConsumer messageConsumer;
        {
            Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("default");

            messageConsumer = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE).createConsumer(queue, null);
            MessageListener sysoutListener = new MessageListener() {

                public void onMessage(Message message) {
                    System.out.println(Thread.currentThread()+ " testSendAndReceive.receive");
                    System.out.println(message);
                    countDownLatch.countDown();
                }

            };
            messageConsumer.setMessageListener(sysoutListener);
        }

        {
            Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("default");
            MessageProducer messageProducer = session.createProducer(queue);

            TextMessage textMessage = session.createTextMessage("Hello world with headers");
            textMessage.setJMSMessageID("my-message-id-1");
            textMessage.setJMSCorrelationID("my-correlation-id-1");
            textMessage.setJMSType("my-type");
            textMessage.setStringProperty("mystringproperty", "my-value");

            messageProducer.send(textMessage);

        }
        Assert.assertTrue("Timeout before message was consumed", countDownLatch.await(2, TimeUnit.SECONDS));
        messageConsumer.close();
    }

    @Test
    public void testSendInTemporaryQueue() throws Exception {
        Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = session.createTemporaryQueue();
        MessageProducer messageProducer = session.createProducer(queue);

        try {
            TextMessage textMessage = session.createTextMessage("Hello temporary queue");

            messageProducer.send(textMessage);
            dumpMessageHeaders(textMessage);
        } catch (JMSException e) {
            e.printStackTrace();
            if (e.getLinkedException() != null) {
                e.getLinkedException().printStackTrace();
            }
            throw e;
        }

        queue.delete();
        session.close();
    }

    @Test
    public void testSendTextMessage() throws Exception {
        Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("default");

        MessageProducer messageProducer = session.createProducer(queue);

        TextMessage textMessage = session.createTextMessage("Hello world JMS Message");

        messageProducer.send(textMessage);

        dumpMessageHeaders(textMessage);

        session.close();

    }

    @Test
    public void testSendTextMessageWithProperties() throws Exception {
        Session session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("default");
        MessageProducer messageProducer = session.createProducer(queue);

        TextMessage textMessage = session.createTextMessage("Hello world with headers");
        textMessage.setJMSMessageID("my-message-id-1");
        textMessage.setJMSCorrelationID("my-correlation-id-1");
        textMessage.setJMSType("my-type");
        textMessage.setStringProperty("mystringproperty", "my-value");

        messageProducer.send(textMessage);

        dumpMessageHeaders(textMessage);

        session.close();
    }
}
