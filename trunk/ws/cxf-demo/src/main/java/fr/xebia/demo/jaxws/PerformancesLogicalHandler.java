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
package fr.xebia.demo.jaxws;

import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

/**
 * <p>
 * Demo {@link Handler}.
 * </p>
 * <p>
 * This handler illustrates how awkward it is to wrap an invocation (much more than with a {@link javax.servlet} ):
 * </p>
 * <ul>
 * <li>Discover invocation direction with {@link MessageContext#MESSAGE_OUTBOUND_PROPERTY}.</li>
 * <li>Store and then retrieve variables in the {@link MessageContext}</li>
 * </ul>
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 */
public class PerformancesLogicalHandler implements LogicalHandler<LogicalMessageContext> {

    public void close(MessageContext context) {

    }

    public boolean handleFault(LogicalMessageContext context) {
        return true;
    }

    public boolean handleMessage(LogicalMessageContext context) {

        QName wsdlOperation = (QName) context.get(LogicalMessageContext.WSDL_OPERATION);

        boolean isOutbound = ((Boolean) context.get(LogicalMessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        if (isOutbound) {
            try {
                Date startTime = (Date) context.get(PerformancesLogicalHandler.class + ".StartTime");
                Date endTime = new Date();
                long duration = endTime.getTime() - startTime.getTime();
                System.out.println(PerformancesLogicalHandler.class + " : Service time in " + wsdlOperation + "=" + duration + " ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Date startTime = new Date();
            context.put(PerformancesLogicalHandler.class + ".StartTime", startTime);
        }
        return true;
    }

}
