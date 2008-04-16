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

import java.util.concurrent.CountDownLatch;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class JmsTest {

    @Test
    public void receiveMessageToWarehouse() throws Exception {
        {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "vm://localhost?broker.persistent=false&broker.useJmx=false");
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination warehouseDestination = session.createQueue("warehouse");
            MessageConsumer messageConsumer = session.createConsumer(warehouseDestination);
            MessageListener messageListener = new MessageListener() {

                public void onMessage(Message message) {
                    System.out.println(Thread.currentThread()+ " warehouse received message " + message);

                }
            };
            messageConsumer.setMessageListener(messageListener);
        }
        {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "vm://localhost?broker.persistent=false&broker.useJmx=false");

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination warehouseDestination = session.createQueue("warehouse");
            Message message = session.createTextMessage("New Order To Ship ...");
            MessageProducer producer = session.createProducer(warehouseDestination);
            producer.send(message);

        }
        Thread.sleep(2000);
    }

}
