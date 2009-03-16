<%@ page import="java.lang.management.ManagementFactory"%>
<%@ page import="javax.management.*"%>
<%@ page import="java.io.*,java.util.*"%>
<html>
<head>
<title>CXF Response time</title>
</head>
<body>
<h1>CXF Response time</h1>
<%
    try {
        out.write("Date: " + new java.sql.Timestamp(System.currentTimeMillis()).toString() + "<br>");
        
        List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
        for (MBeanServer mbeanServer : mbeanServers) {
            
            out.println("<h1> MbeanServer domain = " + mbeanServer.getDefaultDomain() + "</h1>");
            {
                out.println("<h2>CXF Server Endpoints</h2>");
                out.write("<table border='1'>");
                out
                    .write("<tr><th>Object Name</th><th>AvgResponseTime</th><th>NumInvocations</th><th>MaxResponseTime</th><th>MinResponseTime</th></tr>");
                Set<ObjectInstance> objectInstances = mbeanServer
                    .queryMBeans(new ObjectName("org.apache.cxf:type=Performance.Counter.Server,*"), null);
                for (ObjectInstance objectInstance : objectInstances) {
                    ObjectName objectName = objectInstance.getObjectName();
                    out.write("<tr><td>" + objectName + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "AvgResponseTime") + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "NumInvocations") + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "MaxResponseTime") + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "MinResponseTime") + "</td>");
                    out.write("</tr>\r\n");
                    out.flush();
                }
                out.write("</table>");
            }
            
            {
                out.println("<h2>CXF Clients</h2>");
                out.write("<table border='1'>");
                out
                    .write("<tr><th>Object Name</th><th>AvgResponseTime</th><th>NumInvocations</th><th>MaxResponseTime</th><th>MinResponseTime</th></tr>");
                Set<ObjectInstance> objectInstances = mbeanServer
                    .queryMBeans(new ObjectName("org.apache.cxf:type=Performance.Counter.Client,*"), null);
                for (ObjectInstance objectInstance : objectInstances) {
                    ObjectName objectName = objectInstance.getObjectName();
                    out.write("<tr><td>" + objectName + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "AvgResponseTime") + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "NumInvocations") + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "MaxResponseTime") + "</td>");
                    out.write("<td>" + mbeanServer.getAttribute(objectName, "MinResponseTime") + "</td>");
                    out.write("</tr>\r\n");
                    out.flush();
                }
                out.write("</table>");
            }
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