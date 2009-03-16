<%@ page import="java.lang.management.ManagementFactory"%>
<%@ page import="javax.management.*"%>
<%@ page import="java.io.*,java.util.*"%>
<%!public void dumpMbeans(Set<ObjectInstance> objectInstances, JspWriter out, MBeanServer mbeanServer, String... mbeanAttributes) throws Exception {
        out.write("<table border='1'>");
        
        out.write("<tr>");
        out.print("<th>ObjectName</th>");
        for (String mbeanAttribute : mbeanAttributes) {
            out.print("<th>" + mbeanAttribute + "</th>");
        }
        out.println("</tr>");
        
        for (ObjectInstance objectInstance : objectInstances) {
            ObjectName objectName = objectInstance.getObjectName();            
            out.print("<tr><td>" + objectName + "</td>");
            for (String mbeanAttribute : mbeanAttributes) {
                out.print("<td>" + mbeanServer.getAttribute(objectName, mbeanAttribute) + "</td>");
            }
            out.println("</tr>");
        }
        
        out.println("</table>");
        
    }%>
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
            {
                out.println("<h2>DataSource</h2>");
                Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName("Catalina:type=DataSource,class=javax.sql.DataSource,*"), null);
                dumpMbeans(objectInstances, out, mbeanServer, "numActive", "numIdle", "maxActive");
                out.write("Total mbeans count <b>" + objectInstances.size() + "</b>");
            }
            {
                out.println("<h2>Http Thread Pools</h2>");
                Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName("Catalina:type=ThreadPool,*"), null);
                dumpMbeans(objectInstances, out, mbeanServer, "currentThreadsBusy", "currentThreadCount", "maxThreads");
                out.write("Total mbeans count <b>" + objectInstances.size() + "</b>");
            }
            {
                out.println("<h2>Servlets</h2>");
                Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName("Catalina:j2eeType=Servlet,*"), null);
                dumpMbeans(objectInstances, out, mbeanServer, "processingTime", "errorCount", "requestCount");
                out.write("Total mbeans count <b>" + objectInstances.size() + "</b>");
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