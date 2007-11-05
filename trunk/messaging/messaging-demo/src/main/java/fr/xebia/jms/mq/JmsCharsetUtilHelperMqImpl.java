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
package fr.xebia.jms.mq;

import java.nio.charset.UnsupportedCharsetException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQDestination;

import fr.xebia.jms.JmsCharsetHelper;

/**
 * <p>
 * Implementation of {@link JmsCharsetHelper} for IBM Websphere MQ.
 * </p>
 * <p>
 * Relies on IBM coded Character Set ID (aka ccsid).
 * </p>
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class JmsCharsetUtilHelperMqImpl implements JmsCharsetHelper {

    /**
     * @see #getMessageCharset(Message)
     */
    public void setMessageCharset(Message message, String charset) throws JMSException, UnsupportedCharsetException {
        int ccsid = IbmCharsetUtils.getIbmCharacterSetId(charset);
        message.setStringProperty(JMSC.CHARSET_PROPERTY, String.valueOf(ccsid));
    }

    /**
     * {@link Destination} charset is defined by {@link MQDestination#getCCSID()}
     */
    public String getDestinationCharset(Destination destination) {
        int destinationCodedCharacterSetId = ((MQDestination) destination).getCCSID();
        String charsetName = IbmCharsetUtils.getCharsetName(destinationCodedCharacterSetId);
        return charsetName;
    }

    /**
     * {@link Message} charset is defined via the {@link JMSC#CHARSET_PROPERTY}. Value is an IBM Character Set ID
     * 
     * @see IbmCharsetUtils#getIbmCharacterSetId(String)
     */
    public String getMessageCharset(Message message) throws JMSException {
        String ccsid = message.getStringProperty(JMSC.CHARSET_PROPERTY);
        String charset;
        if (ccsid == null) {
            charset = null;
        } else {
            charset = IbmCharsetUtils.getCharsetName(Integer.parseInt(ccsid));
        }
        return charset;
    }
}
