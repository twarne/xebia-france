<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="javax.servlet.http.*"%>
<!-- <%= new java.sql.Timestamp(System.currentTimeMillis()) %> -->
<!-- <%= System.currentTimeMillis() %> -->
<HTML>
<HEAD>
	<TITLE>Servlet's information</TITLE>
</HEAD>
<BODY>
<h1>Request information</h1>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr>
	<tr><td>Protocol</td><td><%= request.getProtocol()%></td></tr>
	<tr><td>ContentType</td><td><%= request.getContentType()%></td></tr>
	<tr><td>RemoteAddr</td><td><%= request.getRemoteAddr()%></td></tr>
	<tr><td>RemoteHost</td><td><%= request.getRemoteHost()%></td></tr>
	<tr><td>Scheme</td><td><%= request.getScheme()%></td></tr>
	<tr><td>ServerName</td><td><%= request.getServerName()%></td></tr>
	<tr><td>ServerPort</td><td><%= request.getServerPort()%></td></tr>
	<tr><th colspan=2>Http request information</th></tr>
	<tr><th>Name</th><th>Value</th></tr>
	<tr><td>AuthType</td><td><%= request.getAuthType() %></td></tr>
	<tr><td>ContextPath</td><td><%= request.getContextPath() %></td></tr>	
	<tr><td>Method</td><td><%= request.getMethod() %></td></tr>
	<tr><td>PathInfo</td><td><%= request.getPathInfo() %></td></tr>
	<tr><td>PathTranslated</td><td><%= request.getPathTranslated() %></td></tr>
	<tr><td>QueryString</td><td><%= request.getQueryString() %></td></tr>
	<tr><td>RemoteUser</td><td><%= request.getRemoteUser() %></td></tr>
	<tr><td>RequestedSessionId</td><td><%= request.getRequestedSessionId() %></td></tr>
	<tr><td>RequestURI</td><td><%= request.getRequestURI() %></td></tr>
	<tr><td>ServletPath</td><td><%= request.getServletPath() %></td></tr>
	<tr><td>isRequestedSessionIdFromCookie</td><td><%= request.isRequestedSessionIdFromCookie() %></td></tr>
	<tr><td>isRequestedSessionIdFromURL</td><td><%= request.isRequestedSessionIdFromURL() %></td></tr>
	<tr><td>isRequestedSessionIdValid</td><td><%= request.isRequestedSessionIdValid() %></td></tr>
</table>
<br>
<h1>Request headers</h1>
<%
Enumeration enuHeaders = request.getHeaderNames();
%>

<table border="1">
	<tr><th>Name</th><th>Value</th></tr><%
	while (enuHeaders.hasMoreElements()) {
		String header = (String) enuHeaders.nextElement();
		%><tr><td><%=header%></td><td><%=request.getHeader(header)%></td></tr><%
	}
%></table>

<h1>Request cookies</h1>
<%
Cookie[] arrCookies = request.getCookies();
%>
<table border="1">
	<tr><th>Name</th><th>Value</th><th>Comment</th><th>Domain</th><th>MaxAge</th><th>Path</th><th>Secure</th><th>Version</th></tr><%
	if (null != arrCookies) {
		for (int i = 0; i < arrCookies.length; ++i) {
			Cookie cookie = arrCookies[i];
			%><tr>
				<td><%= cookie.getName() %></td>
				<td><%= cookie.getValue() %></td>
				<td><%= cookie.getComment() %></td>
				<td><%= cookie.getDomain() %></td>
				<td><%= cookie.getMaxAge() %></td>
				<td><%= cookie.getPath() %></td>
				<td><%= cookie.getSecure() %></td>
				<td><%= cookie.getVersion() %></td>
			</tr><%
		}
	}
%>
</table>
<br>

<h1>Request attributes</h1>
<%
Enumeration enuAttributes = request.getAttributeNames();
%>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr><%
	while (enuAttributes.hasMoreElements()) {
		String attribute = (String) enuAttributes.nextElement();
		%><tr><td><%= attribute%></td><td><%= request.getAttribute(attribute)%></td></tr><%
	}
%></table>


<h1>Request parameters</h1>
<%
Enumeration enuParameters = request.getParameterNames();
%>
<table border="1">
	<tr><th>Name</th><th>Value</th><th>N° of values</th></tr><%
	while (enuParameters.hasMoreElements()) {
		String parameter = (String) enuParameters.nextElement();
		%><tr><td><%= parameter%></td><td><%= request.getParameter(parameter)%></td><td><%= request.getParameterValues(parameter).length%></td></tr><%
	}
%></table>

<br>
<h1>Session information</h1>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr>
	<tr><td>MaxInactiveInterval (min)</td><td><%= (session.getMaxInactiveInterval() / 60)%></td></tr>
	<tr><td>ID</td><td><%= session.getId()%></td></tr>
	<tr><td>LastAccessedTime</td><td><%= new Date(session.getLastAccessedTime())%></td></tr>
