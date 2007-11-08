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

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;
import javax.jms.QueueSender;
import javax.xml.transform.Source;

/**
 * JMS {@link Message} with support for XML {@link Source}.
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public interface XmlMessage extends Message {

    /**
     * Sets the XML {@link Source} containing this message's data.
     * 
     * @throws JMSException
     *             If the JMS provider fails to set the xml source due to some internal error.
     * @exception MessageNotWriteableException
     *                If the message is in read-only mode.
     */
    void setSource(Source xmlSource) throws JMSException;

    /**
     * Gets the {@link Source} containing this message's data. The default value is
     * <code>null</code>.
     * 
     * @return The <code>source</code> containing the message's data
     * 
     * @exception JMSException
     *                If the JMS provider fails to get the xml source due to some internal error.
     */
    Source getSource() throws JMSException;

    /**
     * <p>
     * Get a copy of the output properties for the {@link Source} serialization.
     * </p>
     * 
     * @return A copy of the set of output properties in effect for {@link Source} serialization.
     * 
     * @see javax.xml.transform.OutputKeys
     * @see <a href="http://www.w3.org/TR/xslt#output">XSL Transformations (XSLT) Version 1.0</a>
     * @see javax.xml.transform.Transformer#getOutputProperties()
     */
    Properties getOutputProperties();

    /**
     * <p>
     * Get an output property that will be used during the serialization of the {@link Source}.
     * </p>
     * <p>
     * Serialization occurs during the {@link QueueSender#send(Message)} phase
     * </p>
     * 
     * @param name
     *            A non-null String that specifies an output property name, which may be namespace
     *            qualified.
     * 
     * @return The string value of the output property, or null if no property was found.
     * 
     * @throws IllegalArgumentException
     *             If the property is not supported.
     * 
     * @see javax.xml.transform.OutputKeys
     * @see javax.xml.transform.Transformer#getOutputProperty(String)
     */
    String getOutputProperty(String name) throws IllegalArgumentException;

    /**
     * Set the output properties for the {@link Source} serialization.
     * 
     * @param oformat
     *            A set of output properties that will be used to override any of the same
     *            properties in effect for the {@link Source} serialization.
     * 
     * 
     * @see javax.xml.transform.OutputKeys
     * @see javax.xml.transform.Transformer#setOutputProperties(Properties)
     */
    void setOutputProperties(Properties oformat);

    /**
     * Set an output property that will be in effect for the {@link Source} serialization.
     * 
     * @see javax.xml.transform.OutputKeys
     * @see javax.xml.transform.Transformer#setOutputProperty(String, String)
     */
    void setOutputProperty(String name, String value) throws IllegalArgumentException;
}
