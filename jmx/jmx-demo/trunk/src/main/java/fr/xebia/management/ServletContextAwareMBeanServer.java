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
package fr.xebia.management;

import java.util.Hashtable;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ServletContextAwareMBeanServer extends AbstractMBeanServer implements MBeanServer, ServletContextAware, InitializingBean {
    
    private final static Logger logger = Logger.getLogger(ServletContextAwareMBeanServer.class);
    
    protected ServletContext servletContext;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.servletContext, "servletContext can NOT be null");
    }
    
    /**
     * Limit {@link ServletContext} attributes to {@link ServletContext#getContextPath()} to ease Hyperic configuration.
     * 
     * @param objectName
     * @return
     * @throws MalformedObjectNameException
     */
    protected ObjectName buildObjectName(ObjectName objectName) throws MalformedObjectNameException {
        Hashtable<String, String> table = new Hashtable<String, String>(objectName.getKeyPropertyList());
        table.put("path", this.servletContext.getContextPath());
        
        ObjectName result = ObjectName.getInstance(objectName.getDomain(), table);
        if (logger.isDebugEnabled()) {
            logger.debug("buildObjectName(objectName=" + objectName + "):" + result);
        }
        return result;
    }
    
    @Override
    public ObjectInstance registerMBean(Object object, ObjectName name) throws InstanceAlreadyExistsException, MBeanRegistrationException,
        NotCompliantMBeanException {
        try {
            name = buildObjectName(name);
        } catch (MalformedObjectNameException e) {
            throw new MBeanRegistrationException(e);
        }
        return super.registerMBean(object, name);
    }
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
