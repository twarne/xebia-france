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
package fr.xebia.demo.ws.employee;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.xebia.demo.xml.employee.Employee;
import fr.xebia.demo.xml.employee.Gender;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:clientApplicationContext.xml"})
public class EmployeeServiceIntegrationTest {

    /**
     * On utilise un random car les resultats des services sont caches
     */
    protected static Random random = new Random();

    @Autowired
    protected EmployeeService employeeService;

    @Autowired
    protected ApplicationContext applicationContext;
    
    //@Test
    public void testEmployeeServiceEndpoint() throws Exception {
        String address = "http://localhost:8080/cxf/services/employee-service";
        //employeeServiceEndpoint.publish(address);
        System.out.println("wait ...");
        Thread.sleep(1000*60);
        System.out.println("bye");
    }
    @Test
    public void testListBeans() throws Exception {
        System.out.println("beanDefinitionNames");
       for(String beanDefinitionName : applicationContext.getBeanDefinitionNames()){
           System.out.println(beanDefinitionName);
       }
    }

    @Test
    public void testGetEmployee() throws Exception {

        final int employeeId = random.nextInt();
        Employee employee = employeeService.getEmployee(employeeId);
        System.out.println(ToStringBuilder.reflectionToString(employee));
    }

    @Test
    public void testPutEmployee() throws Exception {
        int id = random.nextInt();

        Employee employee = new Employee();
        employee.setId(null);
        employee.setLastName("Doe-" + id);
        employee.setFirstName("John");
        employee.setGender(Gender.MALE);
        employee.setBirthdate(new Date(new GregorianCalendar(1976, 01, 05).getTimeInMillis()));

        final Holder<Employee> employeeHolder = new Holder<Employee>(employee);
        employeeService.putEmployee(employeeHolder);
        System.out.println(ToStringBuilder.reflectionToString(employeeHolder.value));

    }

    @Test(expected = SOAPFaultException.class)
    public void testPutEmployeeFirstNameMissing() throws Exception {
        int id = random.nextInt();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setLastName("Doe-" + id);
        employee.setFirstName(null);
        employee.setGender(Gender.MALE);
        employee.setBirthdate(new Date(new GregorianCalendar(1976, 01, 05).getTimeInMillis()));

        final Holder<Employee> employeeHolder = new Holder<Employee>(employee);
        try {
            employeeService.putEmployee(employeeHolder);
        } catch (SOAPFaultException e) {
            e.printStackTrace();
            throw e;
        }
        /*
         * throws javax.xml.ws.soap.SOAPFaultException: Marshalling Error: cvc-complex-type.2.4.b: The content of element 'employee' is not
         * complete. One of '{"http://demo.xebia.fr/xml/employee":firstName}' is expected.
         */

    }

    @Test(expected = SOAPFaultException.class)
    public void testPutEmployeeFirstNameTooLong() throws Exception {
        int id = random.nextInt();

        Employee employee = new Employee();
        employee.setId(id);
        employee.setLastName("Doe-" + id);
        String firstName = StringUtils.repeat("John ", 100);
        Assert.assertTrue("firstName must be longer than 256 chars to exceed Schema constraint", firstName.length() > 256);
        employee.setFirstName(firstName);
        employee.setGender(Gender.MALE);
        employee.setBirthdate(new Date(new GregorianCalendar(1976, 01, 05).getTimeInMillis()));

        final Holder<Employee> employeeHolder = new Holder<Employee>(employee);
        try {
            employeeService.putEmployee(employeeHolder);
        } catch (SOAPFaultException e) {
            e.printStackTrace();
            throw e;
        }
        /*
         * throws javax.xml.ws.soap.SOAPFaultException: Marshalling Error: cvc-maxLength-valid: Value '...' with length = '500' is not
         * facet-valid with respect to maxLength '256' for type '#AnonType_firstNameEmployee'.
         */

    }
}
