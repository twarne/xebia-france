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
package fr.xebia.demo.objectgrid.ticketing;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class RouteDetails implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected String arrivalStationCode;
    protected Date arrivalTime;
    protected String departureStationCode;
    protected Date departureTime;
    protected BigInteger price;
    protected String trainCode;
    protected long trainId;
    protected Train.Type trainType;
    
    public String getArrivalStationCode() {
        return arrivalStationCode;
    }
    
    public Date getArrivalTime() {
        return arrivalTime;
    }
    
    public String getDepartureStationCode() {
        return departureStationCode;
    }
    
    public Date getDepartureTime() {
        return departureTime;
    }
    
    public BigInteger getPrice() {
        return price;
    }
    
    public String getTrainCode() {
        return trainCode;
    }
    
    public long getTrainId() {
        return trainId;
    }
    
    public Train.Type getTrainType() {
        return trainType;
    }
    
    public void setArrivalStationCode(String arrivalStationCode) {
        this.arrivalStationCode = arrivalStationCode;
    }
    
    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public void setDepartureStationCode(String departureStationCode) {
        this.departureStationCode = departureStationCode;
    }
    
    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }
    
    public void setPrice(BigInteger price) {
        this.price = price;
    }
    
    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }
    
    public void setTrainId(long trainId) {
        this.trainId = trainId;
    }
    
    public void setTrainType(Train.Type trainType) {
        this.trainType = trainType;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("trainId", this.trainId).append("trainCode", this.trainCode)
            .append("departureStationCode", this.departureStationCode).append("departureTime", this.departureTime)
            .append("arrivalStationCode", this.arrivalStationCode).append("arrivalTime", this.arrivalTime).append("trainType",
                                                                                                                  this.trainType)
            .append("price", this.price).toString();
    }
}
