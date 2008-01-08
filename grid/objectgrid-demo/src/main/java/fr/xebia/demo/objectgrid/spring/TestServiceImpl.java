/*
 * Copyright 2002-2006 the original author or authors.
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
package fr.xebia.demo.objectgrid.spring;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.spring.SpringLocalTxManager;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@Transactional(propagation = Propagation.REQUIRED)
public class TestServiceImpl implements TestService {

    SpringLocalTxManager txManager;

    public TestServiceImpl() {

    }

    public void initialize() throws ObjectGridException {
        Session session = txManager.getSession();
        ObjectMap m = session.getMap("TEST");
        m.insert("Hello", "Billy");
    }

    public void update(String updatedValue) throws ObjectGridException {
        Session s = txManager.getSession();
        System.out.println("Update using " + s);
        ObjectMap m = s.getMap("TEST");
        String v = (String) m.get("Hello");
        m.update("Hello", updatedValue);
    }

    public String query() throws ObjectGridException {
        Session s = txManager.getSession();
        System.out.println("Query using " + s);
        ObjectMap m = s.getMap("TEST");
        return (String) m.get("Hello");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String queryNewTx() throws ObjectGridException {
        Session s = txManager.getSession();
        System.out.println("QueryTX using " + s);
        ObjectMap m = s.getMap("TEST");
        return (String) m.get("Hello");
    }

    public void testRequiresNew(TestService testService) throws ObjectGridException {
        update("1");
        String txValue = testService.query();
        if (!txValue.equals("1")) {
            System.out.println("Requires didnt work");
            throw new IllegalStateException("requires didn't work");
        }
        String committedValue = testService.queryNewTx();
        if (committedValue.equals("1")) {
            System.out.println("Requires new didnt work");
            throw new IllegalStateException("requires new didn't work");
        }
    }

    public SpringLocalTxManager getTxManager() {
        return txManager;
    }

    public void setTxManager(SpringLocalTxManager txManager) {
        this.txManager = txManager;
    }
}