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
            Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName("*:*"), null);
            
            for (ObjectInstance objectInstance : objectInstances) {
                ObjectName objectName = objectInstance.getObjectName();
                out.write("<tr><td>" + objectName + "</td></tr>\r\n");
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