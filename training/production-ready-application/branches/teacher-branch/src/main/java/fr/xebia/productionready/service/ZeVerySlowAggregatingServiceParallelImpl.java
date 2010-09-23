/*
 * Copyright 2008-2010 Xebia and the original author or authors.
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
package fr.xebia.productionready.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import fr.xebia.productionready.backend.anotherveryslowservice.AnotherVerySlowService;
import fr.xebia.productionready.backend.zeveryslowservice.ZeVerySlowService;

@ManagedResource(objectName = "fr.xebia:service=ZeVerySlowAggregatingService,type=ZeVerySlowAggregatingServiceParallelImpl")
public class ZeVerySlowAggregatingServiceParallelImpl implements ZeVerySlowAggregatingService {

    private AnotherVerySlowService anotherVerySlowService;

    private ExecutorService anotherVerySlowServiceExecutor;

    private long timeoutInMillis = 2500;

    private ZeVerySlowService zeVerySlowService;

    private ExecutorService zeVerySlowServiceExecutor;

    @Override
    public String doWork(final long id) {

        Callable<String> zeVerySlowCommand = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return zeVerySlowService.find(id);
            }
        };
        Future<String> zeVerySlowResponse = zeVerySlowServiceExecutor.submit(zeVerySlowCommand);

        Callable<String> anotherVerySlowCommand = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return anotherVerySlowService.find(id);
            }
        };
        Future<String> anotherVerySlowResponse = anotherVerySlowServiceExecutor.submit(anotherVerySlowCommand);

        String result;
        try {
            result = zeVerySlowResponse.get(timeoutInMillis, TimeUnit.MILLISECONDS) + "\t-\t"
                    + anotherVerySlowResponse.get(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @ManagedAttribute
    public long getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public void setAnotherVerySlowService(AnotherVerySlowService anotherVerySlowService) {
        this.anotherVerySlowService = anotherVerySlowService;
    }

    public void setAnotherVerySlowServiceExecutor(ExecutorService anotherVerySlowServiceExecutor) {
        this.anotherVerySlowServiceExecutor = anotherVerySlowServiceExecutor;
    }

    public void setTimeoutInMillis(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    public void setZeVerySlowService(ZeVerySlowService zeVerySlowService) {
        this.zeVerySlowService = zeVerySlowService;
    }

    public void setZeVerySlowServiceExecutor(ExecutorService zeVerySlowServiceExecutor) {
        this.zeVerySlowServiceExecutor = zeVerySlowServiceExecutor;
    }
}
