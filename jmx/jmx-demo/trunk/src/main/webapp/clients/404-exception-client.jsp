
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldServiceException"%>
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldService"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>

<%@page import="java.io.PrintWriter"%><html>
<head>
<title>HelloWorldService expect 404 Exception</title>
</head>
<body>
<h1>HelloWorldService expect RuntimeException</h1>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
    HelloWorldService helloWorldService = (HelloWorldService)applicationContext
        .getBean("helloWorldServiceClient-404Exception");
    try {
        String answer = helloWorldService.sayHi("I expect an 404 Exception");
        out.println("Unexpected answer " + answer);
    } catch (Exception e) {
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    }
%>
</body>
</html>