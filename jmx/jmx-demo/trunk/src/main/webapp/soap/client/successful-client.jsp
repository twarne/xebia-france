
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldServiceException"%>
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldService"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>

<%@page import="java.io.PrintWriter"%><html>
<head>
<title>HelloWorldService expect successful request</title>
</head>
<body>
<h1>HelloWorldService expect successful request</h1>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
    HelloWorldService helloWorldService = (HelloWorldService)applicationContext.getBean("helloWorldServiceClient-ok");
    try {
        String answer = helloWorldService.sayHi("I expect a success");
        out.println("Expected answer " + answer);
    } catch (Exception e) {
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    }
%>
</body>
</html>