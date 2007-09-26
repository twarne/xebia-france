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

import java.util.List;
import java.util.Random;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.ibm.websphere.objectgrid.ObjectGrid;

import fr.xebia.demo.objectgrid.ObjectGridTemplate;

/**
 * ObjectGrid implementation of the {@link EmployeeDao}.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeDaoObjectGridImpl extends ObjectGridTemplate<Employee> implements EmployeeDao {

    /**
     * ID generator for new employees with <code>null</code> id
     */
    protected Random randomIdGenerator = new Random(System.currentTimeMillis());

    /**
     * @param objectGrid
     *            grid on which the employees are stored
     */
    public EmployeeDaoObjectGridImpl(ObjectGrid objectGrid) {
        super(objectGrid);
    }

    public Employee find(Long id) {
        Employee employee = (Employee) getCurrentEntityManager().find(Employee.class, id);
        return employee;
    }

    public List<Employee> findByLastName(String lastName) {
        return findByNamedParam("select e from Employee e where e.lastName=:lastName", "lastName", lastName);
    }

    public void persist(Employee employee) {
        if (employee.getId() == null) {
            employee.setId(this.randomIdGenerator.nextLong());
        }
        getCurrentEntityManager().persist(employee);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("objectGrid", this.objectGrid).toString();
    }
}
