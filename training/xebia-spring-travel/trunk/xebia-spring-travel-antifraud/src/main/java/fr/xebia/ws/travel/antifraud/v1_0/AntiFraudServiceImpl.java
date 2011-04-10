/*
 * Copyright 2008-2010 Xebia and the original author or authors.
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
package fr.xebia.ws.travel.antifraud.v1_0;

import java.io.StringWriter;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.appdynamics.apm.appagent.api.AgentDelegate;
import com.appdynamics.apm.appagent.api.ITransactionDemarcator;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;

import fr.xebia.management.statistics.Profiled;

@ManagedResource
public class AntiFraudServiceImpl implements AntiFraudService {

    private ITransactionDemarcator appDynamicsTransactionDemarcator = AgentDelegate.getTransactionDemarcator();

    private final Logger auditLogger = LoggerFactory.getLogger("fr.xebia.audit.AntiFraudService");

    private final Random random = new Random();

    private int slowRequestMinimumDurationInMillis = 2000;

    private int slowRequestRatioInPercent = 0;

    private int suspiciousBookingRatioInPercent = 0;

    private XStream xstream = new XStream();

    @Profiled(slowInvocationThresholdInMillis = 1000, verySlowInvocationThresholdInMillis = 2000)
    @Override
    public String checkBooking(Booking booking) throws SuspiciousBookingException {
        try {
            randomlySlowRequest();
            randomlyThrowException();
            String result = "txid-" + Math.abs(random.nextLong());

            auditLogger.info(appDynamicsTransactionDemarcator.getUniqueIdentifierForTransaction() + " - Authorize booking "
                    + toXmlString(booking) + " " + result);

            return result;
        } catch (SuspiciousBookingException e) {
            auditLogger.error(appDynamicsTransactionDemarcator.getUniqueIdentifierForTransaction() + " - Reject booking "
                    + toXmlString(booking) + " " + e);
            throw e;
        } catch (RuntimeException e) {
            auditLogger.error(appDynamicsTransactionDemarcator.getUniqueIdentifierForTransaction() + " - Exception checking booking "
                    + toXmlString(booking) + " " + e);
            throw e;
        }
    }
    
    @ManagedAttribute
    public int getSlowRequestMinimumDurationInMillis() {
        return slowRequestMinimumDurationInMillis;
    }

    @ManagedAttribute
    public int getSlowRequestRatioInPercent() {
        return slowRequestRatioInPercent;
    }

    @ManagedAttribute
    public int getSuspiciousBookingRatioInPercent() {
        return suspiciousBookingRatioInPercent;
    }

    protected void randomlySlowRequest() {

        long sleepDurationInMillis = random.nextInt(200);

        if (slowRequestRatioInPercent == 0) {
        } else if (0 == random.nextInt(100 / slowRequestRatioInPercent)) {
            sleepDurationInMillis += slowRequestMinimumDurationInMillis;
        }
        try {
            Thread.sleep(sleepDurationInMillis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

    }

    protected void randomlyThrowException() throws SuspiciousBookingException {
        if (suspiciousBookingRatioInPercent == 0) {
            return;
        } else if (0 == random.nextInt(100 / suspiciousBookingRatioInPercent)) {
            SuspiciousBookingFault suspiciousBookingFault = new SuspiciousBookingFault();
            suspiciousBookingFault.setMessage("Suspicious booking");
            throw new SuspiciousBookingException("Suspicious booking", suspiciousBookingFault);
        }
    }

    @ManagedAttribute
    public void setSlowRequestMinimumDurationInMillis(int slowRequestMinimumDurationInMillis) {
        this.slowRequestMinimumDurationInMillis = slowRequestMinimumDurationInMillis;
    }

    @ManagedAttribute
    public void setSlowRequestRatioInPercent(int slowRequestRatioInPercent) {
        this.slowRequestRatioInPercent = slowRequestRatioInPercent;
    }

    @ManagedAttribute
    public void setSuspiciousBookingRatioInPercent(int suspiciousBookingRatio) {
        this.suspiciousBookingRatioInPercent = suspiciousBookingRatio;
    }

    protected String toXmlString(Object o) {
        StringWriter writer = new StringWriter();
        xstream.marshal(o, new CompactWriter(writer));
        return writer.toString();
    }
}
