/*
 * Copyright 2007 Xebia and the original author or authors.
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
package fr.xebia.sample.springframework.jms;

import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class JmxAnnotedDefaultMessageListenerContainerTest extends AbstractDependencyInjectionSpringContextTests {

    protected MBeanServer mbeanServer;

    protected SpringFrameworkJmsSenderSample sampleSender;

    protected SampleListener sampleListener;

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "classpath:fr/xebia/sample/springframework/jms/beans-JmxAnnotedDefaultMessageListenerContainer.xml" };
    }

    public void setMbeanServer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
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
        Thread.sleep(3 * 1000);

        int actual = this.sampleListener.getReceivedMessagesCounter();
        assertEquals(numberOfMessages, actual);

        Set<ObjectInstance> objectInstances = this.mbeanServer.queryMBeans(new ObjectName("application:type=SpringJmsMessageListener,*"), null);
        assertEquals(1, objectInstances.size());

        ObjectName messageListenerObjectName = objectInstances.iterator().next().getObjectName();

        // DISPLAY ALL THE ATTRIBUTES
        System.out.println(messageListenerObjectName);
        MBeanInfo mbeanInfo = this.mbeanServer.getMBeanInfo(messageListenerObjectName);
        MBeanAttributeInfo[] mbeanAttributeInfos = mbeanInfo.getAttributes();
        for (MBeanAttributeInfo mbeanAttributeInfo : mbeanAttributeInfos) {
            System.out.println("\t" + mbeanAttributeInfo.getName() + "="
                    + this.mbeanServer.getAttribute(messageListenerObjectName, mbeanAttributeInfo.getName()));
        }

    }
}
