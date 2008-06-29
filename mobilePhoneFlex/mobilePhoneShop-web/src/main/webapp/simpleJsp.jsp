<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.context.support.ClassPathXmlApplicationContext"%>
<%@page import="fr.xebia.xke.business.MobilePhoneService"%>
<%@page import="java.util.List"%>
<%@page import="fr.xebia.xke.domain.MobilePhone"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<html>
	<body>
		<table>
			<tr>
				<th>
				Id
				</th>
				<th>
				Name
				</th>
			</tr>
<%
ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
MobilePhoneService service = (MobilePhoneService) ctx.getBean("mobilePhoneService");
List<MobilePhone> mobilePhones = service.getList();
Iterator<MobilePhone> it = mobilePhones.iterator();
while(it.hasNext()){
	MobilePhone mobilePhone = it.next();
	%>
			<tr>
				<td>
				<%= mobilePhone.getId() %>
				</td>
				<td>
				<%= mobilePhone.getName() %>
				</td>
			</tr>
	<%
}
%>
		</table>
	</body>
</html>