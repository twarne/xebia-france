/*
 * Copyright 2002-2008 the original author or authors.
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
package fr.xebia.cxf.interceptor;

import org.apache.cxf.interceptor.ClientOutFaultObserver;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.FaultMode;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * We rely on {@link Interceptor#handleFault(Message)} of a {@link Service#getOutInterceptors()} instead of
 * {@link Interceptor#handleMessage(Message)} of {@link Service#getOutFaultInterceptors()} because {@link ClientOutFaultObserver} swallows
 * {@link Interceptor#handleMessage(Message)} of the {@link Service#getOutFaultInterceptors()} for exceptions that occur on
 * {@link Phase#SEND} ("do nothing for exception occurred during client sending out request").
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class FaultEnhancerInterceptor extends AbstractPhaseInterceptor<Message> {
    
    private final Logger logger = Logger.getLogger(FaultEnhancerInterceptor.class);
    
    public FaultEnhancerInterceptor() {
        super(Phase.PRE_LOGICAL);
    }
    
    @Override
    public void handleMessage(Message message) throws Fault {
        // do nothing. This class only handles Faults
    }
    
    @Override
    public void handleFault(Message message) {
        Exchange exchange = message.getExchange();
        if (exchange == null) {
            // do nothing
        } else {
            Fault fault = (Fault)message.getContent(Exception.class);
            FaultMode mode = message.get(FaultMode.class);
            
            String msg = "Exception invoking service " + exchange.get(Message.WSDL_INTERFACE);
            if (exchange.get(OperationInfo.class) != null) {
                msg += ", operation=" + exchange.get(OperationInfo.class).getName();
            }
            if (exchange.get(Message.ENDPOINT_ADDRESS) != null) {
                msg += ", url=" + exchange.get(Message.ENDPOINT_ADDRESS);
            }
            Throwable cause = fault.getCause() == null ? fault : fault.getCause();
            
            Fault enhancedFault;
            Level level;
            if (FaultMode.UNCHECKED_APPLICATION_FAULT.equals(mode) || FaultMode.LOGICAL_RUNTIME_FAULT.equals(mode)
                || FaultMode.RUNTIME_FAULT.equals(mode)) {
                level = Level.ERROR;
                enhancedFault = new Fault(new RuntimeException(msg, cause));
            } else if (FaultMode.CHECKED_APPLICATION_FAULT.equals(mode)) {
                enhancedFault = fault;
                level = Level.INFO;
            } else /* mode == null */{
                enhancedFault = new Fault(new RuntimeException(msg, cause));
                level = Level.INFO;
            }
            if (logger.isDebugEnabled()) {
                logger.log(level, msg, cause);
            } else {
                logger.log(level, msg + " : " + cause);
            }
            
            message.setContent(Exception.class, enhancedFault);
            if (exchange.get(Exception.class) != null) {
                exchange.put(Exception.class, enhancedFault);
            }
        }
    }
}
