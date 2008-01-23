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
package fr.xebia.demo.objectgrid;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridManager;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.em.EntityManager;
import com.ibm.websphere.objectgrid.em.EntityTransaction;

import fr.xebia.demo.objectgrid.data.Employee;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ObjectGridTest extends TestCase {

    public void testCreateObjectGrid() throws Exception {
        ObjectGridManager objectGridManager = ObjectGridManagerFactory.getObjectGridManager();

        // enable trace
        objectGridManager.setTraceEnabled(true);
        // objectGridManager.setTraceSpecification("ObjectGrid=all=enabled");

        // create grid
        ObjectGrid objectGrid = objectGridManager.createObjectGrid();
        System.out.println("objectGrid:" + objectGrid);
    }

    public void testEntityManager() throws Exception {
        ObjectGridManager objectGridManager = ObjectGridManagerFactory.getObjectGridManager();

        // enable trace
        objectGridManager.setTraceEnabled(true);
        // objectGridManager.setTraceSpecification("ObjectGrid=all=enabled");

        // create grid
        ObjectGrid objectGrid = objectGridManager.createObjectGrid();
        objectGrid.registerEntities(new Class[]{Employee.class});
        Session ssession = objectGrid.getSession();
        EntityManager entityManager = ssession.getEntityManager();

        // fill data
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        List<Employee> employees = loadEmployees();
        for (Employee employee : employees) {
            entityManager.persist(employee);
            System.out.println("Add " + employee);
        }
        transaction.commit();
    }

    private List<Employee> loadEmployees() {
        int i = 0;
        List<Employee> employees = new ArrayList<Employee>();

        employees.add(new Employee(i++, "XXXXX", "Yyyyyy", " yxxxxx@gmail.com", "06 60 00 00 00", 0));
        employees.add(new Employee(i++, "AAAAA", "Bbbbbb", " baaaaa@gmail.com", "06 30 00 00 00", 0));
        return employees;
    }
}
