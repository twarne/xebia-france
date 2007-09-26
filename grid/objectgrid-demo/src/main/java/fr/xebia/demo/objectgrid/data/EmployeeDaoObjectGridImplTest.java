/*
 * Copyright 2007 Xebia and the original author or authors.
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
package fr.xebia.demo.objectgrid.data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import fr.xebia.demo.objectgrid.AbstractObjectGridTest;
import fr.xebia.demo.objectgrid.ObjectGridUtils;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeDaoObjectGridImplTest extends AbstractObjectGridTest {

    protected EmployeeDaoObjectGridImpl employeeDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.employeeDao = new EmployeeDaoObjectGridImpl(this.objectGrid);
    }

    public void testFind() {
        Employee expected = getEmployees().get(0);
        Long id = expected.getId();

        Employee actual = employeeDao.find(id);

        assertEquals(expected, actual);
    }

    public void testFindByLastName() {
        for (Employee employee : getEmployees()) {
            testFindByLastName(employee);
        }
    }

    private void testFindByLastName(Employee expected) {
        String lastName = expected.getLastName();

        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().begin();

        List<Employee> actual = employeeDao.findByLastName(lastName);

        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().commit();

        assertEquals(1, actual.size());

        assertEquals(expected, actual.get(0));
    }

    public void testPersist() throws Exception {

        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().begin();

        Employee johnDoe = new Employee("DOE", "John", "john@doe.com", "06.05.03.02.01");

        employeeDao.persist(johnDoe);
        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().commit();

        Employee actual = employeeDao.find(johnDoe.getId());
        assertEquals(johnDoe, actual);
        assertEquals(0, actual.getPayrolls().size());
    }

    public void testPersistCascade() throws Exception {

        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().begin();

        Employee johnDoe = new Employee("DOE", "John", "john@doe.com", "06.05.03.02.01");
        long payrollId = System.currentTimeMillis();
        Date payrollDate = new Date(payrollId);
        BigDecimal salary = new BigDecimal(200);
        Payroll payroll = new Payroll(payrollId, johnDoe, payrollDate, salary, 0, "test cascade persist");
        johnDoe.getPayrolls().add(payroll);

        employeeDao.persist(johnDoe);
        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().commit();

        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().begin();
        // check persisted employee
        Employee actual = employeeDao.find(johnDoe.getId());

        assertEquals(johnDoe, actual);
        assertEquals(1, actual.getPayrolls().size());

        // get the payroll navigating the object graph employee->payroll
        Payroll actualPayrollViaEmployee = actual.getPayrolls().get(0);
        assertEquals(salary, actualPayrollViaEmployee.getSalary());

        // direct access to the payroll
        Payroll actualPayroll = (Payroll) ObjectGridUtils.getCurrentEntityManager(objectGrid).find(Payroll.class, payrollId);
        assertNotNull(actualPayroll);
        // check mono instantiation (must be in the same transaction)
        assertSame(actualPayrollViaEmployee, actualPayroll);

        ObjectGridUtils.getCurrentEntityManager(objectGrid).getTransaction().commit();
    }
}
