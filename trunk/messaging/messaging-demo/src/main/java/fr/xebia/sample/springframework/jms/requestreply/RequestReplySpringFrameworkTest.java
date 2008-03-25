package fr.xebia.sample.springframework.jms.requestreply;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:fr/xebia/sample/springframework/jms/requestreply/beans.xml"})
public class RequestReplySpringFrameworkTest {

    @Autowired
    RequestReplyClientInvoker requestReplyClientInvoker;

    @Autowired
    RequestReplyServer requestReplyServer;

    @Test
    public void testSimpleSendJmsMessage() throws Exception {

        int numberOfThreads = 10;
        final int numberOfMessages = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads * numberOfMessages);

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int threadIdx = 0; threadIdx < numberOfThreads; threadIdx++) {
            final int idx = threadIdx;
            Runnable runnable = new Runnable() {

                public void run() {
                    for (int messageCounter = 0; messageCounter < numberOfMessages; messageCounter++) {

                        try {
                            String request = "hello world " + idx + "-" + messageCounter;
                            Object reply = requestReplyClientInvoker.requestReply(request);
                            System.out.println("Request " + request);
                            System.out.println("Reply " + reply);
                            countDownLatch.countDown();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };

            executorService.execute(runnable);
        }

        boolean allMessagesSent = countDownLatch.await(numberOfThreads * numberOfMessages, TimeUnit.DAYS);

        Assert.assertTrue("All messages where not processed, " + countDownLatch.getCount() + " remaining", allMessagesSent);

        int actual = requestReplyServer.getInvocationsCounter();
        Assert.assertEquals(numberOfMessages * numberOfThreads, actual);

        executorService.shutdownNow();

    }
}
