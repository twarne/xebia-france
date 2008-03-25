/*
 * Copyright 2002-2006 the original author or authors.
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
package fr.xebia.sample.springframework.jms.requestreply;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class RequestReplyServer {

    private final static Logger logger = Logger.getLogger(RequestReplyServer.class);

    protected AtomicInteger invocationsCounter = new AtomicInteger();

    public String sayHello(String message) throws JMSException {

        int counterValue = this.invocationsCounter.incrementAndGet();
        String result = "Hello " + message + ". Request# " + counterValue;

        logger.debug(result);

        return result;

    }

    public int getInvocationsCounter() {
        return this.invocationsCounter.get();
    }

}
