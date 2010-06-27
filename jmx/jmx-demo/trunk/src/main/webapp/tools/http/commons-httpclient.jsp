
<%@page import="org.apache.commons.httpclient.HttpMethod"%>
<%@page import="org.apache.commons.httpclient.methods.GetMethod"%>
<%@page import="org.apache.commons.httpclient.HttpClient"%>
<%
    HttpClient httpclient = new HttpClient();
    HttpMethod httpMethod = new GetMethod("https://localhost/");
    try {
        httpclient.executeMethod(httpMethod);
        out.println(httpMethod.getURI() + " : ");
        out.println(httpMethod.getStatusLine());
    } finally {
        httpMethod.releaseConnection();
    }
%>