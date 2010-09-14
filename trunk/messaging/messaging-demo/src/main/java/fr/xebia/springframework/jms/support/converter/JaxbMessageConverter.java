/*
 * Copyright 2002-2008 Xebia and the original author or authors.
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
import java.io.StringWriter;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringSource;

/**
 * <p>
 * JAXB2 based <code>MessageConverter</code>.
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
public class JaxbMessageConverter implements MessageConverter, InitializingBean {

    protected Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();

    /**
     * Keep a reference on <code>Jaxb2Marshaller.marshallerProperties</code> to be able to retreive the {@link Marshaller#JAXB_ENCODING}
     * if it has been set.
     */
    protected Map<String, ?> marshallerProperties;

    /**
     * <p>
     * Unmarshal given <code>message</code> into an <code>object</code>.
     * </p>
     * 
     * <p>
     * Should we raise an exception if the XML message encoding is not in sync with the underlying TextMessage encoding when the JMS
     * Provider supports MOM message's encoding ?
     * </p>
     * 
     * @param message
     *            message to unmarshal, MUST be an instance of {@link TextMessage} or of {@link BytesMessage}.
     * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
     * @see org.springframework.oxm.Unmarshaller#unmarshal(Source)
     */
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {

        Source source;
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            source = new StringSource(textMessage.getText());

        } else if (message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;
            byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(bytes);
            source = new StreamSource(new ByteArrayInputStream(bytes));

        } else {
            throw new MessageConversionException("Unsupported JMS Message type " + message.getClass()
                    + ", expected instance of TextMessage or BytesMessage for " + message);
        }
        Object result = jaxb2Marshaller.unmarshal(source);

        return result;
    }

    /**
     * Sets the <code>XmlAdapter</code>s to be registered with the JAXB <code>Marshaller</code> and <code>Unmarshaller</code>.
     * 
     * @see Jaxb2Marshaller#setAdapters(XmlAdapter[])
     */
    public void setAdapters(XmlAdapter<?, ?>[] adapters) {
        jaxb2Marshaller.setAdapters(adapters);
    }

    /**
     * Sets the list of java classes to be recognized by a newly created JAXBContext. Setting this property or <code>contextPath</code> is
     * required.
     * 
     * @see Jaxb2Marshaller#setContextPath(String)
     * @see Jaxb2Marshaller#setClassesToBeBound(Class[])
     */
    public void setClassesToBeBound(Class<?>[] classesToBeBound) {
        jaxb2Marshaller.setClassesToBeBound(classesToBeBound);
    }

    /**
     * Sets the JAXB Context path.
     * 
     * @see AbstractJaxbMarshaller#setContextPath(String)
     */
    public void setContextPath(String contextPath) {
        jaxb2Marshaller.setContextPath(contextPath);
    }

    /**
     * Sets multiple JAXB Context paths. The given array of context paths is converted to a colon-delimited string, as supported by JAXB.
     * 
     * @see AbstractJaxbMarshaller#setContextPaths(String[])
     */
    public void setContextPaths(String[] contextPaths) {
        jaxb2Marshaller.setContextPaths(contextPaths);
    }

    /**
     * Sets the <code>JAXBContext</code> properties. These implementation-specific properties will be set on the <code>JAXBContext</code>.
     * 
     * @see Jaxb2Marshaller#setJaxbContextProperties(Map)
     */
    public void setJaxbContextProperties(Map<String, ?> jaxbContextProperties) {
        jaxb2Marshaller.setJaxbContextProperties(jaxbContextProperties);
    }

    /**
     * Sets the <code>Marshaller.Listener</code> to be registered with the JAXB <code>Marshaller</code>.
     * 
     * @see Jaxb2Marshaller#setMarshallerListener(Listener)
     */
    public void setMarshallerListener(Listener marshallerListener) {
        jaxb2Marshaller.setMarshallerListener(marshallerListener);
    }

    /**
     * Sets the JAXB <code>Marshaller</code> properties. These properties will be set on the underlying JAXB <code>Marshaller</code>,
     * and allow for features such as indentation.
     * 
     * @param properties
     *            the properties
     * @see javax.xml.bind.Marshaller#setProperty(String,Object)
     * @see javax.xml.bind.Marshaller#JAXB_ENCODING
     * @see javax.xml.bind.Marshaller#JAXB_FORMATTED_OUTPUT
     * @see javax.xml.bind.Marshaller#JAXB_NO_NAMESPACE_SCHEMA_LOCATION
     * @see javax.xml.bind.Marshaller#JAXB_SCHEMA_LOCATION
     * @see AbstractJaxbMarshaller#setMarshallerProperties(Map)
     */
    public void setMarshallerProperties(Map<String, Object> properties) {
        jaxb2Marshaller.setMarshallerProperties(properties);
        this.marshallerProperties = properties;
    }

    /**
     * <p>
     * Intended to be overwritten for each JMS Provider implementation (Websphere MQ, Tibco EMS, Active MQ ...).
     * </p>
     * <p>
     * If JMS provider supports messages encoding, this charset must be in sync with the encoding used to generate the XML text output
     * </p>
     */
    protected void postProcessResponseMessage(Message textMessage) throws JMSException {

    }

    /**
     * Indicates whether MTOM support should be enabled or not. Default is <code>false</code>, marshalling using XOP/MTOM is not enabled.
     * 
     * @see Jaxb2Marshaller#setMtomEnabled(boolean)
     */
    public void setMtomEnabled(boolean mtomEnabled) {
        jaxb2Marshaller.setMtomEnabled(mtomEnabled);
    }

    /**
     * Sets the schema resource to use for validation.
     * 
     * @see Jaxb2Marshaller#setSchema(Resource)
     */
    public void setSchema(Resource schemaResource) {
        jaxb2Marshaller.setSchema(schemaResource);
    }

    /**
     * Sets the schema language. Default is the W3C XML Schema: <code>http://www.w3.org/2001/XMLSchema"</code>.
     * 
     * @see XMLConstants#W3C_XML_SCHEMA_NS_URI
     * @see XMLConstants#RELAXNG_NS_URI
     * @see Jaxb2Marshaller#setSchemaLanguage(String)
     */
    public void setSchemaLanguage(String schemaLanguage) {
        jaxb2Marshaller.setSchemaLanguage(schemaLanguage);
    }

    /**
     * Sets the schema resources to use for validation.
     * 
     * @see Jaxb2Marshaller#setSchemas(Resource[])
     */
    public void setSchemas(Resource[] schemaResources) {
        jaxb2Marshaller.setSchemas(schemaResources);
    }

    /**
     * Sets the <code>Unmarshaller.Listener</code> to be registered with the JAXB <code>Unmarshaller</code>.
     * 
     * @see Jaxb2Marshaller#setUnmarshallerListener(javax.xml.bind.Unmarshaller.Listener)
     */
    public void setUnmarshallerListener(javax.xml.bind.Unmarshaller.Listener unmarshallerListener) {
        jaxb2Marshaller.setUnmarshallerListener(unmarshallerListener);
    }

    /**
     * Sets the JAXB <code>Unmarshaller</code> properties. These properties will be set on the underlying JAXB <code>Unmarshaller</code>.
     * 
     * @param properties
     *            the properties
     * @see javax.xml.bind.Unmarshaller#setProperty(String,Object)
     * @see AbstractJaxbMarshaller#setUnmarshallerProperties(Map)
     */
    public void setUnmarshallerProperties(Map<String, Object> properties) {
        jaxb2Marshaller.setUnmarshallerProperties(properties);
    }

    /**
     * Sets the JAXB validation event handler. This event handler will be called by JAXB if any validation errors are encountered during
     * calls to any of the marshal API's.
     * 
     * @param validationEventHandler
     *            the event handler
     * @see AbstractJaxbMarshaller#setValidationEventHandler(ValidationEventHandler)
     */
    public void setValidationEventHandler(ValidationEventHandler validationEventHandler) {
        jaxb2Marshaller.setValidationEventHandler(validationEventHandler);
    }

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
     *            Object to marshal, MUST be supported by the jaxb context used by this converter (see {@link #setJaxbContext(JAXBContext)}).
     * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object, javax.jms.Session)
     */
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
        StringWriter out = new StringWriter();
        jaxb2Marshaller.marshal(object, new StreamResult(out));

        // create TextMessage result
        String text = out.toString();
        TextMessage textMessage = session.createTextMessage(text);

        postProcessResponseMessage(textMessage);

        return textMessage;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("jaxb2Marshaller", this.jaxb2Marshaller).toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.jaxb2Marshaller.afterPropertiesSet();
    }
}
