package fr.xebia.sample.springframework.jms;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class RequestReplySpringFrameworkTest extends AbstractDependencyInjectionSpringContextTests {

    protected String[] getConfigLocations() {
        return new String[]{"classpath:fr/xebia/sample/springframework/jms/RequestReplySpringFrameworkTest.xml"};
    }

    RequestReplyClientInvoker requestReplyClientInvoker;

    RequestReplyServerMessageListener requestReplyServerMessageListener;

    public void testSimpleSendJmsMessage() throws Exception {

        int numberOfThreads = 10;
        final int numberOfMessages = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int threadIdx = 0; threadIdx < numberOfThreads; threadIdx++) {
            Runnable runnable = new Runnable() {

                public void run() {
                    for (int messageCounter = 0; messageCounter < numberOfMessages; messageCounter++) {

                        try {
                            String request = "hello world " + messageCounter;
                            Object reply = requestReplyClientInvoker.requestReply(request);
                            System.out.println("Request " + request);
                            System.out.println("Reply " + reply);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };

            executorService.execute(runnable);
        }
        executorService.shutdown();
        executorService.awaitTermination(3 * 60, TimeUnit.SECONDS);

        Thread.sleep(3 * 1000);
        int actual = requestReplyServerMessageListener.getReceivedMessagesCounter();
        assertEquals(numberOfMessages * numberOfThreads, actual);
    }

    public void setRequestReplyClientInvoker(RequestReplyClientInvoker requestReplyClientInvoker) {
        this.requestReplyClientInvoker = requestReplyClientInvoker;
    }

    public void setRequestReplyServerMessageListener(RequestReplyServerMessageListener requestReplyServerMessageListener) {
        this.requestReplyServerMessageListener = requestReplyServerMessageListener;
    }
}
