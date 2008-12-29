<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.context.support.ClassPathXmlApplicationContext"%>
<%@page import="fr.xebia.xke.business.MobilePhoneService"%>
<%@page import="java.util.List"%>
<%@page import="fr.xebia.xke.domain.MobilePhone"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<?xml version="1.0" encoding="utf-8" ?>
<mobilePhones>
<%
ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
MobilePhoneService service = (MobilePhoneService) ctx.getBean("mobilePhoneService");
List<MobilePhone> mobilePhones = service.getList();
Iterator<MobilePhone> it = mobilePhones.iterator();
while(it.hasNext()){
	MobilePhone mobilePhone = it.next();
	%>
			<mobilePhone id="<%= mobilePhone.getId() %>">
				<name><%= mobilePhone.getName() %></name>
				<image><%= mobilePhone.getImage() %></image>
				<description><%= mobilePhone.getDescription() %></description>
				<price><%= mobilePhone.getPrice() %></price>
			</mobilePhone>
	<%
}
%>
</mobilePhones>