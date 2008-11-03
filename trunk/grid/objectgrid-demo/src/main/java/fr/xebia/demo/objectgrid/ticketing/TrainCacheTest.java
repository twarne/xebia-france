/*
 * Copyright 2002-2008 the original author or authors.
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

import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.em.EntityManager;
import com.ibm.websphere.objectgrid.em.EntityTransaction;

import fr.xebia.demo.objectgrid.ticketing.test.AbstractTicketingGridTest;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class TrainCacheTest extends AbstractTicketingGridTest {
    
    public void testGetTrain() throws Exception {
        Session session = objectGrid.getSession();
        EntityManager entityManager = session.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        
        Integer id = persistedTrainIds.get(0);
        Train train = (Train)entityManager.find(Train.class, id);
        System.out.println(train);
        for (TrainStop trainStop : train.getTrainStops()) {
            if (trainStop == null) {
                System.out.println("\tnull train stop");
            } else {
                System.out
                    .println("\t" + trainStop.getId() + " - " + trainStop.getStationCode() + " - " + trainStop.getDepartureDateTime());
            }
        }
        transaction.commit();
    }
}
