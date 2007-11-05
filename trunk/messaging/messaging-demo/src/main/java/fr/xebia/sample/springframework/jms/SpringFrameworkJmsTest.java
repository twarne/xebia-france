package fr.xebia.sample.springframework.jms;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class SpringFrameworkJmsTest extends AbstractDependencyInjectionSpringContextTests {

    protected SpringFrameworkJmsSenderSample sampleSender;

    protected SampleListener sampleListener;

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "classpath:fr/xebia/sample/springframework/jms/beans.xml" };
    }

    public void setSampleListener(SampleListener sampleListener) {
        this.sampleListener = sampleListener;
    }

    public void setSampleSender(SpringFrameworkJmsSenderSample sampleSender) {
        this.sampleSender = sampleSender;
    }

    public void testSimpleSendJmsMessage() throws Exception {

        int numberOfMessages = 100;
        for (int i = 0; i < numberOfMessages; i++) {
            this.sampleSender.simpleSend("hello world");
        }
        Thread.sleep(5 * 1000);

        int actual = this.sampleListener.getReceivedMessagesCounter();
        assertEquals(numberOfMessages, actual);
    }
}
