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
import java.util.Random;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class ZeBuggyServiceImpl implements ZeBuggyService {
    
    private Random random = new Random();
    
    @Override
    public ZeBuggyPerson find(long id) throws ZeBuggyServiceException, ZeBuggyServiceRuntimeException, RuntimeException {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        switch (random.nextInt(15)) {
            case 0:
                throw new NullPointerException("buggy code happens");
            case 1:
                throw new RuntimeException(new SocketTimeoutException("Connect timed out"));
            case 2:
                throw new RuntimeException(new SocketTimeoutException("Receive timed out"));
            case 3:
                throw new ZeBuggyServiceRuntimeException(new SocketTimeoutException("Connect timed out"));
            case 4:
                throw new ZeBuggyServiceRuntimeException(new SocketTimeoutException("Receive timed out"));
            case 5:
                throw new ZeBuggyServiceException("Buggy id '" + id + "'");
            case 6:
                return null;
            default:
                return new ZeBuggyPerson("John", "Buggy Doe-" + id);
        }
    }
}
