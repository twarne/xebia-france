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
package fr.xebia.jms.activemq;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import fr.xebia.jms.JmsCharsetHelper;

/**
 * <a href="http://activemq.apache.org/">Active MQ</a> only supports UTF-8 encoding.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JmsCharsetUtilHelperActiveMqImpl implements JmsCharsetHelper {

    protected Charset utf8 = Charset.forName("utf-8");

    /**
     * Active MQ only supports UTF-8. Other given <code>charset</code> will raise an
     * {@link UnsupportedEncodingException}
     * 
     * @throws UnsupportedEncodingException
     */
    public void setMessageCharset(Message message, String charset) throws JMSException, UnsupportedCharsetException {
        if (Charset.forName(charset).equals(this.utf8) == false) {
            throw new UnsupportedCharsetException(charset);
        }
    }

    /**
     * Active MQ only supports UTF-8.
     */
    public String getDestinationCharset(Destination destination) {
        return this.utf8.name();
    }

    /**
     * Active MQ only supports UTF-8.
     */
    public String getMessageCharset(Message message) throws JMSException {
        return this.utf8.name();
    }
}
