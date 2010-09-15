import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.util.ClassUtils;

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

/**
 * 
 */
public class MoneyTransfer {

    final private AtomicInteger activeCount = new AtomicInteger();
    final private AtomicInteger invocationCount = new AtomicInteger();
    final private AtomicInteger overThresholdInvocationCount = new AtomicInteger();
    final private AtomicInteger otherExceptionCount = new AtomicInteger();
    final private AtomicInteger timeoutExceptionCount = new AtomicInteger();
    final private AtomicLong totalDurationInNanos = new AtomicLong();

    private int durationThresholdInNanos = 300 * 1000 * 1000; // 300 ms

    private final static Logger performanceLogger = LoggerFactory.getLogger("fr.xebia.performance."
            + ClassUtils.getShortName(MoneyTransfer.class));

    public void doJob() {
        String meaningFullParameters = "";
        String meaningFillSubDurations = "";

        long nanosBefore = System.nanoTime();
        activeCount.incrementAndGet();
        try {
            someWork();
        } catch (SocketTimeoutException e) {
            timeoutExceptionCount.incrementAndGet();
        } catch (IOException e) {
            otherExceptionCount.incrementAndGet();
        } catch (RuntimeException e) {
            otherExceptionCount.incrementAndGet();
        } finally {
            activeCount.decrementAndGet();
            invocationCount.incrementAndGet();
            long durationInNanos = System.nanoTime() - nanosBefore;
            totalDurationInNanos.addAndGet(durationInNanos);
            if (durationInNanos >= durationThresholdInNanos) {
                overThresholdInvocationCount.incrementAndGet();
                performanceLogger.info("doJob(" + meaningFullParameters + ") " + meaningFillSubDurations + " took "
                        + TimeUnit.MILLISECONDS.convert(durationInNanos, TimeUnit.NANOSECONDS) + " ms");
            }
        }
    }

    @ManagedAttribute
    public AtomicInteger getInvocationCount() {
        return invocationCount;
    }

    public AtomicInteger getOtherExceptionCount() {
        return otherExceptionCount;
    }

    public AtomicInteger getTimeoutExceptionCount() {
        return timeoutExceptionCount;
    }

    private void someWork() throws IOException {
        // TODO Auto-generated method stub

    }

}
