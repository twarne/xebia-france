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
package fr.xebia.springframework.jms.support.converter;

import java.lang.reflect.Method;
import java.nio.charset.UnsupportedCharsetException;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.jms.support.converter.MessageConversionException;

/**
 * <p>
 * {@link JaxbMessageConverter} implementation for Tibco Enterprise Messaging Service (aka Tibco EMS
 * or Tibco JMS).
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JaxbMessageConverterTibjmsImpl extends JaxbMessageConverter {

    private static final String TIBJMS_SET_MESSAGE_ENCODING_METHOD = "setMessageEncoding";

    private static final String TIBJMS_CLASS = "com.tibco.tibjms.Tibjms";

    protected Method setMessageEncodingMethod;

    /**
     * Zero args constructor for setter based dependency injection.
     * 
     * @throws UnsupportedCharsetException
     *             if the given encoding is not supported by the JVM
     */
    public JaxbMessageConverterTibjmsImpl() {
        super();
        init();
    }

    /**
     * Constructor with params for constructor based dependency injection.
     * 
     * @param jaxbContext
     *            to marshal given objects into xml text and unmarshal given text messages into
     *            objects
     * @param encoding
     *            used to marshal object into XML (e.g. "UTF-8", "ISO-8859-1" ...)
     * 
     * @throws UnsupportedCharsetException
     *             if the given encoding is not supported by the JVM
     */
    public JaxbMessageConverterTibjmsImpl(JAXBContext jaxbContext, String encoding) throws UnsupportedCharsetException {
        super(jaxbContext, encoding);
        init();
    }

    protected void init() {
        try {
            Class<?> tibJmsClass = Class.forName(TIBJMS_CLASS);

            this.setMessageEncodingMethod = tibJmsClass.getMethod(TIBJMS_SET_MESSAGE_ENCODING_METHOD,
                    new Class[] { Message.class, String.class });
        } catch (Exception e) {
            throw new MessageConversionException(NestedExceptionUtils.buildMessage("Exception loading Tibjms class", e), e);
        }
    }

    /**
     * Set given <code>message</code> encoding with Tibco proprietary APIs.
     * 
     * @see com.tibco.tibjms.Tibjms#setMessageEncoding(javax.jms.Message, String)
     * @see fr.xebia.springframework.jms.support.converter.JaxbMessageConverter#setMessageCharset(javax.jms.TextMessage,
     *      java.lang.String)
     */
    @Override
    protected void setMessageCharset(TextMessage textMessage, String charset) {
        try {
            this.setMessageEncodingMethod.invoke(null, new Object[] { textMessage, charset });
        } catch (Exception e) {
            MessageConversionException jmse = new MessageConversionException(NestedExceptionUtils.buildMessage(
                    "Exception setting message encoding", e), e);
            throw jmse;
        }
    }
}
