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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectGridManager;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.em.EntityManager;
import com.ibm.websphere.objectgrid.em.EntityTransaction;
import com.ibm.ws.objectgrid.SessionImpl;

import fr.xebia.demo.objectgrid.data.Employee;
import fr.xebia.demo.objectgrid.data.Payroll;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public abstract class AbstractObjectGridTest extends TestCase {

    private final static Logger logger = Logger.getLogger(AbstractObjectGridTest.class);

    protected ObjectGrid objectGrid;

    protected List<Employee> employees;

    public ObjectGrid getObjectGrid() {
        return objectGrid;
    }

    private List<Employee> loadEmployees() {
        int employeeId = 1;
        List<Employee> employees = new ArrayList<Employee>();

        employees.add(new Employee(employeeId++, "XXXXX", "Yyyyyy", " yxxxxx@gmail.com", "06 60 00 00 00", 0));
        employees.add(new Employee(employeeId++, "AAAAA", "Bbbbbb", " baaaaa@gmail.com", "06 30 00 00 00", 0));

        int payrollId = 0;
        for (Employee employee : employees) {
            for (int year = 2005; year < 2007; year++) {
                for (int month = 0; month < 12; month++) {
                    Date date = new Date(new GregorianCalendar(year, month, 1).getTimeInMillis());
                    BigDecimal salary = new BigDecimal(employee.getId());
                    Payroll payroll = new Payroll(payrollId++, employee, date, salary, 0, "salary for " + year + "-" + month);
                    employee.getPayrolls().add(payroll);
                }
            }
        }
        return employees;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // create grid
        this.objectGrid = loadObjectGrid();

        Session session = objectGrid.getSession();
        EntityManager entityManager = session.getEntityManager();

        // load data
        this.employees = loadEmployees();

        // fill data
        EntityTransaction transaction = entityManager.getTransaction();
        for (Employee employee : employees) {
            transaction.begin();
            if (entityManager.find(Employee.class, employee.getId()) == null) {
                logger.debug("Persist " + employee);
                entityManager.persist(employee);
            } else {
                logger.debug("Skip already in grid " + employee);
            }
            transaction.commit();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        Session currentSession = ObjectGridUtils.getCurrentSession(this.objectGrid);
        if (((SessionImpl) currentSession).isTransactionActive()) {
            // transaction was not committed nor rolled back. force rollback
            currentSession.rollback();
        }
    }

    public ObjectGrid loadObjectGrid() throws ObjectGridException {
        ObjectGridManager objectGridManager = ObjectGridManagerFactory.getObjectGridManager();

        // enable trace
        objectGridManager.setTraceEnabled(true);
        objectGridManager.setTraceSpecification("ObjectGrid=all=enabled");
        objectGridManager.setTraceFileName("traceObjectGrid.log");

        ObjectGrid result;
        boolean useLocalGrid = false;
        if (useLocalGrid) {
            logger.info("Use local ObjectGrid");
            result = objectGridManager.createObjectGrid();
            result.registerEntities(new Class[]{Employee.class, Payroll.class});
        } else {
            logger.info("Use distributed ObjectGrid");
            String catalogServerAddresses = "localhost:2809";
            ClientClusterContext clientClusterContext = objectGridManager.connect(catalogServerAddresses, null, null);
            result = objectGridManager.getObjectGrid(clientClusterContext, "xebiaGrid");
        }
        return result;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
