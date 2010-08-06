package fr.xebia.demo.ws.springws.employee;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.pox.dom.DomPoxMessageFactory;

import fr.xebia.demo.xml.employee.Employee;
import fr.xebia.demo.xml.employee.Gender;

public class EmployeeServiceClient {

	@Test
	public void testWsClient() throws Exception {

		final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(new Class[] { Employee.class, Gender.class, EmployeeElement.class });
		marshaller.afterPropertiesSet();

		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(new DomPoxMessageFactory());
		webServiceTemplate.setMarshaller(marshaller);
		webServiceTemplate.setUnmarshaller(marshaller);
		webServiceTemplate.setDefaultUri("http://localhost:" + 8080 + "/");
		webServiceTemplate.afterPropertiesSet();

		Employee employee = new Employee();
		employee.setLastName("Le Clerc");
		employee.setFirstName("Cyrille");
		employee.setBirthdate(new Date(System.currentTimeMillis()));
		employee.setGender(Gender.MALE);
		employee.setId(1);

		final EmployeeElement employeeElement = new EmployeeElement(employee);

		Server server = new Server(8080);
		Handler handler = new AbstractHandler() {

			@Override
			public void handle(String path, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
				response.setContentType("text/xml");
				marshaller.marshal(employeeElement, new StreamResult(response.getOutputStream()));
				((Request) request).setHandled(true);

			}
		};
		server.addHandler(handler);
		server.start();

		EmployeeElement returnedEmployee = (EmployeeElement) webServiceTemplate.marshalSendAndReceive(employeeElement);
		System.out.println("Client Side returned value");
		marshaller.marshal(returnedEmployee, new StreamResult(System.out));

		Thread.sleep(Long.MAX_VALUE);
		server.stop();

	}
}
