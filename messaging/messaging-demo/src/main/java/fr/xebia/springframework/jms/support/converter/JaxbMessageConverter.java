/*
 * Copyright 2002-2005 the original author or authors.
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
package fr.xebia.springframework.jms.support.converter;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.util.Assert;

/**
 * <p>
 * JAXB based message converter.
 * </p>
 * 
 * <p>
 * Marshalling : converts the given object into a {@link JAXBSource} and serialize this XML source into a string that is associated to a
 * {@link TextMessage}.
 * </p>
 * <p>
 * Unmarshalling : converts the given {@link TextMessage} into a {@link StreamSource} and instantiate the associated object via an XSL
 * identity transformation toward a {@link JAXBResult}
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public abstract class JaxbMessageConverter implements MessageConverter, InitializingBean {

    protected JAXBContext jaxbContext;

    protected TransformerFactory transformerFactory;

    protected String encoding;

    /**
     * Zero args constructor for setter based dependency injection.
     */
    public JaxbMessageConverter() {
        super();
    }

    /**
     * Constructor with params for constructor based dependency injection.
     * 
     * @param jaxbContext
     *            to marshal given objects into xml text and unmarshal given text messages into objects
     * @param encoding
     *            used to marshal object into XML (e.g. "UTF-8", "ISO-8859-1" ...)
     * 
     * @throws UnsupportedCharsetException
     *             if the given encoding is not supported by the JVM
     */
    public JaxbMessageConverter(JAXBContext jaxbContext, String encoding) throws UnsupportedCharsetException {
        this();
        this.jaxbContext = jaxbContext;
        this.encoding = encoding;
    }

    public void afterPropertiesSet() throws Exception {
        this.transformerFactory = TransformerFactory.newInstance();
        // Call Charset.forName() to raise an UnsupportedCharsetException if encoding is not supported
        Charset.forName(this.encoding);
    }

    /**
     * <p>
     * Unmarshal given <code>message</code> into an <code>object</code>.
     * </p>
     * 
     * <p>
     * Should we raise an exception if the XML message encoding is not in sync with the underlying TextMessage encoding when the JMS
     * Provider supports MOM message's encoding.
     * </p>
     * 
     * @param message
     *            to unmarshal, MUST be an instance of {@link TextMessage}
     * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
     */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            Assert.isInstanceOf(TextMessage.class, message);
            TextMessage textMessage = (TextMessage) message;

            // prepare JAXB unmarshalling based on XSL identity transformation
            StreamSource stringSource = new StreamSource(new StringReader(textMessage.getText()));
            JAXBResult jaxbResult = new JAXBResult(this.jaxbContext);
            Transformer transformer = this.transformerFactory.newTransformer();

            // JAXB unmarshalling
            transformer.transform(stringSource, jaxbResult);

            Object result = jaxbResult.getResult();
            return result;
        } catch (Exception e) {
            throw new MessageConversionException("Exception unmarshalling message: " + message, e);
        }
    }

    /**
     * Encoding used for marshalling.
     * 
     * @param encoding
     *            used to marshal object into XML (e.g. "UTF-8", "ISO-8859-1" ...)
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * JAXB context used for marshalling and unmarshalling.
     * 
     * @param jaxbContext
     *            to marshal given objects into xml text and unmarshal given text messages into objects
     */
    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    /**
     * <p>
     * Intended to be overwritten for each JMS Provider implementation (Websphere MQ, Tibco EMS, Active MQ ...).
     * </p>
     * <p>
     * If JMS provider supports messages encoding, this charset must be in sync with the encoding used to generate the XML text output
     * </p>
     */
    protected abstract void setMessageCharset(TextMessage textMessage, String charset) throws JMSException;

    /**
     * <p>
     * Marshal the given <code>object</code> into a text message.
     * </p>
     * 
     * <p>
     * This method ensures that the message encoding supported by the underlying JMS provider is in sync with the encoding used to generate
     * the XML message.
     * </p>
     * 
     * @param object
     *            to marshal. MUST be supported by the jaxb context used by this converter (see {@link #setJaxbContext(JAXBContext)})
     * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object, javax.jms.Session)
     */
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        try {
            // prepare the JAXB marshalling via identity transformation toward a StreamResult
            Source source = new JAXBSource(this.jaxbContext, object);
            Transformer transformer = this.transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, this.encoding);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // JAXB Marshalling
            transformer.transform(source, new StreamResult(out));

            // create TextMessage result
            String text = out.toString(this.encoding);
            TextMessage textMessage = session.createTextMessage(text);

            // Force encoding of the JMS Provider's Message implementation
            setMessageCharset(textMessage, this.encoding);

            return textMessage;
        } catch (Exception e) {
            throw new MessageConversionException("Exception converting", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("jaxbContext", this.jaxbContext).append("encoding", this.encoding).toString();
    }
}
