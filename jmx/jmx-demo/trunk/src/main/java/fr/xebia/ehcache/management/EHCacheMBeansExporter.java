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
package fr.xebia.ehcache.management;

import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.management.Cache;
import net.sf.ehcache.management.CacheManager;
import net.sf.ehcache.management.ManagementService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.util.Assert;

/**
 * Spring Framework's {@link MBeanExporter} based EHCache MBeans service. Inspired from {@link ManagementService}.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class EHCacheMBeansExporter implements InitializingBean, CacheManagerEventListener {
    
    private static final Logger logger = Logger.getLogger(EHCacheMBeansExporter.class);
    
    private net.sf.ehcache.CacheManager cacheManager;
    
    private MBeanExporter mbeanExporter;
    
    private Status status;
    
    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.cacheManager, "cacheManager can NOT be null");
        Assert.notNull(this.mbeanExporter, "mbeanExporter can NOT be null");
        
        CacheManager manageableCacheManager = new CacheManager(cacheManager);
        
        mbeanExporter.registerManagedResource(manageableCacheManager, ObjectName.getInstance("net.sf.ehcache:CacheManager="
                                                                                             + cacheManager.getName()));
        
        List<Cache> caches = manageableCacheManager.getCaches();
        for (Cache cache : caches) {
            registerCache(cache);
        }
        status = Status.STATUS_ALIVE;
        cacheManager.getCacheManagerEventListenerRegistry().registerListener(this);
    }
    
    /**
     * NO OP method as {@link MBeanExporter} does not offer an MBean unregistration method. Unregistration is performed by
     * {@link MBeanExporter#destroy()}.
     */
    public void dispose() throws CacheException {
        status = Status.STATUS_SHUTDOWN;
    }
    
    public Status getStatus() {
        return status;
    }
    
    /**
     * Business logic moved to {@link #afterPropertiesSet()}
     */
    @Override
    public void init() throws CacheException {
        status = Status.STATUS_ALIVE;
    }
    
    public void notifyCacheAdded(String cacheName) {
        Cache cache = new Cache(cacheManager.getCache(cacheName));
        registerCache(cache);
    }
    
    /**
     * NO OP method as {@link MBeanExporter} does not offer an MBean unregistration method.
     */
    public void notifyCacheRemoved(String cacheName) {
        
    }
    
    private void registerCache(Cache cache) {
        String baseObjectName = "net.sf.ehcache:CacheManager=" + cacheManager.getName() + ",name=" + ObjectName.quote(cache.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("Register MBeans for cache '" + cache.getName() + "' with base ObjectName " + baseObjectName);
        }
        try {
            mbeanExporter.registerManagedResource(cache.getCacheConfiguration(), ObjectName.getInstance(baseObjectName
                                                                                                        + ",type=CacheConfiguration"));
            mbeanExporter.registerManagedResource(cache.getStatistics(), ObjectName.getInstance(baseObjectName + ",type=CacheStatistics"));
            mbeanExporter.registerManagedResource(cache, ObjectName.getInstance(baseObjectName + ",type=Cache"));
        } catch (MalformedObjectNameException e) {
            throw new IllegalStateException("Unexpected exception getting ObjectName for cache " + cache.getName());
        }
    }
    
    public void setCacheManager(net.sf.ehcache.CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    public void setMbeanExporter(MBeanExporter mbeanExporter) {
        this.mbeanExporter = mbeanExporter;
    }
}
