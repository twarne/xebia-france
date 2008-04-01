/*
 * Copyright 2002-2008 Xebia and the original author or authors.
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
package fr.xebia.springframework.jms.support.converter;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.JmsUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:fr/xebia/springframework/jms/support/converter/beans.xml"})
public class JaxbMessageConverterSpringTest {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "Employee")
    @XmlType(name = "Employee", propOrder = {"id", "lastName", "firstName", "gender", "birthdate"})
    public static class Employee implements Serializable {

        private final static long serialVersionUID = 1L;

        protected Integer id;

        @XmlElement(required = true)
        protected String lastName;

        @XmlElement(required = true)
        protected String firstName;

        protected Gender gender;

        @XmlElement(required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar birthdate;

        public Employee() {
            super();
        }

        public Employee(Integer id, String firstName, String lastName, Gender gender, XMLGregorianCalendar birthdate) {
            super();
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.birthdate = birthdate;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Employee other = (Employee) obj;
            if (birthdate == null) {
                if (other.birthdate != null)
                    return false;
            } else if (!birthdate.equals(other.birthdate))
                return false;
            if (firstName == null) {
                if (other.firstName != null)
                    return false;
            } else if (!firstName.equals(other.firstName))
                return false;
            if (gender == null) {
                if (other.gender != null)
                    return false;
            } else if (!gender.equals(other.gender))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (lastName == null) {
                if (other.lastName != null)
                    return false;
            } else if (!lastName.equals(other.lastName))
                return false;
            return true;
        }

        public XMLGregorianCalendar getBirthdate() {
            return birthdate;
        }

        public String getFirstName() {
            return firstName;
        }

        public Gender getGender() {
            return gender;
        }

        public Integer getId() {
            return id;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((birthdate == null) ? 0 : birthdate.hashCode());
            result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
            result = prime * result + ((gender == null) ? 0 : gender.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
            return result;
        }

        public void setBirthdate(XMLGregorianCalendar birthdate) {
            this.birthdate = birthdate;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    @XmlType(name = "Gender")
    @XmlEnum
    public enum Gender {

        MALE, FEMALE;

        public static Gender fromValue(String v) {
            return valueOf(v);
        }

        public String value() {
            return name();
        }

    }

    @Autowired
    protected ConnectionFactory connectionFactory;

    protected Connection connection;

    protected Session session;

    @Autowired
    protected JaxbMessageConverter jaxbMessageConverter;

    @After
    public void after() throws Exception {
        JmsUtils.closeSession(session);
        JmsUtils.closeConnection(connection);
    }

    @Before
    public void before() throws Exception {
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    }

    @Test
    public void testFromMessage() throws Exception {

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<Employee>" + "<id>1</id>"
                + "<lastName>Le Clerc</lastName>" + "<firstName>Cyrille</firstName>" + "<gender>MALE</gender>"
                + "<birthdate>1976-01-05</birthdate>" + "</Employee>";
        Message message = session.createTextMessage(xml);

        Employee expected = new Employee(1, "Cyrille", "Le Clerc", Gender.MALE, DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                1976, 01, 05, DatatypeConstants.FIELD_UNDEFINED));

        Employee actual = (Employee) jaxbMessageConverter.fromMessage(message);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testToMessage() throws Exception {

        Employee employee = new Employee(1, "Cyrille", "Le Clerc", Gender.MALE, DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                1976, 01, 05, DatatypeConstants.FIELD_UNDEFINED));

        TextMessage actualMessage = (TextMessage) jaxbMessageConverter.toMessage(employee, session);

        String actual = actualMessage.getText();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<Employee>" + "<id>1</id>"
                + "<lastName>Le Clerc</lastName>" + "<firstName>Cyrille</firstName>" + "<gender>MALE</gender>"
                + "<birthdate>1976-01-05</birthdate>" + "</Employee>";

        Assert.assertEquals(expected, actual);
    }
}
