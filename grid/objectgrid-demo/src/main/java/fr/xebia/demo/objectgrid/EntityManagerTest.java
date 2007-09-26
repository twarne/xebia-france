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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ibm.websphere.objectgrid.BackingMap;
import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridManager;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.PartitionManager;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.datagrid.AgentManager;
import com.ibm.websphere.objectgrid.datagrid.MapGridAgent;
import com.ibm.websphere.objectgrid.em.EntityManager;
import com.ibm.websphere.objectgrid.em.Query;

import fr.xebia.demo.objectgrid.data.Employee;

public class EntityManagerTest extends AbstractObjectGridTest {

    @SuppressWarnings("unchecked")
    public void testObjectQueryOnPartitionedData() throws Exception {

        // LOAD OBJECT GRID, SESSION AND ENTITY MANAGER
        Session session = this.objectGrid.getSession();
        EntityManager entityManager = session.getEntityManager();

        // GET PARTITION MANAGER
        BackingMap employeeBackingMap = objectGrid.getMap("Employee");
        PartitionManager partitionManager = employeeBackingMap.getPartitionManager();

        // PERFORM QUERY ON EACH PARTITION
        for (int partitionId = 0; partitionId < partitionManager.getNumOfPartitions(); partitionId++) {

            // BEGIN TRANSACTION (otherwise "TransactionRequiredException")
            // use one transaction per partion, otherwise "PersistenceException: Invalid partition id: 1. The current transaction is pinned
            // to partition id: 0"
            entityManager.getTransaction().begin();

            Query query = entityManager.createQuery("select e from Employee e where e.lastName=:lastName");
            query.setParameter("lastName", "BACROT");
            // define partition (otherwise "PersistenceException: Unable to route request for non-root partitioned entity")
            query.setPartition(partitionId);

            Iterator<Employee> iterator = query.getResultIterator();
            while (iterator.hasNext()) {
                Employee employee = iterator.next();
                System.out.println("Partition " + partitionId + " : " + employee);
            }

            // COMMIT TRANSACTION
            entityManager.getTransaction().commit();
        }
    }

    @SuppressWarnings("unchecked")
    public void testObjectQueryOnPartitionedGridWithAgent() throws Exception {

        // LOAD OBJECT GRID, SESSION AND ENTITY MANAGER
        ObjectGridManager objectGridManager = ObjectGridManagerFactory.getObjectGridManager();
        ClientClusterContext clientClusterContext = objectGridManager.connect("localhost:2809", null, null);
        ObjectGrid objectGrid = objectGridManager.getObjectGrid(clientClusterContext, "xebiaGrid");
        Session session = objectGrid.getSession();

        AgentManager agentManager = session.getMap(QueryAgent.AGENT_RUNNER_MAP_NAME).getAgentManager();
        MapGridAgent agent = new QueryAgent<Employee>("select e from Employee e where e.lastName=:lastName", "lastName", "BACROT");

        Map<Integer, List<Employee>> employeesByPartitionId = agentManager.callMapAgent(agent);
        List<Employee> result = new ArrayList<Employee>();
        for (Entry<Integer, List<Employee>> entry : employeesByPartitionId.entrySet()) {
            Integer partitionId = entry.getKey();
            List<Employee> partitionResults = entry.getValue();
            System.out.println("Found " + partitionResults.size() + " entries on partition " + partitionId);
            result.addAll(partitionResults);
        }

        for (Employee employee : result) {
            System.out.println(employee);
        }
    }
}
