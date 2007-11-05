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

import java.io.StringReader;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * <p>
 * Implementation of an {@link XmlMessage} relying on a {@link TextMessage} for actual message
 * sending.
 * </p>
 * <p>
 * Relying on a <code>TextMessage</code> is performed wrapping a <code>TextMessage</code>
 * </p>
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class XmlMessageImpl extends MessageWrapper implements XmlMessage {

    protected TextMessage textMessage;

    protected Properties outputProperties = new Properties();

    protected Source source;

    /**
     * 
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    public XmlMessageImpl(TextMessage textMessage) throws JMSException {
        super(textMessage);
        this.textMessage = textMessage;
        if (textMessage.getText() != null) {
            // this message has been received by the MOM, parse the text
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                DOMResult result = new DOMResult(document);
                transformer.transform(new StreamSource(new StringReader(textMessage.getText())), result);
                this.source = new DOMSource(document.getFirstChild());
            } catch (Exception e) {
                JMSException jmse = new JMSException("Exception parsing textMessage text");
                jmse.setLinkedException(e);
            }
        }
    }

    /**
     * Get a copy of the XML serialization parsing properties
     * 
     * @see Transformer#getOutputProperties()
     */
    public Properties getOutputProperties() {
        Properties result = new Properties();
        result.putAll(this.outputProperties);
        return result;
    }

    /**
     * Return the value of the given XML serialization parsing property
     * 
     * @return given property value or <code>null</code>
     * @see Transformer#getOutputProperty(String)
     */
    public String getOutputProperty(String name) throws IllegalArgumentException {
        return this.outputProperties.getProperty(name);
    }

    /**
     * @see fr.xebia.jms.XmlMessage#getSource()
     */
    public Source getSource() throws JMSException {
        return this.source;
    }

    /**
     * @see Transformer#setOutputProperties(Properties)
     */
    public void setOutputProperties(Properties oformat) {
        this.outputProperties = oformat;
    }

    /**
     * @see Transformer#setOutputProperty(String, String)
     */
    public void setOutputProperty(String name, String value) throws IllegalArgumentException {
        this.outputProperties.setProperty(name, value);
    }

    /**
     * @see fr.xebia.jms.XmlMessage#setSource(javax.xml.transform.Source)
     */
    public void setSource(Source source) throws JMSException {
        this.source = source;
    }

    /**
     * Returns the underlying {@link TextMessage}
     */
    public TextMessage _getTextMessage() {
        return this.textMessage;
    }

    @Override
    public String toString() {
        return super.toString() + "[outputProperties=" + this.outputProperties + ", source=" + this.source + ", textMessage="
                + this.textMessage + "]";
    }
}
