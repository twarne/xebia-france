<%@page import="java.util.Properties"%>
<%@page import="java.util.Random"%>
<%@page import="java.sql.Connection"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.sql.DataSource"%>
<%!Random random = new Random();%>
<%
    long startTime = System.currentTimeMillis();
    InitialContext initialContext = new InitialContext(new Properties());
    DataSource dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/jmx-demo-data-source");
    Thread.sleep(random.nextInt(500));
    Connection connection = dataSource.getConnection();
    try {
        Thread.sleep(random.nextInt(500));
    } finally {
        connection.close();
    }
    out.println("page duration : " + (System.currentTimeMillis() - startTime) + " ms");

%>