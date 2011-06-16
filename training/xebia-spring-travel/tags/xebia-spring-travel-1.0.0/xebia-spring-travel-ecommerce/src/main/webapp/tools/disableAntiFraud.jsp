<%@page import="org.springframework.webflow.samples.booking.BookingAction"%>
<%@page contentType="text/plain" %>
<%@page import="java.io.PrintWriter"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%
    WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(application);
    BookingAction bookingAction = applicationContext.getBean(BookingAction.class);
    bookingAction.setEnableAntiFraudService(false);
%>