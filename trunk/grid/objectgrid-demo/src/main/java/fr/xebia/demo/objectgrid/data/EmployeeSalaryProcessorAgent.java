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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.datagrid.EntityAgentMixin;
import com.ibm.websphere.objectgrid.datagrid.MapGridAgent;
import com.ibm.websphere.objectgrid.em.EntityManager;
import com.ibm.websphere.objectgrid.em.Query;

/**
 * Demo of a distributed agent that will on each partition process the salary of each employee and return to the grid client the result.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@SuppressWarnings("unchecked")
public class EmployeeSalaryProcessorAgent implements MapGridAgent, EntityAgentMixin {

    private final static Logger logger = Logger.getLogger(EmployeeSalaryProcessorAgent.class);

    private static final long serialVersionUID = 1L;

    /**
     * Process the cumulative salary of the given employee
     * 
     * @param session
     *            the current session
     * @param objectMap
     *            the underlying objectMap
     * @param key
     *            the employee
     * @see com.ibm.websphere.objectgrid.datagrid.MapGridAgent#process(com.ibm.websphere.objectgrid.Session,
     *      com.ibm.websphere.objectgrid.ObjectMap, java.lang.Object)
     */
    public Object process(Session session, ObjectMap objectMap, Object key) {
        Employee employee = (Employee) key;

        BigDecimal cumulativeSalary = new BigDecimal(0);
        for (Payroll payroll : employee.getPayrolls()) {
            cumulativeSalary = cumulativeSalary.add(payroll.getSalary());
        }

        logger.debug(">< process(" + employee + "): " + cumulativeSalary);
        return cumulativeSalary;
    }

    public Map processAllEntries(Session session, ObjectMap objectMap) {
        logger.debug("> processAllEntries");

        EntityManager entityManager = session.getEntityManager();

        Query q = entityManager.createQuery("select e from Employee e");
        Iterator<Employee> iter = q.getResultIterator();
        Map<Employee, BigDecimal> cumulativeSalaries = new HashMap<Employee, BigDecimal>();
        int counter = 0;
        while (iter.hasNext()) {
            Employee employee = (Employee) iter.next();
            cumulativeSalaries.put(employee, (BigDecimal) process(session, objectMap, employee));
            counter++;
        }

        logger.debug("< processAllEntries() : processed " + counter + " entries");
        return cumulativeSalaries;
    }

    public Class getClassForEntity() {
        return Employee.class;
    }

}
