<%
response.setContentType("text/xml");
response.sendRedirect(request.getRequestURL().toString());
System.out.println(request.getRequestURL() + " return 302" );
%>