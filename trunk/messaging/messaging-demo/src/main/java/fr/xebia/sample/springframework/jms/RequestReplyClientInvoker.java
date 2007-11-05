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
package fr.xebia.sample.springframework.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang.Validate;
import org.springframework.core.style.ToStringCreator;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * <p>
 * SpringFramework {@link JmsTemplate} based Request/Reply client invoker.
 * </p>
 * <p>
 * Note that this request-reply does not support temporary queues because temporary queues lifetime does not fit well with
 * {@link JmsTemplate}'s JCA based approach . Indeed, temporary queues have the lifetime of a connections when JMSTemplate eagerly closes
 * resources (Connection, Session, MessageProducer and MessageConsumer ) and is aimed to be used with an underlying JCA connector to pool
 * all these resources.
 * </p>
 * <p>
 * Extract from JMS 1.1 Specification :
 * </p>
 * <blockquote><strong> 4.4.3 Creating Temporary Destinations </strong> <br/> Although sessions are used to create temporary destinations,
 * this is only for convenience. Their scope is actually the entire connection. Their lifetime is that of their connection, and any of the
 * connection’s sessions is allowed to create a MessageConsumer for them. </blockquote>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class RequestReplyClientInvoker extends JmsTemplate {

    /**
     * Set the Message JMSReplyTo property and keep a reference on the sent {@link Message} to let retrieve the generated Message ID to
     * apply a MessageSelector to receive the reply.
     */
    public class RequestReplyMessagePostProcessor implements MessagePostProcessor {

        Message sentMessage;

        public RequestReplyMessagePostProcessor() {
            super();
        }

        public Message getSentMessage() {
            return this.sentMessage;
        }

        public Message postProcessMessage(Message message) throws JMSException {
            Validate.notNull(replyToDestination, "replyToDestination");
            message.setJMSReplyTo(replyToDestination);
            this.sentMessage = message;
            return message;
        }
    }

    protected Destination replyToDestination;

    /**
     * Request/Reply SpringFramework sample.
     * 
     * @param request
     *            sent to the remote service
     * @return reply returned by the remote service
     * @throws JMSException
     */
    public Object requestReply(Object request) throws JMSException {
        RequestReplyMessagePostProcessor messagePostProcessor = new RequestReplyMessagePostProcessor();

        convertAndSend(request, messagePostProcessor);

        Message sentMessage = messagePostProcessor.getSentMessage();
        System.out.println("jmsMessageId=" + sentMessage.getJMSMessageID());
        String messageSelector = "JMSCorrelationID  LIKE '" + sentMessage.getJMSMessageID() + "'";

        Object reply = receiveSelectedAndConvert(this.replyToDestination, messageSelector);

        logger.debug("Received message:" + reply);
        return reply;
    }

    public void setReplyToDestination(Destination replyToDestination) {
        this.replyToDestination = replyToDestination;
    }

    public void setRequestDestination(Destination requestDestination) {
        setDefaultDestination(requestDestination);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("defaultDestination", this.getDefaultDestination()).append("defaultDestinationName",
                this.getDefaultDestinationName()).append("replyToDestination", this.replyToDestination).toString();
    }
}