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
package fr.xebia.springframework.jms.support.listener;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.jms.JmsException;
import org.springframework.jms.listener.AbstractJmsListeningContainer;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;

/**
 * <p>
 * Spring JMX-ification of <code>DefaultMessageListenerContainer</code> via subclassing.
 * </p>
 * <p>
 * Methods are just overridden if they are not <code>final</code>. Otherwise, the name is
 * prefixed by <code>container</code>. JMX descriptions are inspired of the javadoc of the
 * attributes and methods;
 * </p>
 * <p>
 * Implement {@link SelfNaming} to append the bean name to the object name
 * </p>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@ManagedResource(objectName = "application:type=SpringJmsMessageListener")
public class JmxAnnotedDefaultMessageListenerContainer extends DefaultMessageListenerContainer implements SelfNaming {

    @ManagedAttribute(description = "Level of caching that this listener container is allowed to apply "
            + "(CACHE_NONE = 0, CACHE_CONNECTION = 1, CACHE_SESSION = 2, CACHE_CONSUMER = 3)")
    @Override
    public int getCacheLevel() {
        return super.getCacheLevel();
    }

    @ManagedAttribute(description = "JMS client ID for the shared Connection created and used " + "by this container, if any")
    @Override
    public String getClientId() {
        return super.getClientId();
    }

    /**
     * Override {@link DefaultMessageListenerContainer#getActiveConsumerCount()} with another name
     * because it is <code>final</code>.
     * 
     * @see #getActiveConsumerCount()
     */
    @ManagedAttribute(description = "Return the number of currently active consumers. "
            + "This number will always be inbetween 'concurrentConsumers and "
            + "'maxConcurrentConsumers', but might be lower than 'scheduledConsumerCount' "
            + "(in case of some consumers being scheduled but not executed at the moment)")
    public int getContainerActiveConsumerCount() {
        return super.getActiveConsumerCount();
    }

    /**
     * Override {@link DefaultMessageListenerContainer#getConcurrentConsumers()} with another name
     * because it is <code>final</code>.
     * 
     * @see #getConcurrentConsumers()
     */
    @ManagedAttribute(description = "Return the 'concurrentConsumer' setting. "
            + "This returns the currently configured 'concurrentConsumers' value; "
            + " the number of currently scheduled/active consumers might differ.")
    public int getContainerConcurrentConsumers() {
        return super.getConcurrentConsumers();
    }

    /**
     * Combine names from {link {@link #getDestination()} and {@link #getDestinationName()}.
     * 
     * @throws JMSException
     * 
     * @see #getDestination()
     * @see #getDestinationName()
     */
    @ManagedAttribute(description = "Name of the destination to receive messages from")
    public String getContainerDestinationName() throws JMSException {
        String destinationName = null;
        if (super.getDestinationName() != null) {
            destinationName = super.getDestinationName();
        }
        if (super.getDestination() != null) {
            Destination destination = super.getDestination();
            if (destination instanceof Queue) {
                Queue queue = (Queue) destination;
                destinationName = "Queue: " + queue.getQueueName();
            } else if (destination instanceof Topic) {
                Topic topic = (Topic) destination;
                destinationName = "Topic: " + topic.getTopicName();
            } else {
                // should not occur
            }
        }
        return destinationName;
    }

    /**
     * Override {@link DefaultMessageListenerContainer#getMaxConcurrentConsumers()} with another
     * name because it is <code>final</code>.
     * 
     * @see #getMaxConcurrentConsumers()
     */
    @ManagedAttribute(description = "Return the 'maxConcurrentConsumer' setting. "
            + "This returns the currently configured 'maxConcurrentConsumers' value; "
            + "the number of currently scheduled/active consumers might differ.")
    public int getContainerMaxConcurrentConsumers() {
        return super.getMaxConcurrentConsumers();
    }

    /**
     * Override {@link DefaultMessageListenerContainer#getScheduledConsumerCount()} with another
     * name because it is <code>final</code>.
     * 
     * @see #getScheduledConsumerCount()
     */
    @ManagedAttribute(description = "Return the number of currently scheduled consumers. "
            + "This number will always be inbetween 'concurrentConsumers' and "
            + "'maxConcurrentConsumers', but might be higher than 'activeConsumerCount' "
            + "(in case of some consumers being scheduled but not executed at the moment).")
    public int getContainerScheduledConsumerCount() {
        return super.getScheduledConsumerCount();
    }

    @ManagedAttribute(description = "name of a durable subscription to create, if any.")
    @Override
    public String getDurableSubscriptionName() {
        return super.getDurableSubscriptionName();
    }

    @ManagedAttribute(description = "Limit for idle executions of a receive task")
    @Override
    public int getIdleTaskExecutionLimit() {
        return super.getIdleTaskExecutionLimit();
    }

    @ManagedAttribute(description = "Maximum number of messages to process in one task")
    @Override
    public int getMaxMessagesPerTask() {
        return super.getMaxMessagesPerTask();
    }

    @ManagedAttribute(description = "JMS message selector expression (or null if none)")
    @Override
    public String getMessageSelector() {
        return super.getMessageSelector();
    }

    /**
     * Compose <code>objectName</code> with <code>@ManagedResource#objectName</code> annotation and the bean name obtained via
     *                             <code>BeanNameAware</code> contract.
     * 
     * @see org.springframework.jmx.export.naming.SelfNaming#getObjectName()
     * @see ManagedResource#objectName()
     * @see BeanNameAware
     */
    public ObjectName getObjectName() throws MalformedObjectNameException {
        ManagedResource managedResource = JmxAnnotedDefaultMessageListenerContainer.class.getAnnotation(ManagedResource.class);
        String objectNameAsString = managedResource.objectName() + ",beanName=" + getBeanName();

        return ObjectName.getInstance(objectNameAsString);
    }

    @ManagedAttribute(description = "Acknowledgement mode for JMS sessions "
            + "(SESSION_TRANSACTED = 0, AUTO_ACKNOWLEDGE = 1, CLIENT_ACKNOWLEDGE = 2, DUPS_OK_ACKNOWLEDGE = 3")
    @Override
    public int getSessionAcknowledgeMode() {
        return super.getSessionAcknowledgeMode();
    }

    /**
     * Can NOT override {@link AbstractJmsListeningContainer#isActive()} because this method is
     * final. We use another name to jmx-annotate this attribute
     */
    @ManagedAttribute(description = "Return whether this container is currently active, "
            + "that is, whether it has been set up but not shut down yet.")
    public boolean isContainerActive() {
        return super.isActive();
    }

    /**
     * Override {@link AbstractJmsListeningContainer#isRunning()} with another name because this
     * method is <code>final</code>.
     * 
     * @see #isRunning()
     */
    @ManagedAttribute(description = "Return whether this container is currently running, "
            + "that is, whether it has been set up but not shut down yet.")
    public boolean isContainerRunning() {
        return super.isRunning();
    }

    @ManagedOperation(description = "Start this container")
    @Override
    public void start() throws JmsException {
        super.start();
    }

    @ManagedOperation(description = "Stop this container")
    @Override
    public void stop() throws JmsException {
        super.stop();
    }
}
