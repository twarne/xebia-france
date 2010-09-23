/*
 * Copyright 2008-2009 Xebia and the original author or authors.
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
package fr.xebia.productionready.backend.zeslowservice;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName = "fr.xebia:service=ZeSlowService,type=ZeSlowServiceBoundedImpl")
public class ZeSlowServiceBoundedImpl implements ZeSlowService {

    private ZeSlowService zeSlowService;

    @Override
    public ZeSlowPerson find(long id) {

        return zeSlowService.find(id);

    }

    @ManagedAttribute
    public int getAvailablePermits() {
        return 0;
    }

    @ManagedAttribute
    public int getInvocationCount() {
        return 0;
    }

    @ManagedAttribute
    public int getMaxConcurrentInvocations() {
        return 0;
    }

    @ManagedAttribute
    public int getRejectedInvocationCount() {
        return 0;
    }

    public long getSemaphoreAcquireTimeoutInMillis() {
        return 0;
    }

    @ManagedAttribute
    public void setMaxConcurrentInvocations(int maxConcurrentInvocations) {

    }

    public void setSemaphoreAcquireTimeoutInMillis(long semaphoreAcquireTimeoutInMillis) {
    }

    public void setZeSlowService(ZeSlowService zeSlowService) {
        this.zeSlowService = zeSlowService;
    }

}
