<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>

<%@page import="java.io.PrintWriter"%>
<%@page import="fr.xebia.demo.jmx.webservice.RestHelloService"%><html>
<head>
<title>HelloWorldService expect 404 Exception</title>
</head>
<body>
<h1>HelloWorldService expect 404 Exception</h1>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
    RestHelloService restHelloService = (RestHelloService)applicationContext
        .getBean("restHelloServiceClient-404Exception");
    try {
        String answer = restHelloService.sayHello("I expect a 404 Exception");
        out.println("Unexpected answer " + answer);
    } catch (Exception e) {
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    }
%>
</body>
</html>