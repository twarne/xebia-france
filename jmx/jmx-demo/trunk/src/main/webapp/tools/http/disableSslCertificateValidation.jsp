
<%@page import="fr.xebia.net.ssl.EasySSLSocketFactory"%>
<%@page import="javax.net.ssl.HttpsURLConnection"%><%
HttpsURLConnection.setDefaultSSLSocketFactory(new EasySSLSocketFactory());
%>