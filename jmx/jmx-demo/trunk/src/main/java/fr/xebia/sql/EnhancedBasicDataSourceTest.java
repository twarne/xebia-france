/*
 * Copyright 2008-2009 the original author or authors.
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
package fr.xebia.sql;

import static junit.framework.Assert.*;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.junit.Test;

/**
 * @author <a href="mailto:cyrille@cyrilleleclerc.com">Cyrille Le Clerc</a>
 */
public class EnhancedBasicDataSourceTest {
    
    @Test
    public void testLifeCycle() throws Exception {
        EnhancedBasicDataSource dataSource = new EnhancedBasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testcase-db");
        dataSource.setUsername("sa");
        
        MBeanServer mbeanServer = MBeanServerFactory.newMBeanServer(null);
        
        dataSource.setMbeanServer(mbeanServer);
        
        dataSource.postConstruct();
        
        ObjectName dataSourceObjectName = dataSource.getObjectName();
        try {
            mbeanServer.getObjectInstance(dataSourceObjectName);
        } catch (InstanceNotFoundException e) {
            fail("objectname " + dataSourceObjectName + " should have been found");
        }
        
        assertNotNull(dataSourceObjectName);
        
        dataSource.preDestroy();
        
        try {
            mbeanServer.getObjectInstance(dataSourceObjectName);
            fail("objectname should not have been found");
        } catch (InstanceNotFoundException e) {
            // ok expected
        }
    }
}
