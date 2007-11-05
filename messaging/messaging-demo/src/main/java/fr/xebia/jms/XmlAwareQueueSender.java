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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;

/**
 * <p>
 * {@link QueueSender} with support for {@link XmlMessage}.
 * </p>
 * <p>
 * If the {@link Message} is an instance of {@link XmlMessage}, maintains the CCSID in sync with the "encoding" attribute of the XML
 * document.
 * </p>
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class XmlAwareQueueSender extends XmlAwareMessageProducer implements QueueSender {

    protected QueueSender queueSender;

    public XmlAwareQueueSender(QueueSender queueSender, JmsCharsetHelper jmsCharsetHelper) {
        super(queueSender, jmsCharsetHelper);
        this.queueSender = queueSender;
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(getQueue(), (XmlMessageImpl) message);
        }
        this.queueSender.send(message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void send(Message message) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(getQueue(), (XmlMessageImpl) message);
        }
        this.queueSender.send(message);
    }

    public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(queue, (XmlMessageImpl) message);
        }
        this.queueSender.send(queue, message, deliveryMode, priority, timeToLive);
    }

    public void send(Queue queue, Message message) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(queue, (XmlMessageImpl) message);
        }
        this.queueSender.send(queue, message);
    }

    public Queue getQueue() throws JMSException {
        return this.queueSender.getQueue();
    }
}
