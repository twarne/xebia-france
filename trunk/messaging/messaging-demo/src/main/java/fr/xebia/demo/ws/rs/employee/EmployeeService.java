/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.demo.ws.rs.employee;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import fr.xebia.demo.xml.employee.Employee;
import fr.xebia.demo.xml.employee.Gender;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@Path("/employeeservice/")
public class EmployeeService {

    private final static Logger logger = Logger.getLogger(EmployeeService.class);

    @GET
    @Path("/employees/{id}/")
    public Employee getEmployee(@PathParam("id")
    int id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setLastName("Doe");
        employee.setFirstName("John");
        employee.setGender(Gender.MALE);
        try {
            employee.setBirthdate(DatatypeFactory.newInstance()
                    .newXMLGregorianCalendarDate(1976, 01, 05, DatatypeConstants.FIELD_UNDEFINED));
        } catch (DatatypeConfigurationException e) {
            logger.error("Exception creating XMLGregorianCalendarDate", e);
        }

        logger.info("getEmployee(" + id + "): " + ToStringBuilder.reflectionToString(employee));
        return employee;
    }

    @PUT
    @Path("/employees/")
    public Response updateEmployee(Employee employee) {
        logger.info("updateEmployee(" + employee + "): " + ToStringBuilder.reflectionToString(employee));
        return Response.ok(employee).build();
    }

    @POST
    @Path("/employees/")
    public Response addEmployee(Employee employee) {
        logger.info("addEmployee(" + employee + "): " + ToStringBuilder.reflectionToString(employee));
        return Response.ok(employee).build();
    }

    @DELETE
    @Path("/employees/{id}/")
    public Response deleteEmployee(@PathParam("id")
    String id) {
        return Response.ok(id).build();
    }

}
