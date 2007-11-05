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
package fr.xebia.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

/**
 * <p>
 * {@link TopicPublisher} with support for {@link XmlMessage}.
 * </p>
 * 
 * <p>
 * If the {@link Message} is an instance of {@link XmlMessage}, maintains the CCSID in sync with the "encoding" attribute of the XML
 * document.
 * </p>
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class XmlAwareTopicPublisher extends XmlAwareMessageProducer implements TopicPublisher {

    protected TopicPublisher topicPublisher;

    public XmlAwareTopicPublisher(TopicPublisher topicPublisher, JmsCharsetHelper jmsCharsetHelper) {
        super(topicPublisher, jmsCharsetHelper);
        this.topicPublisher = topicPublisher;
    }

    public Topic getTopic() throws JMSException {
        return this.topicPublisher.getTopic();
    }

    public void publish(Message message) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(getTopic(), (XmlMessageImpl) message);
        }
        this.topicPublisher.publish(message);
    }

    public void publish(Topic topic, Message message) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(topic, (XmlMessageImpl) message);
        }
        this.topicPublisher.publish(topic, message);
    }

    public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(getTopic(), (XmlMessageImpl) message);
        }
        this.topicPublisher.publish(message, deliveryMode, priority, timeToLive);
    }

    public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (message instanceof XmlMessage) {
            message = prepareMessage(topic, (XmlMessageImpl) message);
        }
        this.topicPublisher.publish(topic, message, deliveryMode, priority, timeToLive);
    }

}
