
<%@page import="javax.jms.Message"%>
<%@page import="javax.jms.MessageProducer"%>
<%@page import="javax.jms.Queue"%>
<%@page import="javax.jms.Session"%>
<%@page import="javax.jms.Connection"%>
<%@page import="javax.jms.ConnectionFactory"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%
    boolean useManagedConnection = Boolean.valueOf(request.getParameter("use-managed-connection"));
    String connectionFactoryName = useManagedConnection ? "connectionFactory" : "activemqConnectionFactory";
    ConnectionFactory connectionFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(application).getBean(
            connectionFactoryName, ConnectionFactory.class);

    Connection connection = connectionFactory.createConnection();
    connection.start();

    Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    Queue queue = jmsSession.createQueue("hello-world-queue");

    MessageProducer messageProducer = jmsSession.createProducer(queue);

    TemporaryQueue replyToQueue = jmsSession.createTemporaryQueue();

    Message message = jmsSession.createTextMessage("hello world");
    message.setJMSReplyTo(replyToQueue);

    messageProducer.send(message);

    messageProducer.close();

    MessageConsumer messageConsumer = jmsSession.createConsumer(replyToQueue);
    Message responseMessage = messageConsumer.receive(1000);

    messageConsumer.close();
    replyToQueue.delete();
    jmsSession.close();

    connection.close();
%>

<%@page import="javax.jms.Destination"%>
<%@page import="javax.jms.MessageConsumer"%>
<%@page import="javax.jms.TemporaryQueue"%><html>
<body>
Managed connection :
<%=useManagedConnection%>
<h1>Send JMS Message</h1>
<pre>
<code>
<%=message%>
</code>
</pre>
<h1>Receive Response JMS Message</h1>
<pre>
<code>
<%=responseMessage%>
</code>
</pre>

</body>
</html>