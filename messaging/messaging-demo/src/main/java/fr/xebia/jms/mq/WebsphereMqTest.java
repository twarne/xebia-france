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

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQDestination;

/**
 * Tests messages formats ( MQSTR vs. RFH2/MQHRF2) and encoding (ccsid).
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class WebsphereMqTest extends TestCase {

    protected Connection connection;

    protected Session session;

    /**
     * Send the given XML {@link Source} keeping in sync the XML encoding attribute with MQ's ccsid.
     * 
     * @param xmlSource
     * @param encoding
     * @see OutputKeys#ENCODING
     * @see JMSC#ENCODING_PROPERTY
     */
    private void sendXmlMessage(Source xmlSource, String encoding) throws Exception {
        Destination destination = session.createQueue("default");

        // DEBUG INFO
        int defaultCcsid = ((MQDestination) destination).getCCSID();
        System.out.println("Queue default ccsid: " + defaultCcsid);

        // SERIALIZE XML WITH THE GIVEN ENCODING
        Transformer identityTransformer = TransformerFactory.newInstance().newTransformer();
        identityTransformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        identityTransformer.transform(xmlSource, new StreamResult(out));
        String serializedXml = out.toString(encoding);

        // CREATE XML MESSAGE WITH ENCODING AND CCSID IN SYNC
        Message xmlMessage = session.createTextMessage(serializedXml);
        int ccsid = IbmCharsetUtils.getIbmCharacterSetId(encoding);
        xmlMessage.setStringProperty(JMSC.CHARSET_PROPERTY, String.valueOf(ccsid));

        MessageProducer messageProducer = session.createProducer(destination);
        messageProducer.send(xmlMessage);

        System.out.println("Sent message");
        System.out.println(xmlMessage);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // ConfigEnvironment.setTracingOn("base");
        // System.setProperty("com.ibm.mq.tuning.socketGrainTimeout", "120000");
        // System.setProperty("MQJMS_TRACE_LEVEL", "base");
        // System.setProperty("MQJMS_TRACE_DIR", "/tmp");

        System.out.println("This test requires :");
        System.out.println("- QueueManager listening on 'localhost:1414'");
        System.out.println("- A channel called 'SYSTEM.DEF.SVRCONN'");
        System.out.println("- A Queue called 'default'");
        ConnectionFactory connectionFactory = new MQConnectionFactory();
        ((MQConnectionFactory) connectionFactory).setHostName("localhost");
        ((MQConnectionFactory) connectionFactory).setPort(80);
        ((MQConnectionFactory) connectionFactory).setChannel("SYSTEM.DEF.SVRCONN");
        ((MQConnectionFactory) connectionFactory).setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

        long timeBefore = System.currentTimeMillis();
        try {
            this.connection = connectionFactory.createConnection();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            long timeAfter = System.currentTimeMillis();

            System.out.println("connectionFactory.createConnection() duration " + (timeAfter - timeBefore) + " ms");
        }
        connection.start();

        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.session.close();
        this.connection.close();
    }

    public void testBasicSendTextMessage() throws Exception {

        Destination destination = this.session.createQueue("default");
        MessageProducer messageProducer = session.createProducer(destination);

        TextMessage textMessage = session.createTextMessage("Hello world JMS Message " + new Date());
        messageProducer.send(textMessage);

        System.out.println("Sent message");
        System.out.println(textMessage);
    }

    /**
     * Send raw MQSTR message using {@link MQDestination#setTargetClient(int)}.
     * 
     * @see MQDestination#setTargetClient(int)
     * @see JMSC#MQJMS_CLIENT_NONJMS_MQ
     */
    public void testMqstrViaApiTextMessage() throws Exception {

        Destination destination = this.session.createQueue("default");

        // Force MQSTR format
        ((MQDestination) destination).setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

        MessageProducer messageProducer = session.createProducer(destination);

        TextMessage textMessage = session.createTextMessage("Hello MQSTR world via MQQueue#setTargetClient(int) " + new Date());

        messageProducer.send(textMessage);

        System.out.println("Sent message");
        System.out.println(textMessage);
    }

    /**
     * Send raw MQSTR message using createQueue("queue:///default?targetClient=1").
     */
    public void testMqstrViaConfigurationTextMessage() throws Exception {

        // Force MQSTR format
        Destination destination = session.createQueue("queue:///default?targetClient=1");

        MessageProducer messageProducer = session.createProducer(destination);

        TextMessage textMessage = session.createTextMessage("Hello MQSTR world via createQueue(\"queue:///default?targetClient=1\" "
                + new Date());
        messageProducer.send(textMessage);

        System.out.println("Sent message");
        System.out.println(textMessage);
    }

    /**
     * Send an XML {@link Source} encoded with ISO-8859-1 charset.
     * 
     * @throws Exception
     */
    public void testSendXmlMessageIso88591() throws Exception {
        String encoding = "ISO-8859-1";
        Source xmlSource = new StreamSource(new StringReader("<root><child>Hello Queue יאט with " + encoding + " encoding</child></root>"));

        sendXmlMessage(xmlSource, encoding);
    }

    /**
     * Send an XML {@link Source} encoded with UTF-8 charset.
     * 
     * @throws Exception
     */
    public void testSendXmlMessageUtf8() throws Exception {
        String encoding = "UTF-8";
        Source xmlSource = new StreamSource(new StringReader("<root><child>Hello Queue יאט with " + encoding + " encoding</child></root>"));

        sendXmlMessage(xmlSource, encoding);
    }

}
