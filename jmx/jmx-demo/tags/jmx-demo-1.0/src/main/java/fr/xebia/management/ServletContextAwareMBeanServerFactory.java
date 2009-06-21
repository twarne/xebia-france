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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * <p></p>
 * <p>
 * Don't extend {@link AbstractFactoryBean} due to <a href="http://jira.springframework.org/browse/SPR-4968">SPR-4968 : Error
 * "Singleton instance not initialized yet" triggered by toString call in case of circular references</a>
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ServletContextAwareMBeanServerFactory implements FactoryBean, ServletContextAware, InitializingBean {
    
    private final static Logger logger = Logger.getLogger(ServletContextAwareMBeanServerFactory.class);
    
    protected MBeanServer instance;
    
    protected MBeanServer mbeanServer;
    
    protected ServletContext servletContext;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.mbeanServer, "mbeanServer can NOT be null");
        Assert.notNull(this.servletContext, "servletContext can NOT be null");
    }
    
    /**
     * <p>Copy the given <code>objectName</code> adding a "path" property  </p>
     * <p>Limit {@link ServletContext} attributes to {@link ServletContext#getContextPath()} to ease Hyperic configuration.</p>
     * 
     * @param objectName
     * @return
     * @throws MalformedObjectNameException
     */
    protected ObjectName buildObjectName(ObjectName objectName) throws MalformedObjectNameException {
        Hashtable<String, String> table = new Hashtable<String, String>(objectName.getKeyPropertyList());
        table.put("path", this.servletContext.getContextPath());
        
        ObjectName result = ObjectName.getInstance(objectName.getDomain(), table);
        if (logger.isTraceEnabled()) {
            logger.trace("buildObjectName(objectName=" + objectName + "):" + result);
        }
        return result;
    }
    
    @Override
    public Object getObject() throws Exception {
        if (instance == null) {
            InvocationHandler invocationHandler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Object[] modifiedArgs = args.clone();
                    for (int i = 0; i < modifiedArgs.length; i++) {
                        Object arg = modifiedArgs[i];
                        if (arg instanceof ObjectName) {
                            ObjectName objectName = (ObjectName)arg;
                            modifiedArgs[i] = buildObjectName(objectName);
                        }
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(method + " : " + Arrays.asList(modifiedArgs));
                    }
                    try {
                        return method.invoke(mbeanServer, modifiedArgs);
                    } catch (InvocationTargetException ite) {
                        throw ite.getCause();
                    }
                }
            };
            instance = (MBeanServer)Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class[] {
                MBeanServer.class
            }, invocationHandler);
        }
        return instance;
    }
    
    @Override
    public Class<? extends MBeanServer> getObjectType() {
        return MBeanServer.class;
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
    public void setMbeanServer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }
    
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
