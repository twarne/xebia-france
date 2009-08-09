/*
 * Copyright 2002-2009 the original author or authors.
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

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EnhancedBasicDataSource extends BasicDataSource implements EnhancedBasicDataSourceMBean {
    
    private final Logger logger = LoggerFactory.getLogger(EnhancedBasicDataSource.class);
    
    private MBeanServer mbeanServer;
    
    private String name;

    private ObjectName objectName;

    public MBeanServer getMbeanServer() {
        return mbeanServer;
    }

    public String getName() {
        return name;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    @PostConstruct
    public void postConstruct() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException,
        MalformedObjectNameException, NullPointerException {
        if (mbeanServer != null) {
            if (this.objectName == null) {
                if (StringUtils.isEmpty(this.name)) {
                    name = String.valueOf(this.hashCode());
                }
                objectName = new ObjectName("javax.sql:type=DataSource,name=" + ObjectName.quote(name));
            }
            ObjectInstance objectInstance = mbeanServer.registerMBean(this, objectName);
            this.objectName = objectInstance.getObjectName();
            
            logger.debug("Datasource {} registered as {}", this, this.objectName);
            
        }
    }

    @PreDestroy
    public void preDestroy() throws SQLException {
        if (mbeanServer != null) {
            try {
                mbeanServer.unregisterMBean(objectName);
            } catch (MBeanRegistrationException e) {
                logger.warn("NON blocking exception unregistering dataSource " + this.objectName, e);
            } catch (InstanceNotFoundException e) {
                logger.warn("DataSource {} could not be unregistered from MBeanServer because it was not found", this.objectName);
            }
        }
        super.close();
    }
    
    public void setMbeanServer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }
}
