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

import java.nio.charset.UnsupportedCharsetException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Jaxb Message Converter implementation for IBM Websphere MQ. Should also work for IBM Websphere
 * SIBus.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JaxbMessageConverterMqImpl extends JaxbMessageConverter {

    /**
     * Value of <code>com.ibm.mq.jms.JMSC#CHARSET_PROPERTY</code> used to set Websphere MQ message
     * charset. Value recopied from JMSC API to prevent needing mq jar to compile
     * 
     * @see com.ibm.mq.jms.JMSC#CHARSET_PROPERTY
     */
    public static final String JMSC_CHARSET_PROPERTY = "JMS_IBM_Character_Set";

    /**
     * Set given <code>message</code> encoding with Websphere MQ proprietary APIs.
     * 
     * @throws UnsupportedCharsetException
     *             No matching IBM CCSID found for given <code>charset</code>
     * @see com.ibm.mq.jms.JMSC#CHARSET_PROPERTY
     * @see fr.xebia.springframework.jms.support.converter.JaxbMessageConverter#setMessageCharset(javax.jms.TextMessage,
     *      java.lang.String)
     */
    @Override
    protected void setMessageCharset(TextMessage textMessage, String charset) throws JMSException, UnsupportedCharsetException {
        String ccsid = String.valueOf(IbmCharsetUtils.getIbmCharacterSetId(charset));
        textMessage.setStringProperty(JMSC_CHARSET_PROPERTY, ccsid);
    }
}
