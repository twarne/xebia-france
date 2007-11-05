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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * {@link Message} wrapper.
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class MessageWrapper implements Message {

    protected Message message;

    public MessageWrapper(Message message) {
        this.message = message;
    }

    public void acknowledge() throws JMSException {
        this.message.acknowledge();
    }

    public void clearBody() throws JMSException {
        this.message.clearBody();
    }

    public void clearProperties() throws JMSException {
        this.message.clearProperties();
    }

    public boolean getBooleanProperty(String name) throws JMSException {
        return this.message.getBooleanProperty(name);
    }

    public byte getByteProperty(String name) throws JMSException {
        return this.message.getByteProperty(name);
    }

    public double getDoubleProperty(String name) throws JMSException {
        return this.message.getDoubleProperty(name);
    }

    public float getFloatProperty(String name) throws JMSException {
        return this.message.getFloatProperty(name);
    }

    public int getIntProperty(String name) throws JMSException {
        return this.message.getIntProperty(name);
    }

    public String getJMSCorrelationID() throws JMSException {
        return this.message.getJMSCorrelationID();
    }

    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return this.message.getJMSCorrelationIDAsBytes();
    }

    public int getJMSDeliveryMode() throws JMSException {
        return this.message.getJMSDeliveryMode();
    }

    public Destination getJMSDestination() throws JMSException {
        return this.message.getJMSDestination();
    }

    public long getJMSExpiration() throws JMSException {
        return this.message.getJMSExpiration();
    }

    public String getJMSMessageID() throws JMSException {
        return this.message.getJMSMessageID();
    }

    public int getJMSPriority() throws JMSException {
        return this.message.getJMSPriority();
    }

    public boolean getJMSRedelivered() throws JMSException {
        return this.message.getJMSRedelivered();
    }

    public Destination getJMSReplyTo() throws JMSException {
        return this.message.getJMSReplyTo();
    }

    public long getJMSTimestamp() throws JMSException {
        return this.message.getJMSTimestamp();
    }

    public String getJMSType() throws JMSException {
        return this.message.getJMSType();
    }

    public long getLongProperty(String name) throws JMSException {
        return this.message.getLongProperty(name);
    }

    public Object getObjectProperty(String name) throws JMSException {
        return this.message.getObjectProperty(name);
    }

    public Enumeration<?> getPropertyNames() throws JMSException {
        return this.message.getPropertyNames();
    }

    public short getShortProperty(String name) throws JMSException {
        return this.message.getShortProperty(name);
    }

    public String getStringProperty(String name) throws JMSException {
        return this.message.getStringProperty(name);
    }

    public boolean propertyExists(String name) throws JMSException {
        return this.message.propertyExists(name);
    }

    public void setBooleanProperty(String name, boolean value) throws JMSException {
        this.message.setBooleanProperty(name, value);
    }

    public void setByteProperty(String name, byte value) throws JMSException {
        this.message.setByteProperty(name, value);
    }

    public void setDoubleProperty(String name, double value) throws JMSException {
        this.message.setDoubleProperty(name, value);
    }

    public void setFloatProperty(String name, float value) throws JMSException {
        this.message.setFloatProperty(name, value);
    }

    public void setIntProperty(String name, int value) throws JMSException {
        this.message.setIntProperty(name, value);
    }

    public void setJMSCorrelationID(String correlationID) throws JMSException {
        this.message.setJMSCorrelationID(correlationID);
    }

    public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {
        this.message.setJMSCorrelationIDAsBytes(correlationID);
    }

    public void setJMSDeliveryMode(int deliveryMode) throws JMSException {
        this.message.setJMSDeliveryMode(deliveryMode);
    }

    public void setJMSDestination(Destination destination) throws JMSException {
        this.message.setJMSDestination(destination);
    }

    public void setJMSExpiration(long expiration) throws JMSException {
        this.message.setJMSExpiration(expiration);
    }

    public void setJMSMessageID(String id) throws JMSException {
        this.message.setJMSMessageID(id);
    }

    public void setJMSPriority(int priority) throws JMSException {
        this.message.setJMSPriority(priority);
    }

    public void setJMSRedelivered(boolean redelivered) throws JMSException {
        this.message.setJMSRedelivered(redelivered);
    }

    public void setJMSReplyTo(Destination replyTo) throws JMSException {
        this.message.setJMSReplyTo(replyTo);
    }

    public void setJMSTimestamp(long timestamp) throws JMSException {
        this.message.setJMSTimestamp(timestamp);
    }

    public void setJMSType(String type) throws JMSException {
        this.message.setJMSType(type);
    }

    public void setLongProperty(String name, long value) throws JMSException {
        this.message.setLongProperty(name, value);
    }

    public void setObjectProperty(String name, Object value) throws JMSException {
        this.message.setObjectProperty(name, value);
    }

    public void setShortProperty(String name, short value) throws JMSException {
        this.message.setShortProperty(name, value);
    }

    public void setStringProperty(String name, String value) throws JMSException {
        this.message.setStringProperty(name, value);
    }

}
