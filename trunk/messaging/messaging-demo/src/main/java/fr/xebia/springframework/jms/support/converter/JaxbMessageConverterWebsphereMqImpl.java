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

import java.nio.charset.UnsupportedCharsetException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.bind.Marshaller;

/**
 * IBM Websphere MQ implementation of <code>JaxbMessageConverter</code>. Should also work for IBM Websphere SIBus. Keeps generated XML
 * encoding in sync with the CCSID indicated in <code>com.ibm.mq.jms.JMSC.CHARSET_PROPERTY</code> JMS property.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JaxbMessageConverterWebsphereMqImpl extends JaxbMessageConverter {

    /**
     * Value of <code>com.ibm.mq.jms.JMSC.CHARSET_PROPERTY</code> used to set Websphere MQ message charset. Value recopied from JMSC API
     * to prevent needing mq jar to compile.
     * 
     * @see com.ibm.mq.jms.JMSC#CHARSET_PROPERTY
     */
    public static final String JMSC_CHARSET_PROPERTY = "JMS_IBM_Character_Set";

    /**
     * Set given <code>message</code> encoding with Websphere MQ proprietary APIs.
     * 
     * @throws UnsupportedCharsetException
     *             No matching IBM CCSID found for given encoding
     * @see com.ibm.mq.jms.JMSC#CHARSET_PROPERTY
     * @see Marshaller#JAXB_ENCODING
     * @see fr.xebia.springframework.jms.support.converter.JaxbMessageConverter#setMessageCharset(javax.jms.TextMessage, java.lang.String)
     */
    @Override
    protected void postProcessResponseMessage(Message message) throws JMSException {
        super.postProcessResponseMessage(message);
        String encoding = this.marshallerProperties == null ? null : (String) this.marshallerProperties.get(Marshaller.JAXB_ENCODING);

        encoding = encoding == null ? "UTF-8" : encoding;

        String ccsid = String.valueOf(IbmCharsetUtils.getIbmCharacterSetId(encoding));
        message.setStringProperty(JMSC_CHARSET_PROPERTY, ccsid);
    }

}
