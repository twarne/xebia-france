
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldServiceException"%>
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldService"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>

<%@page import="java.io.PrintWriter"%><html>
<head>
<title>HelloWorldService expect 403 Exception</title>
</head>
<body>
<h1>HelloWorldService expect 403 Exception</h1>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
    HelloWorldService helloWorldService = (HelloWorldService)applicationContext
        .getBean("helloWorldServiceClient-403Exception");
    try {
        String answer = helloWorldService.sayHi("I expect a 403 Exception");
        out.println("Unexpected answer " + answer);
    } catch (Exception e) {
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    }
%>
</body>
</html>