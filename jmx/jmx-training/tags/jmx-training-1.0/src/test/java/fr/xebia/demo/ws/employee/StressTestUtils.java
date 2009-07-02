/*
 * Created on Nov 12, 2004
 */
package fr.xebia.demo.ws.employee;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

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
    public static void incrementProgressBarFailure(String symbol) {
        instance.incrementProgressBar(symbol);
    }

    /**
     * Outputs a dash in SystemOut and line breaks when necessary
     */
    public static void incrementProgressBarSuccess() {
        instance.incrementProgressBar(SYMBOL_SUCCESS);
    }

    private long lastSampleTime;

    private int progressBarCounter;

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
        return instance.progressBarCounter;
    }

    /**
     * <p>
     * Outputs a dash in SystemOut and line breaks when necessary
     * </p>
     */
    public synchronized void incrementProgressBar(String symbol) {
        if (this.lastSampleTime == 0) {
            this.lastSampleTime = System.currentTimeMillis();
        }
        System.out.print(symbol);
        this.progressBarCounter++;

        if (this.progressBarCounter % DASH_PER_LINE == 0) {
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
            System.out.print("[" + DateFormatUtils.format(System.currentTimeMillis(), "HH:mm:ss") + " " + StringUtils.leftPad(throughput + "", 4) + " req/s]\t");
            this.lastSampleTime = System.currentTimeMillis();
        }
    }
}
