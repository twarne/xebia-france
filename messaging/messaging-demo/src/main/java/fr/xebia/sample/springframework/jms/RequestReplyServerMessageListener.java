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
package fr.xebia.sample.springframework.jms;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;

/**
 * <p>
 * SpringFramework {@link SessionAwareMessageListener} based Request/Reply Server Listener.
 * </p>
 * <p>
 * Unfortunately, {@link SessionAwareMessageListener} does not trap the "onCreateSession(Session)"
 * to cache the {@link MessageProducer} used to reply to the incoming requests. Creating a new
 * {@link MessageProducer} at each may have a performances impact even if the MessageProducer is not
 * attached to a destination (<code>session.createProducer(null)</code>).
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class RequestReplyServerMessageListener implements SessionAwareMessageListener {

    private final static Logger logger = Logger.getLogger(RequestReplyServerMessageListener.class);

    protected int receivedMessagesCounter;

    public void onMessage(Message message, Session session) throws JMSException {

        logger.debug("> SampleListener.onMessage");

        if (message.getJMSReplyTo() == null) {
            logger.debug("Request/NOReply message: " + message);

        } else {
            Message replyMessage = session.createTextMessage("Reply at " + new Date() + "to \r\n" + message);
            replyMessage.setJMSCorrelationID(message.getJMSMessageID());
            // recreating a messageProducer each time may be a performances bottleneck (seems pretty
            // cheap with
            // ActiveMQ)
            MessageProducer messageProducer = session.createProducer(null);
            messageProducer.send(message.getJMSReplyTo(), replyMessage);

            logger.debug("Request/Reply message: " + message + "\r\n" + replyMessage);
            message.acknowledge();

        }
        this.receivedMessagesCounter++;

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        logger.debug("< SampleListener.onMessage");

    }

    public int getReceivedMessagesCounter() {
        return this.receivedMessagesCounter;
    }

}
