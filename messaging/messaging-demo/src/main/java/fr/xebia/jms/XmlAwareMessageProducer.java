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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

/**
 * {@link MessageProducerWrapper} with support for {@link XmlMessage}.
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public abstract class XmlAwareMessageProducer extends MessageProducerWrapper {

    protected MessageProducer messageProducer;

    protected JmsCharsetHelper jmsCharsetHelper;

    public XmlAwareMessageProducer(MessageProducer messageProducer, JmsCharsetHelper jmsCharsetHelper) {
        super(messageProducer);
        this.messageProducer = messageProducer;
        this.jmsCharsetHelper = jmsCharsetHelper;
    }

    /**
     * Prepare the given <code>message</code>
     */
    protected Message prepareMessage(Destination destination, XmlMessageImpl xmlMessage) throws JMSException {
        try {
            String jmsMessageCharacterSet = this.jmsCharsetHelper.getMessageCharset(xmlMessage);
            String xmlCharacterSet = xmlMessage.getOutputProperty(OutputKeys.ENCODING);

            if (jmsMessageCharacterSet == null && xmlCharacterSet == null) {
                /*
                 * Charset not defined in JMS header nor than in XML output property, use queue
                 * charset for XML serialization (JMS Header will be set by MQSender)
                 */
                String charsetName = this.jmsCharsetHelper.getDestinationCharset(destination);
                if (charsetName == null || charsetName.trim().length() == 0) {
                    // destination charset is unknown, force to UTF-8
                    charsetName = "UTF-8";
                }
                xmlMessage.setOutputProperty(OutputKeys.ENCODING, charsetName);
                this.jmsCharsetHelper.setMessageCharset(xmlMessage, charsetName);

            } else if (jmsMessageCharacterSet != null && xmlCharacterSet == null) {
                /*
                 * Charset not defined in XML output property, use JMS header encoding for XML
                 * serialization
                 */
                xmlMessage.setOutputProperty(OutputKeys.ENCODING, jmsMessageCharacterSet);

            } else if (xmlCharacterSet != null && jmsMessageCharacterSet == null) {
                /*
                 * Charset not defined in JMS header, use XML output property for JMS encoding
                 * header
                 */
                this.jmsCharsetHelper.setMessageCharset(xmlMessage, xmlCharacterSet);

            } else {
                if (jmsMessageCharacterSet.equalsIgnoreCase(xmlCharacterSet)) {
                    /*
                     * Everything is ok : JMS Header and XML Serialization charsets are in sync
                     */
                } else {
                    throw new JMSException("Mismatch between JMS Message characterSet (" + jmsMessageCharacterSet
                            + ") and XML output property (" + OutputKeys.ENCODING + "=" + xmlCharacterSet + ")");
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperties(xmlMessage.getOutputProperties());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            transformer.transform(xmlMessage.getSource(), new StreamResult(outputStream));

            TextMessage textMessage = (xmlMessage)._getTextMessage();

            String xmlMessageAsString = outputStream.toString(xmlMessage.getOutputProperty(OutputKeys.ENCODING));
            textMessage.setText(xmlMessageAsString);

            return textMessage;

        } catch (TransformerException e) {
            JMSException jmsException = new JMSException("Exception transforming XML message: " + e.toString());
            jmsException.setLinkedException(e);
            throw jmsException;
        } catch (UnsupportedEncodingException e) {
            JMSException jmsException = new JMSException("Exception encoding XML stream: " + e.toString());
            jmsException.setLinkedException(e);
            throw jmsException;
        }
    }

    @Override
    public void send(Destination destination, Message message) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(destination, (XmlMessageImpl) message);
        }
        this.messageProducer.send(destination, message);
    }

    @Override
    public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(destination, (XmlMessageImpl) message);
        }
        this.messageProducer.send(destination, message, deliveryMode, priority, timeToLive);
    }

    @Override
    public void send(Message message) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(getDestination(), (XmlMessageImpl) message);
        }
        this.messageProducer.send(message);
    }

    @Override
    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(getDestination(), (XmlMessageImpl) message);
        }
        this.messageProducer.send(message, deliveryMode, priority, timeToLive);
    }
}
