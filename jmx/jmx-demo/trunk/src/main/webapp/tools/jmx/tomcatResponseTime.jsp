<%@ page import="java.lang.management.ManagementFactory"%>
<%@ page import="javax.management.*"%>
<%@ page import="java.io.*,java.util.*"%>
<html>
<head>
<title>MBeanServers</title>
</head>
<body>
<h1>MBeanServers</h1>
<%
    try {
        out.write("Date: " + new java.sql.Timestamp(System.currentTimeMillis()).toString() + "<br>");
        
        List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
        for (MBeanServer mbeanServer : mbeanServers) {

            out.println("<h1> MbeanServer domain = " + mbeanServer.getDefaultDomain() + "</h1>");
            out.write("<table border='1'>");
            out.write("<tr><th>Object Name</th><th>processingTime</th><th>requestCount</th><th>maxTime</th><th>errorCount</th></tr>");
            Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName("Catalina:j2eeType=Servlet,*"), null);            
            for (ObjectInstance objectInstance : objectInstances) {
                ObjectName objectName = objectInstance.getObjectName();
                out.write("<tr><td>" + objectName + "</td>");                
                out.write("<td>" + mbeanServer.getAttribute(objectName,"processingTime") + "</td>");
                out.write("<td>" + mbeanServer.getAttribute(objectName,"requestCount") + "</td>");
                out.write("<td>" + mbeanServer.getAttribute(objectName,"maxTime") + "</td>");
                out.write("<td>" + mbeanServer.getAttribute(objectName,"errorCount") + "</td>");
                out.write("</tr>\r\n");
                out.flush();
            }
            
            out.write("</table>");
            out.write("Total mbeans count <b>" + objectInstances.size() + "</b>");
        }
    } catch (Throwable e) {
        out.println("<pre>");
        PrintWriter printWriter = new PrintWriter(out);
        e.printStackTrace(printWriter);
        out.println("</pre>");
        printWriter.flush();
    }
%>
</body>
</html>