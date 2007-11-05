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
package fr.xebia.jms.tibjms;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import com.tibco.tibjms.Tibjms;

import fr.xebia.jms.JmsCharsetHelper;

/**
 * Tibco EMS / JMS encoding is managed via the {@link Tibjms} helper class.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class JmsCharsetHelperTibjmsImpl implements JmsCharsetHelper {

    /**
     * <p>
     * Set given <code>charset</code> to given <code>TibjmsMessage</code>.
     * </p>
     * <p>
     * {@link Charset} is set with {@link Tibjms#setMessageEncoding(Message, String)}
     * </p>
     * 
     * @see #getMessageCharset(Message)
     * @see Tibjms#setMessageEncoding(Message, String)
     */
    public void setMessageCharset(Message message, String charset) throws JMSException, UnsupportedCharsetException {
        Tibjms.setMessageEncoding(message, charset);
    }

    /**
     * <p>
     * Return the default charset of the given <code>destination</code>
     * </p>
     * <p>
     * {@link Destination} charset is defined by {@link Tibjms#getEncoding()}.
     * </p>
     * 
     * @see Tibjms#getEncoding()
     */
    public String getDestinationCharset(Destination destination) {
        return Tibjms.getEncoding();
    }

    /**
     * {@link Message} charset is defined via {@link Tibjms#getMessageEncoding(Message)}.
     * 
     * @see Tibjms#setMessageEncoding(Message, String)
     */
    public String getMessageCharset(Message message) throws JMSException {
        return Tibjms.getMessageEncoding(message);
    }
}
