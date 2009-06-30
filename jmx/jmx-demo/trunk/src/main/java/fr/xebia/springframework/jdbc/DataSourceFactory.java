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
package fr.xebia.springframework.jdbc;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.util.StringUtils;

/**
 * TODO complete properties settings.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class DataSourceFactory implements FactoryBean, InitializingBean, DisposableBean, BeanNameAware {
    @ManagedResource
    public static class SpringJmxEnableBasicDataSource extends BasicDataSource implements SelfNaming {
        private final ObjectName objectName;
        
        public SpringJmxEnableBasicDataSource(ObjectName objectName) {
            super();
            this.objectName = objectName;
        }
        
        @ManagedAttribute
        @Override
        public synchronized int getNumActive() {
            return super.getNumActive();
        }
        
        @ManagedAttribute
        @Override
        public synchronized int getNumIdle() {
            return super.getNumIdle();
        }
        
        @Override
        public ObjectName getObjectName() throws MalformedObjectNameException {
            return objectName;
        }
        
        @ManagedAttribute
        @Override
        public synchronized String getUrl() {
            return super.getUrl();
        }
    }
    
    private BasicDataSource basicDataSource;
    
    private String beanName;
    
    private String driverClassName;
    
    private int maxActive;
    
    private long maxWait;
    
    private String objectName;
    
    private String password;
    
    private String url;
    private String username;
    @Override
    public void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasLength(this.objectName)) {
            objectName = "javax.sql:type=DataSource,name=" + ObjectName.quote(beanName);
        }
        BasicDataSource newBasicDataSource = new SpringJmxEnableBasicDataSource(new ObjectName(objectName));
        newBasicDataSource.setDriverClassName(driverClassName);
        newBasicDataSource.setUrl(url);
        newBasicDataSource.setUsername(username);
        newBasicDataSource.setPassword(password);
        newBasicDataSource.setMaxActive(maxActive);
        newBasicDataSource.setMaxWait(maxWait);
        
        this.basicDataSource = newBasicDataSource;
    }
    @Override
    public void destroy() throws Exception {
        this.basicDataSource.close();
    }
    @Override
    public Object getObject() throws Exception {
        return this.basicDataSource;
    }
    @Override
    public Class<?> getObjectType() {
        return this.basicDataSource == null ? DataSource.class : this.basicDataSource.getClass();
        
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
    
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }
    
    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }
    
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
}
