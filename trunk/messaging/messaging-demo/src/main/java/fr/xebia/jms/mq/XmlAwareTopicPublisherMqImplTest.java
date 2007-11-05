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

import java.io.Reader;
import java.io.StringReader;

import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQTopic;
import com.ibm.mq.jms.MQTopicConnectionFactory;

import fr.xebia.jms.XmlAwareTopicPublisher;
import fr.xebia.jms.XmlMessage;
import fr.xebia.jms.XmlMessageImpl;

/**
 * Test {@link XmlAwareTopicPublisher}
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class XmlAwareTopicPublisherMqImplTest extends TestCase {

    protected TopicConnection connection;

    protected TopicSession session;

    protected Topic topic;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MQTopicConnectionFactory connectionFactory = new MQTopicConnectionFactory();
        connectionFactory.setHostName("localhost");
        connectionFactory.setPort(1414);
        connectionFactory.setChannel("default");
        connectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

        this.connection = connectionFactory.createTopicConnection();
        this.connection.start();
        this.session = this.connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        this.topic = this.session.createTemporaryTopic();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.session.close();
        this.connection.close();
    }

    public void testSendXmlMessage() throws Exception {
        TopicPublisher topicSender = new XmlAwareTopicPublisher(this.session.createPublisher(this.topic), new JmsCharsetUtilHelperMqImpl());

        XmlMessage xmlMessage = new XmlMessageImpl(this.session.createTextMessage());

        Reader xml = new StringReader("<root><child>Hello Topic יאט with default encoding</child></root>");
        xmlMessage.setSource(new StreamSource(xml));

        topicSender.publish(xmlMessage);

        int topicCodedCharacterSetId = ((MQTopic) this.topic).getCCSID();
        String expectedCharset = IbmCharsetUtils.getCharsetName(topicCodedCharacterSetId);
        String expectedXmlDeclaration = "<?xml version=\"1.0\" encoding=\"" + expectedCharset + "\"?>";

        String xmlAsText = ((XmlMessageImpl) xmlMessage)._getTextMessage().getText();
        assertTrue(xmlAsText.startsWith(expectedXmlDeclaration));
    }

    public void testSendIso88591XmlMessage() throws Exception {
        TopicPublisher topicSender = new XmlAwareTopicPublisher(this.session.createPublisher(this.topic), new JmsCharsetUtilHelperMqImpl());

        XmlMessage xmlMessage = new XmlMessageImpl(this.session.createTextMessage());

        Reader xml = new StringReader("<root><child>Hello Topic יאט with ISO-8859-1 encoding</child></root>");
        xmlMessage.setSource(new StreamSource(xml));
        xmlMessage.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        topicSender.publish(xmlMessage);

        String expectedCharset = "ISO-8859-1";

        // check XML
        String expectedXmlDeclaration = "<?xml version=\"1.0\" encoding=\"" + expectedCharset + "\"?>";
        String xmlAsText = ((XmlMessageImpl) xmlMessage)._getTextMessage().getText();
        assertTrue(xmlAsText.startsWith(expectedXmlDeclaration));

        // check JMS Header
        String ccsid = xmlMessage.getStringProperty(JMSC.CHARSET_PROPERTY);
        assertNotNull("ccsid", ccsid);
        String actualCharset = IbmCharsetUtils.getCharsetName(Integer.parseInt(ccsid));
        assertEquals(expectedCharset, actualCharset);
    }
}
