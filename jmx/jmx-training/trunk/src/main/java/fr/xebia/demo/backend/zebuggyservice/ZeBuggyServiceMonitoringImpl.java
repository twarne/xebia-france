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
package fr.xebia.demo.backend.zebuggyservice;

import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@ManagedResource(objectName = "fr.xebia.jmx:type=ZeBuggyService")
public class ZeBuggyServiceMonitoringImpl implements ZeBuggyService {
    
    final private AtomicInteger invocationsCount = new AtomicInteger();
    
    final private AtomicLong invocationsDurationInMillis = new AtomicLong();
    
    final private AtomicInteger invocationsOtherRuntimeExceptionCount = new AtomicInteger();
    
    final private AtomicInteger invocationsOtherZeBuggyServiceExceptionCount = new AtomicInteger();
    
    final private AtomicInteger invocationsTimeoutExceptionCount = new AtomicInteger();
    
    final private AtomicInteger numActive = new AtomicInteger();
    
    private ZeBuggyService zeBuggyService;
    
    @Override
    public ZeBuggyPerson find(long id) throws ZeBuggyServiceException, ZeBuggyServiceRuntimeException, RuntimeException {
        long startTime = System.currentTimeMillis();
        numActive.incrementAndGet();
        try {
            return zeBuggyService.find(id);
        } catch (RuntimeException e) {
            if (ExceptionUtils.indexOfThrowable(e, SocketTimeoutException.class) != -1) {
                invocationsTimeoutExceptionCount.incrementAndGet();
            } else {
                invocationsOtherRuntimeExceptionCount.incrementAndGet();
            }
            throw e;
        } catch (ZeBuggyServiceException e) {
            if (ExceptionUtils.indexOfThrowable(e, SocketTimeoutException.class) != -1) {
                invocationsTimeoutExceptionCount.incrementAndGet();
            } else {
                invocationsOtherZeBuggyServiceExceptionCount.incrementAndGet();
            }
            throw e;
        } finally {
            numActive.decrementAndGet();
            invocationsCount.incrementAndGet();
            invocationsDurationInMillis.addAndGet(System.currentTimeMillis() - startTime);
        }
    }
    
    @ManagedAttribute(description = "Number of invocations (trendsup)")
    public int getInvocationsCount() {
        return invocationsCount.get();
    }
    
    @ManagedAttribute(description = "Total duration of the invocations in millis (trendsup)")
    public long getInvocationsDurationInMillis() {
        return invocationsDurationInMillis.get();
    }
    
    @ManagedAttribute(description = "Number of invocations that raised unclassified runtime exceptions (trendsup)")
    public int getInvocationsOtherRuntimeExceptionCount() {
        return invocationsOtherRuntimeExceptionCount.get();
    }
    
    @ManagedAttribute(description = "Number of invocations that raised unclassified ZeBuggyService exceptions (trendsup)")
    public int getInvocationsOtherZeBuggyServiceExceptionCount() {
        return invocationsOtherZeBuggyServiceExceptionCount.get();
    }
    
    @ManagedAttribute(description = "Number of invocations that raised timeout exceptions (trendsup)")
    public int getInvocationsTimeoutExceptionCount() {
        return invocationsTimeoutExceptionCount.get();
    }
    
    @ManagedAttribute(description = "Number of active invocations to Ze Buggy Service")
    public int getNumActive() {
        return numActive.get();
    }
    
    public void setZeBuggyService(ZeBuggyService zeBuggyService) {
        this.zeBuggyService = zeBuggyService;
    }
    
}