</table>
<%
enuAttributes = session.getAttributeNames();
%>
<h2>Attributes</h2>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr><%
	while (enuAttributes.hasMoreElements()) {
        String attribute = (String) enuAttributes.nextElement();
		%><tr><td><%= attribute%></td><td><%= session.getAttribute(attribute)%></td></tr><%
	}
%></table>
<br>

<h1>Application information</h1>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr>
	<tr><td>ServletContextName</td><td><%= application.getServletContextName()%></td></tr>
	<tr><td>MajorVersion</td><td><%= application.getMajorVersion()%></td></tr>
	<tr><td>MinorVersion</td><td><%= application.getMinorVersion()%></td></tr>
	<tr><td>ServerInfo</td><td><%= application.getServerInfo()%></td></tr>
	<tr><td>ResourceUrlForSlash</td><td><%= application.getResource("/")%></td></tr>
</table>
<h1>Application attributes</h1>
<%
enuAttributes = application.getAttributeNames();
%>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr><%
	while (enuAttributes.hasMoreElements()) {
		String attribute = (String) enuAttributes.nextElement();

		%><tr><td  valign="top"><%= attribute %></td><td><%

		if(attribute.indexOf("classpath") >= 0){
			out.println("<pre>");

			Object oClasspath = application.getAttribute(attribute);
			String classpath = oClasspath == null ? "" : oClasspath.toString();
			String[] arrClasspath = classpath.split(System.getProperty("path.separator"));
			for (int i = 0; i < arrClasspath.length; i++) {
				out.println(arrClasspath[i] + System.getProperty("path.separator"));
			}

			out.println("</pre>");
		} else {

			%><%= application.getAttribute(attribute) %><%
		}
		%></td></tr><%
	}
%>
</table>
<br>

<h1>Application init parameters</h1>
<%
enuParameters = application.getInitParameterNames();
%>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr>
<%
while (enuParameters.hasMoreElements()) {
	String parameter = (String) enuParameters.nextElement();
	%><tr><td><%= parameter %></td><td><%= application.getInitParameter(parameter) %></td></tr><%
}
%>
</table>
<br>

<h1>InitialContext</h1>
<%
try {

	InitialContext initialContext = new InitialContext(new Properties());

	%>
	<table border="1">
		<tr><th>Name</th><th>Value</th></tr>
		<tr><td>initialContext.nameInNamespace</td><td><%= initialContext.getNameInNamespace()  %></td></tr>
	</table>
	<%
} catch(Throwable e){

	out.write("<pre>");
	PrintWriter printWriter = new PrintWriter(out);
	e.printStackTrace(printWriter);
	out.write("</pre>");
	printWriter.close();
}
%>
<h1>System information</h1>
<%
Properties propSystem = System.getProperties();
Enumeration enuSystemProperties = propSystem.propertyNames();
%>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr>
<%
while (enuSystemProperties.hasMoreElements()) {
	String property = (String) enuSystemProperties.nextElement();
	%><tr><td valign="top"><%= property %></td><td><%

	if(
		property.indexOf("java.library.path") >= 0 || 
		property.indexOf("ws.ext.dirs") >= 0 || 
		property.indexOf("java.class.path") >= 0 || 
		property.indexOf("sun.boot.class.path") >= 0
		){
		out.println("<pre>");

		Object oClasspath = propSystem.getProperty(property);
		String classpath = oClasspath == null ? "" : oClasspath.toString();
		String[] arrClasspath = classpath.split(System.getProperty("path.separator"));
		for (int i = 0; i < arrClasspath.length; i++) {
			out.println(arrClasspath[i] + System.getProperty("path.separator"));
		}

		out.println("</pre>");
	} else {

		%><%= propSystem.getProperty(property) %><%
	}
	%></td></tr><%
}
java.io.File file = new java.io.File(".");
String workingDirectory = file.getAbsolutePath();
%>
	<tr><td>Working directory</td><td><%= workingDirectory %></td></tr>
</table>

<br>
<h1>Memory</h1>
<table border="1">
	<tr><th>Name</th><th>Value</th></tr>
	<tr><td>Total Memory</td><td><%=Runtime.getRuntime().totalMemory()%></td></tr>
	<tr><td>Free Memory</td><td><%=Runtime.getRuntime().freeMemory()%></td></tr>
	
</table>
<br>
<hr>
<form name="dynamicForm" method="POST" action="" onSubmit="return submitForm(this);">
action : <input name="formAction" value="" size="100"><br>

field1 : <input name="field1" value="" size="100"><br>
chk1 : <input name="chk1" type=checkbox><br>
radio : opt1 <input name=radio1 type=radio value="opt1"> - opt2 <input name=radio1 type=radio value="opt2"><br>
Select1 :<select name=select1 size=2>
	<option value="slt1">slt1</option>
	<option value="slt2">slt2</option>
	<option value="slt3">slt3</option>
	</select><br>
<input value="submit1" name="submit1Name" type="submit"> <input value="submit2" name="submit2Name" type="submit">
</form>
<script>
document.dynamicForm.formAction.value = document.location.href;

function submitForm(zeForm){
	zeForm.action = zeForm.formAction.value;
	return true;
}
</script>
</BODY>
</HTML>

