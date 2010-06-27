<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page
	import="org.springframework.web.context.support.WebApplicationContextUtils"%>

<%@page import="java.io.PrintWriter"%>
<%@page import="fr.xebia.demo.jmx.webservice.RestHelloService"%><html>
<head>
<title>RestHelloService expect successful request</title>
</head>
<body>
<h1>RestHelloService expect successful request</h1>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
    RestHelloService restHelloService = (RestHelloService) applicationContext.getBean("restHelloServiceClient-ok-ssl");
    try {
        String parameter = request.getParameter("parameter");
        if (parameter == null || parameter.isEmpty()) {
            parameter = "I expect a success";
        }
        out.println("invoke : restHelloService.sayHello(" + parameter + ")<br/>");
        String answer = restHelloService.sayHello(parameter);
        out.println("Actual answer: " + answer);
    } catch (Exception e) {
        out.println("<pre>");
        e.printStackTrace(new PrintWriter(out));
        out.println("</pre>");
    }
%>
</body>
</html>