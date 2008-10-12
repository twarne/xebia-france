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

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.FaultMode;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * Log outgoing exceptions. If {@link Logger#isDebugEnabled()} is true, the exception's stack trace is outputted in the logs ; otherwise, a toString()
 * of the exception is logged.
 * </p>
 * <p>
 * </p>
 * <p>
 * Log4j logging {@link Level} according to the Message {@link FaultMode}
 * </p>
 * <table border="1">
 * <tr>
 * <th>org.apache.cxf.message.FaultMode</th>
 * <th>org.apache.log4j.Level</th>
 * </tr>
 * <tr>
 * <td>FaultMode.UNCHECKED_APPLICATION_FAULT <br/>
 * FaultMode.LOGICAL_RUNTIME_FAULT <br/>
 * FaultMode.RUNTIME_FAULT</td>
 * <td>Level.ERROR</td>
 * </tr>
 * <tr>
 * <td>FaultMode.CHECKED_APPLICATION_FAULT</td>
 * <td>Level.INFO</td>
 * </tr>
 * <tr>
 * <td>other</td>
 * <td>Level.INFO</td>
 * </tr>
 * </table>
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class FaultLoggingOutInterceptor extends AbstractPhaseInterceptor<Message> {
    
    private final Logger logger = Logger.getLogger(FaultLoggingOutInterceptor.class);
    
    public FaultLoggingOutInterceptor() {
        super(Phase.PRE_STREAM);
    }
    
    @Override
    public void handleMessage(Message message) throws Fault {
        FaultMode mode = message.get(FaultMode.class);
        Fault fault = (Fault)message.getContent(Exception.class);
        String msg = "Exception invoking " + message.getExchange().get(OperationInfo.class);
        
        Level level;
        if (FaultMode.UNCHECKED_APPLICATION_FAULT.equals(mode) || FaultMode.LOGICAL_RUNTIME_FAULT.equals(mode)
            || FaultMode.RUNTIME_FAULT.equals(mode)) {
            level = Level.ERROR;
        } else {
            level = Level.INFO;
        }
        Throwable cause = fault.getCause() == null ? fault : fault.getCause();
        if (logger.isDebugEnabled()) {
            logger.log(level, msg, cause);
        } else {
            logger.log(level, msg + " : " + cause);
        }
    }
}
