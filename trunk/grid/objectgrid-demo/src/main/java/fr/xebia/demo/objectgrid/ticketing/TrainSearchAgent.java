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
package fr.xebia.demo.objectgrid.ticketing;

import java.util.ArrayList;
import java.util.Date;
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
 * Demo of a distributed agent that will on each partition process the salary of each employee and return to the grid client the result.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class TrainSearchAgent implements MapGridAgent {

    private final static Logger logger = Logger.getLogger(TrainSearchAgent.class);

    private static final long serialVersionUID = 1L;

    protected String departureStationCode;

    protected String arrivalStationCode;

    protected Date departureTime;

    public TrainSearchAgent(String departureStationCode, Date departureTime, String arrivalStationCode) {
        super();
        this.departureStationCode = departureStationCode;
        this.departureTime = departureTime;
        this.arrivalStationCode = arrivalStationCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("departureStationCode", this.departureStationCode).append("departureTime",
                this.departureTime).append("arrivalStationCode", this.arrivalStationCode).toString();
    }

    public Object process(Session session, ObjectMap objectMap, Object key) {
        throw new UnsupportedOperationException();
    }

    public Map<?, ?> processAllEntries(Session session, ObjectMap objectMap) {
        logger.debug("> processAllEntries");

        EntityManager entityManager = session.getEntityManager();

        Query q = entityManager.createQuery("select t from Train t");
        Iterator<Train> trainsIterator = q.getResultIterator();

        // key is the partition id to differentiate agent results according to the partition on which they run
        Map<Integer, List<Train>> matchingTrainsByPartionId = new HashMap<Integer, List<Train>>();
        int partitionId = session.getObjectGrid().getMap(objectMap.getName()).getPartitionId();
        List<Train> matchingTrains = new ArrayList<Train>();
        matchingTrainsByPartionId.put(partitionId, matchingTrains);

        int counter = 0;
        while (trainsIterator.hasNext()) {
            Train train = (Train) trainsIterator.next();

            if (train.hasAvailableSeat() && train.matchRoute(this.departureStationCode, this.departureTime, this.arrivalStationCode)) {
                matchingTrains.add(train);
            }
            counter++;
        }

        logger.debug("< processAllEntries() : processed " + counter + " entries");
        return matchingTrainsByPartionId;
    }
}
