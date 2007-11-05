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

import java.util.Date;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

/**
 * {@link MessageListener} basic sample.
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class SampleListener implements MessageListener {

    private final static Logger logger = Logger.getLogger(SampleListener.class);

    protected int receivedMessagesCounter;

    public void onMessage(Message message) {

        logger.debug("> SampleListener.onMessage");
        logger.debug(message);
        this.receivedMessagesCounter++;
        logger.debug(new Date() + "< SampleListener.onMessage");
    }

    public int getReceivedMessagesCounter() {
        return this.receivedMessagesCounter;
    }
}