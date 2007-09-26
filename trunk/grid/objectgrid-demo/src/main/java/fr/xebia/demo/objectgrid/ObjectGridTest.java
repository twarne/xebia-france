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

        employees.add(new Employee(i++, "BACROT", "Mélanie", " melaniebacrot@gmail.com", "06 63 10 25 11", 0));
        employees.add(new Employee(i++, "BITARD", "Geoffrey", " ", "06 21 81 80 94", 0));
        employees.add(new Employee(i++, "BODET", "Guillaume", "gbodet@gmail.com", "06 20 88 86 65", 0));
        employees.add(new Employee(i++, "BORGOLTZ", "Alexandre", "alexandre.borgoltz@gmail.com", "06 22 00 10 75", 0));
        employees.add(new Employee(i++, "BOUCHOT", "Christophe", "christophe.bouchot@gmail.com", "06 20 73 40 46", 0));
        employees.add(new Employee(i++, "CARRE", "Guillaume", "guillaume.carre@gmail.com", "06 62 57 56 38", 0));
        employees.add(new Employee(i++, "D'ANGELA", "Laurent", " ", "06 84 15 56 07", 0));
        employees.add(new Employee(i++, "EVENO", "Manuel", "manuel.eveno@gmail.com", "06 13 43 59 92", 0));
        employees.add(new Employee(i++, "GRISO", "Nicolas", "nicolas.griso@gmail.com", "06 74 56 54 93", 0));
        employees.add(new Employee(i++, "HEUBES", "Christophe", "christophe.heubes@gmail.com", "06 60 10 94 46", 0));
        employees.add(new Employee(i++, "JOZWIAK", "Nicolas", "n.jozwiak@gmail.com", "06 84 56 80 59", 0));
        employees.add(new Employee(i++, "LE CLERC", "Cyrille", "cyrille.leclerc@gmail.com", "06 61 33 69 86", 0));
        employees.add(new Employee(i++, "LEGARDEUR", "Luc", "luc.legardeur@gmail.com", "06 20 701 702", 0));
        employees.add(new Employee(i++, "MOUSSAUD", "Benoit", "bmoussaud@gmail.com", "06 70 57 33 17", 0));
        employees.add(new Employee(i++, "SAGAKIAN", "David", "dsa.xebia@gmail.com", "06 75 05 44 30", 0));
        employees.add(new Employee(i++, "SIDHOUM", "Nadia", " ", "06 61 90 46 47", 0));
        employees.add(new Employee(i++, "THIVENT", "Pascal", "pthivent@gmail.com", "06 18 01 07 20", 0));
        return employees;
    }
}
