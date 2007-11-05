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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * {@link MessageProducer} wrapper.
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class MessageProducerWrapper implements MessageProducer {

    protected MessageProducer messageProducer;

    public MessageProducerWrapper(MessageProducer messageProducer) {
        super();
        this.messageProducer = messageProducer;
    }

    public void close() throws JMSException {
        this.messageProducer.close();
    }

    public int getDeliveryMode() throws JMSException {
        return this.messageProducer.getDeliveryMode();
    }

    public Destination getDestination() throws JMSException {
        return this.messageProducer.getDestination();
    }

    public boolean getDisableMessageID() throws JMSException {
        return this.messageProducer.getDisableMessageID();
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return this.messageProducer.getDisableMessageTimestamp();
    }

    public int getPriority() throws JMSException {
        return this.messageProducer.getPriority();
    }

    public long getTimeToLive() throws JMSException {
        return this.messageProducer.getTimeToLive();
    }

    public void send(Destination destination, Message message) throws JMSException {
        this.messageProducer.send(destination, message);
    }

    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.messageProducer.send(destination, message, deliveryMode, priority, timeToLive);
    }

    public void send(Message message) throws JMSException {
        this.messageProducer.send(message);
    }

    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        this.messageProducer.send(message, deliveryMode, priority, timeToLive);
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException {
        this.messageProducer.setDeliveryMode(deliveryMode);
    }

    public void setDisableMessageID(boolean value) throws JMSException {
        this.messageProducer.setDisableMessageID(value);
    }

    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        this.messageProducer.setDisableMessageTimestamp(value);
    }

    public void setPriority(int defaultPriority) throws JMSException {
        this.messageProducer.setPriority(defaultPriority);
    }

    public void setTimeToLive(long timeToLive) throws JMSException {
        this.messageProducer.setTimeToLive(timeToLive);
    }

    @Override
    public String toString() {
        return this.messageProducer.toString();
    }
}
