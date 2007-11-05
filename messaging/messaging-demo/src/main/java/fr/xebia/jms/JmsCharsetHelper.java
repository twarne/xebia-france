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
package fr.xebia.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public interface JmsCharsetHelper {

    /**
     * Returns the charset of the given <code>destination</code>.
     */
    String getDestinationCharset(Destination destination) throws JMSException;

    /**
     * Returns the charset of the given <code>message</code>.
     */
    String getMessageCharset(Message message) throws JMSException;

    void setMessageCharset(Message message, String charset) throws JMSException;
}
