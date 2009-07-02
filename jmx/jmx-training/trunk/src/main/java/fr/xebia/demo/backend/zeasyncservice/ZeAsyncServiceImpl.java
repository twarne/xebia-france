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
package fr.xebia.demo.backend.zeasyncservice;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ZeAsyncServiceImpl implements ZeAsyncService, InitializingBean {
    
    private final static Logger logger = Logger.getLogger(ZeAsyncServiceImpl.class);
    
    private ExecutorService executorService;
    
    private Random random = new Random();
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.executorService, "executorService can not be null");        
    }
    
    @Override
    public void doAsyncWork(long employeeId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(random.nextInt(200));
                } catch (InterruptedException e) {
                    logger.warn("non blocking " + e);
                }
            }
        };
        
        try {
            executorService.execute(runnable);
        } catch (RejectedExecutionException e) {
            logger.warn("Non blocking " + e);
        }
    }
    
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
