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
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;

/**
 * Util class for Stress tests
 *
 * @author <a href="mailto:cleclerc@pobox.com">Cyrille Le Clerc </a>
 */
public final class StressTestUtils {

    private static final int DASH_PER_LINE = 30;

    private static final StressTestUtils instance = new StressTestUtils();

    private static final String SYMBOL_FAILURE = "x";

    private static final String SYMBOL_SUCCESS = "-";

    /**
     * Outputs a dash in SystemOut and line breaks when necessary
     */
    public static void incrementProgressBarFailure() {
        instance.incrementProgressBar(SYMBOL_FAILURE);
    }

    /**
     * Outputs a dash in SystemOut and line breaks when necessary
     */
    public static void incrementProgressBarSuccess() {
        instance.incrementProgressBar(SYMBOL_SUCCESS);
    }

    public static void writeProgressBarLegend() {
        System.out.println();
        System.out.println(SYMBOL_SUCCESS + " : Success");
        System.out.println(SYMBOL_FAILURE + " : Failure");
    }

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private long lastSampleTime;

    private AtomicInteger progressBarCounter = new AtomicInteger();

    /**
     * Private contructor for singleton class
     */
    private StressTestUtils() {
        super();
    }

    public static long getLastSampleTime() {
        return instance.lastSampleTime;
    }

    public static int getProgressBarCounter() {
        return instance.progressBarCounter.get();
    }

    /**
     * <p>
     * Outputs a dash in SystemOut and line breaks when necessary
     * </p>
     */
    protected synchronized void incrementProgressBar(String symbol) {
        if (this.lastSampleTime == 0) {
            this.lastSampleTime = System.currentTimeMillis();
        }
        System.out.print(symbol);
        progressBarCounter.incrementAndGet();

        if (progressBarCounter.get() % DASH_PER_LINE == 0) {
            System.out.println();
            long now = System.currentTimeMillis();
            long elapsedDuration = now - this.lastSampleTime;
            String throughput;
            if (elapsedDuration == 0) {
                throughput = "infinite";
            } else {
                long throughputAsInt = (DASH_PER_LINE * 1000) / elapsedDuration;
                throughput = String.valueOf(throughputAsInt);
            }
            System.out.print("[" + this.dateFormat.format(new Date()) + " " + StringUtils.leftPad(throughput + "", 4) + " req/s]\t");
            this.lastSampleTime = System.currentTimeMillis();
        }
    }
}