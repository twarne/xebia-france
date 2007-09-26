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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.datagrid.MapGridAgent;
import com.ibm.websphere.objectgrid.em.EntityManager;
import com.ibm.websphere.objectgrid.em.Query;

/**
 * <p>
 * Agent to perform distributed queries
 * </p>
 * <p>
 * See <a href="http://www.ibm.com/developerworks/forums/dw_thread.jsp?thread=175971&forum=778&cat=9&ca=drs-fo"> Best practice for
 * EntityManager queries on a partitioned grid.</a>
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@SuppressWarnings("unchecked")
public class QueryAgent<Entity extends Object> implements MapGridAgent {

    private final static Logger logger = Logger.getLogger(QueryAgent.class);

    public final static String AGENT_RUNNER_MAP_NAME = "AgentRunner";

    private static final long serialVersionUID = 1L;

    protected String[] paramNames;

    protected Object[] values;

    protected String queryString;

    public QueryAgent(String query, String paramName, Object value) {
        this(query, new String[]{paramName}, new Object[]{value});
    }

    public QueryAgent(String query, String[] paramNames, Object[] values) {
        super();

        if (paramNames.length != values.length) {
            throw new IllegalArgumentException("Length of paramNames array must match length of values array");
        }

        this.queryString = query;
        this.paramNames = paramNames;
        this.values = values;
    }

    public Object process(Session session, ObjectMap objectMap, Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Returns a one entry Map<Integer, List<T>> with key=partitionId and value is a list of entities.
     * </p>
     * <p>
     * Note that the key is the partition id to differentiate the results of the execution of the agents on the different partitions of the
     * Grid.
     * </p>
     */
    public Map processAllEntries(Session session, ObjectMap objectMap) {

        EntityManager entityManager = session.getEntityManager();

        try {
            Query q = entityManager.createQuery(this.queryString);

            for (int i = 0; i < this.paramNames.length; i++) {
                String paramName = this.paramNames[i];
                Object value = this.values[i];
                q.setParameter(paramName, value);
            }

            // key is the partition id to differentiate agent results according to the partition on which they run
            Map<Integer, List<Entity>> findResult = new HashMap<Integer, List<Entity>>();

            int partitionId = session.getObjectGrid().getMap(objectMap.getName()).getPartitionId();
            List<Entity> resultEntries = new ArrayList<Entity>();
            findResult.put(partitionId, resultEntries);

            Iterator<Entity> iter = q.getResultIterator();
            while (iter.hasNext()) {
                Entity entity = (Entity) iter.next();
                resultEntries.add(entity);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Query '" + this + "' found " + resultEntries.size() + " entries");
            }
            return findResult;

        } catch (RuntimeException e) {
            String message = "Exception querying Grid with " + this + " : " + e;
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("query", this.queryString).append("paramNames", this.paramNames).append("values",
                this.values).toString();
    }
}
