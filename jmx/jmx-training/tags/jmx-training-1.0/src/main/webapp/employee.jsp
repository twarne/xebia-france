<%@page import="javax.xml.bind.JAXBElement"%><%@page import="javax.xml.namespace.QName"%><%@page
   import="javax.xml.bind.Marshaller"%><%@page import="fr.xebia.demo.ws.employee.EmployeeService"%><%@page
   import="fr.xebia.demo.xml.employee.Employee"%><%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%><%@page
   import="javax.xml.bind.JAXBContext"%><%!JAXBContext jaxbContext = null;
    EmployeeService employeeService = null;%><%
    String idAsString = request.getParameter("id");
    if (idAsString == null || idAsString.length() == 0) {
        // return blank page
        // TODO improve
    } else {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance("fr.xebia.demo.xml.employee");
        }
        if (employeeService == null) {
            employeeService = (EmployeeService)WebApplicationContextUtils.getWebApplicationContext(application)
                .getBean("employeeServiceImpl");
        }
        long id = Long.valueOf(request.getParameter("id"));
        Employee employee = employeeService.getEmployee(id);
        
        response.setContentType("text/xml");
        
        JAXBElement<Employee> employeeElement = new JAXBElement<Employee>(
                                                                          new QName("http://demo.xebia.fr/xml/employee", "employee"),
                                                                          Employee.class, employee);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(employeeElement, out);
    }
%>