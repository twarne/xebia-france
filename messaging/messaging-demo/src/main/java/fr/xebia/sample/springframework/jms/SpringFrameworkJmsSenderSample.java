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

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

/**
 * SpringFramework {@link JmsTemplate} based JMS Message sender sample.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class SpringFrameworkJmsSenderSample {

    /**
     * <code>MessagePostProcessor</code> in charge of holding a reference on the sent message.
     * <p>
     * Useful to retrieve the JMS properties that has been genertated by the JMS Provider (e.g.
     * jmsMessageID ...)
     */
    public static class ReferenceHolderMessagePostProcessor implements MessagePostProcessor {
        protected Message sentMessage;

        /**
         * Return the sent message.
         */
        public Message getSentMessage() {
            return this.sentMessage;
        }

        public Message postProcessMessage(Message message) throws JMSException {
            // keep a reference on the messge
            this.sentMessage = message;
            return message;
        }

    }

    protected JmsTemplate jmsTemplate;

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Send a JMS Message with the given <code>message</code> and retrieves the generated
     * JMSMessageID.
     */
    public void simpleSend(Object message) throws Exception {

        ReferenceHolderMessagePostProcessor messagePostProcessor = new ReferenceHolderMessagePostProcessor();
        this.jmsTemplate.convertAndSend(message, messagePostProcessor);

        Message sentMessage = messagePostProcessor.getSentMessage();
        System.out.println("Generated JMSMessageID" + sentMessage.getJMSMessageID());
    }
}