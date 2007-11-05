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

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnectionFactory;

import fr.xebia.jms.XmlAwareQueueSender;
import fr.xebia.jms.XmlMessage;
import fr.xebia.jms.XmlMessageImpl;

/**
 * Test {@link XmlAwareQueueSender}.
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class XmlAwareQueueSenderMqTest extends TestCase {

    protected QueueConnection connection;

    protected QueueSession session;

    protected Queue queue;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        connectionFactory.setHostName("localhost");
        connectionFactory.setPort(1414);
        connectionFactory.setChannel("default");
        connectionFactory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

        this.connection = connectionFactory.createQueueConnection();
        this.connection.start();
        this.session = this.connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        this.queue = this.session.createTemporaryQueue();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.session.close();
        this.connection.close();
    }

    public void testSendXmlMessage() throws Exception {
        QueueSender queueSender = new XmlAwareQueueSender(this.session.createSender(this.queue), new JmsCharsetUtilHelperMqImpl());

        XmlMessage xmlMessage = new XmlMessageImpl(this.session.createTextMessage());

        Reader xml = new StringReader("<root><child>Hello Queue יאט with default encoding</child></root>");
        xmlMessage.setSource(new StreamSource(xml));

        queueSender.send(xmlMessage);

        int queueCodedCharacterSetId = ((MQQueue) this.queue).getCCSID();
        String expectedCharset = IbmCharsetUtils.getCharsetName(queueCodedCharacterSetId);
        String expectedXmlDeclaration = "<?xml version=\"1.0\" encoding=\"" + expectedCharset + "\"?>";

        String xmlAsText = ((XmlMessageImpl) xmlMessage)._getTextMessage().getText();
        assertTrue(xmlAsText.startsWith(expectedXmlDeclaration));
    }

    public void testSendIso88591XmlMessage() throws Exception {
        QueueSender queueSender = new XmlAwareQueueSender(this.session.createSender(this.queue), new JmsCharsetUtilHelperMqImpl());

        XmlMessage xmlMessage = new XmlMessageImpl(this.session.createTextMessage());

        Reader xml = new StringReader("<root><child>Hello Queue יאט with ISO-8859-1 encoding</child></root>");
        xmlMessage.setSource(new StreamSource(xml));
        xmlMessage.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

        queueSender.send(xmlMessage);

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

    public void testSendUtf8XmlMessage() throws Exception {
        QueueSender queueSender = new XmlAwareQueueSender(this.session.createSender(this.queue), new JmsCharsetUtilHelperMqImpl());

        XmlMessage xmlMessage = new XmlMessageImpl(this.session.createTextMessage());

        Reader xml = new StringReader("<root><child>Hello Queue יאט with UTF-8 encoding</child></root>");
        xmlMessage.setSource(new StreamSource(xml));
        xmlMessage.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        queueSender.send(xmlMessage);

        String expectedCharset = "UTF-8";

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
