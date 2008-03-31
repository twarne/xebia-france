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

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <p>
 * JAXB based message converter.
 * </p>
 * 
 * <p>
 * Marshalling : converts the given object into a {@link TextMessage} thanks to {@link Marshaller#marshal(Object, java.io.OutputStream)} .
 * </p>
 * <p>
 * Unmarshalling : converts the given {@link TextMessage} or {@link BytesMessage} body into an object thanks to
 * {@link Unmarshaller#unmarshal(javax.xml.transform.Source)}.
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public abstract class JaxbMessageConverter implements MessageConverter, InitializingBean {

    /**
     * According to <a href="https://jaxb.dev.java.net/faq/index.html#threadSafety">JAXB FAQ : Q. Are the JAXB runtime API's thread safe?</a>,
     * {@link JAXBContext} is thread safe but {@link Marshaller} and {@link Unmarshaller} are not.
     */
    protected JAXBContext jaxbContext;

    protected Map<String, ?> jaxbContextProperties;

    protected String jaxbContextPath;

    /**
     * @see Marshaller#JAXB_ENCODING
     */
    protected String encoding = "UTF-8";

    /**
     * @see Marshaller#JAXB_FORMATTED_OUTPUT
     */
    protected Boolean formattedOutput;

    /**
     * Call {@link Charset#forName()} to raise an {@link UnsupportedCharsetException} if {@link #encoding} is not supported.
     */
    public void afterPropertiesSet() throws Exception {
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
     *            message to unmarshal, MUST be an instance of {@link TextMessage} or of {@link BytesMessage}
     * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
     * @see Unmarshaller#unmarshal(java.io.Reader)
     */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            Object result;
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                result = unmarshaller.unmarshal(new StringReader(textMessage.getText()));

            } else if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(bytes);
                result = unmarshaller.unmarshal(new ByteArrayInputStream(bytes));

            } else {
                throw new MessageConversionException("Unsupported JMS Message type " + message.getClass()
                        + ", expected instance of TextMessage or BytesMessage for " + message);
            }

            return result;
        } catch (JAXBException e) {
            throw new MessageConversionException("Exception unmarshalling message: " + message, e);
        }
    }

    /**
     * Encoding used for marshalling (ie {@link MessageConverter#toMessage(Object, Session)}).
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
     *            object to marshal, MUST be supported by the jaxb context used by this converter (see {@link #setJaxbContext(JAXBContext)})
     * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object, javax.jms.Session)
     */
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        try {
            // JAXB Marshalling
            Marshaller marshaller = this.jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, this.encoding);
            if (this.formattedOutput != null) {
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, this.formattedOutput);
            }
            StringWriter out = new StringWriter();
            marshaller.marshal(object, out);

            // create TextMessage result
            String text = out.toString();
            TextMessage textMessage = session.createTextMessage(text);

            // Force encoding of the JMS Provider's Message implementation
            setMessageCharset(textMessage, this.encoding);

            return textMessage;
        } catch (JAXBException e) {
            throw new MessageConversionException("Exception converting", e);
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("jaxbContext", this.jaxbContext).append("encoding", this.encoding).toString();
    }

    public void setJaxbContextProperties(Map<String, ?> jaxbContextProperties) {
        this.jaxbContextProperties = jaxbContextProperties;
    }

    /**
     * @param jaxbContextPath
     *            list of java package names that contain schema derived class and/or java to schema (JAXB-annotated) mapped classes.
     */
    public void setJaxbContextPath(String jaxbContextPath) {
        Assert.notNull(jaxbContextPath, "'jaxbContextPath' must not be null");
        this.jaxbContextPath = jaxbContextPath;
    }
    /**
     * @param jaxbContextPath
     *            list of java package names that contain schema derived class and/or java to schema (JAXB-annotated) mapped classes.
     */
    public void setJaxbContextPaths(String[] jaxbContextPaths) {
        Assert.notEmpty(jaxbContextPaths, "'jaxbContextPaths' must not be empty");
        this.jaxbContextPath = StringUtils.arrayToDelimitedString(jaxbContextPaths, ":");
    }

    public void setFormattedOutput(Boolean formattedOutput) {
        this.formattedOutput = formattedOutput;
    }
}
