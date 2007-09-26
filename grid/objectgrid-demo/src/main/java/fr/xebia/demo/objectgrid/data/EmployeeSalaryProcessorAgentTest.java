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
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.datagrid.AgentManager;
import com.ibm.websphere.projector.Tuple;
import com.ibm.websphere.projector.md.TupleAttribute;

import fr.xebia.demo.objectgrid.AbstractObjectGridTest;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EmployeeSalaryProcessorAgentTest extends AbstractObjectGridTest {

    private final static Logger logger = Logger.getLogger(EmployeeSalaryProcessorAgentTest.class);

    @SuppressWarnings("unchecked")
    public void testAgent() throws Exception {
        EmployeeSalaryProcessorAgent agent = new EmployeeSalaryProcessorAgent();
        Session session = objectGrid.getSession();
        session.begin();

        ObjectMap employeeMap = session.getMap("Employee");
        AgentManager agentManager = employeeMap.getAgentManager();

        Map<Tuple, BigDecimal> cumulativeSalaryPerEmployee = agentManager.callMapAgent(agent);

        for (Tuple employeeTuple : cumulativeSalaryPerEmployee.keySet()) {
            BigDecimal cumulativeSalary = cumulativeSalaryPerEmployee.get(employeeTuple);

            String msg = "MapAgent result: ";
            for (int i = 0; i < employeeTuple.getMetadata().getNumAttributes(); i++) {
                TupleAttribute tupleAttribute = employeeTuple.getMetadata().getAttribute(i);
                msg += tupleAttribute.getName() + "=" + employeeTuple.getAttribute(i) + ",";
            }

            msg += "cumulative salary: " + cumulativeSalary;
            logger.debug(msg);
        }
        session.commit();
    }
}
