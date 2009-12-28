
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldServiceException"%>
<%@page import="fr.xebia.demo.jmx.webservice.HelloWorldService"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>

<%@page import="java.io.PrintWriter"%><html>
<head>
<title>HelloWorldService expect HelloWorldServiceException</title>
</head>
<body>
<h1>HelloWorldService expect HelloWorldServiceException</h1>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
    HelloWorldService helloWorldService = (HelloWorldService)applicationContext
        .getBean("helloWorldServiceClient-helloWorldServiceException");
    try {
        String answer = helloWorldService.sayHi("I expect an HelloWorlServiceException");
        out.println("Unexpected answer " + answer);
    } catch (HelloWorldServiceException e) {
        out.println("Received expected " + e);
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    } catch (Exception e) {
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    }
%>
</body>
</html>