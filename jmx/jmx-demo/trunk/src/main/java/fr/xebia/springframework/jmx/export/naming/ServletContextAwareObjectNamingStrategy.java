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
package fr.xebia.springframework.jmx.export.naming;

import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.web.context.ServletContextAware;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ServletContextAwareObjectNamingStrategy implements ServletContextAware, ObjectNamingStrategy {
    
    private final static Logger logger = Logger.getLogger(ServletContextAwareObjectNamingStrategy.class);
    
    protected ObjectNamingStrategy objectNamingStrategy;
    
    protected ServletContext servletContext;
    
    @Override
    public ObjectName getObjectName(Object managedBean, String beanKey) throws MalformedObjectNameException {
        ObjectName objectName = this.objectNamingStrategy.getObjectName(managedBean, beanKey);
        Hashtable<String, String> table = new Hashtable<String, String>(objectName.getKeyPropertyList());
        table.put("path", this.servletContext.getContextPath());
        table.put("webApplication", this.servletContext.getServletContextName());
        ObjectName result = ObjectName.getInstance(objectName.getDomain(), table);
        if (logger.isDebugEnabled()) {
            logger.debug("getObjectName(managedBean=" + managedBean + ", beanKey=" + beanKey + "):" + result);
        }
        return result;
    }
    
    public void setObjectNamingStrategy(ObjectNamingStrategy objectNamingStrategy) {
        this.objectNamingStrategy = objectNamingStrategy;
    }
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
}
