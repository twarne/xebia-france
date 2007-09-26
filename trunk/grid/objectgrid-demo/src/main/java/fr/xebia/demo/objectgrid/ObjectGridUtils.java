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

import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.em.EntityManager;

/**
 * Utils to simplify ObjectGrid data access code.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ObjectGridUtils {

    private final static ThreadLocal<Session> sessionHolder = new ThreadLocal<Session>();

    /**
     * Private constructor for static utilities class.
     */
    private ObjectGridUtils() {

    }

    /**
     * Obtains a {@link ThreadLocal} based {@link EntityManager}. If none exist, creates one.
     * 
     * @return the current EntityManager
     * @see org.hibernate.SessionFactory#getCurrentSession()
     */
    public static EntityManager getCurrentEntityManager(ObjectGrid objectGrid) {
        return getCurrentSession(objectGrid).getEntityManager();
    }

    /**
     * Obtains a {@link ThreadLocal} based {@link Session}. If none exist, creates one.
     * 
     * @param objectGrid
     *            the {@link ObjectGrid}
     * @return the current Session
     * @see org.hibernate.SessionFactory#getCurrentSession()
     */
    public static Session getCurrentSession(ObjectGrid objectGrid) {
        Session result = sessionHolder.get();

        if (result == null) {
            try {
                Session session = objectGrid.getSession();
                sessionHolder.set(session);
                result = session;
            } catch (ObjectGridException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * Converts a catchable {@link ObjectGridException} into a non-catchable exception (ie a subclass of {@link RuntimeException}).
     * 
     * @param objectGridException
     *            the {@link ObjectGridException} to convert
     * @return a subclass of {@link RuntimeException}
     */
    public static RuntimeException convertException(ObjectGridException objectGridException) {
        return new RuntimeException(objectGridException);
    }
}
