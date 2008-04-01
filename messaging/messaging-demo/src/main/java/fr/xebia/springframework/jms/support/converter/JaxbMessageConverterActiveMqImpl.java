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

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.bind.Marshaller;

/**
 * <code>JaxbMessageConverter</code> implementation for ActiveMQ. Enforce that UTF-8 is used to marshal the XML.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JaxbMessageConverterActiveMqImpl extends JaxbMessageConverter {

    protected final static Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * ActiveMQ only supports UTF-8.
     * 
     * @throws UnsupportedCharsetException
     *             if the given <code>charset is not UTF-8</code>
     */
    @Override
    protected void postProcessResponseMessage(Message message) throws JMSException, UnsupportedCharsetException {
        String encoding = this.marshallerProperties == null ? null : (String) this.marshallerProperties.get(Marshaller.JAXB_ENCODING);
        if (encoding != null) {
            Charset charset = Charset.forName(encoding);
            if (UTF_8.equals(charset) == false) {
                throw new UnsupportedCharsetException("ActiveMQ only supports UTF-8, '" + charset + "' charset is not supported");
            }
        }
    }
}
