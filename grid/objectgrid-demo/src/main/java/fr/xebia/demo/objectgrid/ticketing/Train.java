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
package fr.xebia.demo.objectgrid.ticketing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.ibm.websphere.objectgrid.em.annotations.Index;
import com.ibm.websphere.projector.annotations.Basic;
import com.ibm.websphere.projector.annotations.CascadeType;
import com.ibm.websphere.projector.annotations.Entity;
import com.ibm.websphere.projector.annotations.Id;
import com.ibm.websphere.projector.annotations.OneToMany;
import com.ibm.websphere.projector.annotations.Version;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@Entity(schemaRoot = true)
public class Train implements Serializable {
    
    public enum Type {
        HIGH_SPEED, NORMAL
    }
    
    private final static Logger logger = Logger.getLogger(Train.class);
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Train's business identifier
     */
    @Basic
    @Index
    protected String code;
    
    @Id
    protected int id;
    
    @OneToMany(cascade = CascadeType.ALL)
    protected List<Seat> seats = new ArrayList<Seat>();
    
    @OneToMany(cascade = CascadeType.ALL)
    protected List<TrainStop> trainStops = new ArrayList<TrainStop>();
    
    @Basic
    protected Type type;
    
    @Version
    protected int version;
    
    public Train() {
        super();
    }
    
    public Train(int id, String code, Type type) {
        super();
        this.id = id;
        this.code = code;
        this.type = type;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Train)) {
            return false;
        }
        final Train other = (Train)obj;
        return new EqualsBuilder().append(this.code, other.code).isEquals();
    }
    
    public int getAvailableSeatsCount() {
        int result = 0;
        for (Seat seat : this.seats) {
            if (seat.isBooked() == false) {
                result++;
            }
        }
        return result;
    }
    
    public String getCode() {
        return code;
    }
    
    public int getId() {
        return id;
    }
    
    public List<Seat> getSeats() {
        return seats;
    }
    
    public int getTotalSeatsCount() {
        return this.seats.size();
    }
    
    public List<TrainStop> getTrainStops() {
        return trainStops;
    }
    
    public Type getType() {
        return type;
    }
    
    public int getVersion() {
        return version;
    }
    
    /**
     * @return the first available {@link Seat} or <code>null</code> if no available Seat has been found.
     */
    public Seat getFirstAvailableSeat() {
        for (Seat seat : this.seats) {
            if (seat.isBooked() == false) {
                return seat;
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.code).toHashCode();
    }
    
    /**
     * Returns details of the route if the train matches the given route and has available seats; <code>null</code> otherwise.
     * 
     * @param departureStationCode
     * @param departureTime
     * @param arrivalStationCode
     * @return
     */
    public RouteDetails hasAvailableSeat(String departureStationCode, Date departureTime, String arrivalStationCode) {
        RouteDetails routeDetails = matchRoute(departureStationCode, departureTime, arrivalStationCode);
        if (routeDetails == null) {
            if (logger.isDebugEnabled())
                logger.debug("Train " + getCode() + " does not match route, return null");
            return null;
        }
        
        Seat seat = getFirstAvailableSeat();
        if (seat == null) {
            if (logger.isDebugEnabled())
                logger.debug("Train " + getCode() + " matches route but no available seat found, return null");
            return null;
        }
        routeDetails.setPrice(seat.getPrice());
        
        if (logger.isDebugEnabled()) {
            logger.debug("hasAvailableSeat(departureStationCode=" + departureStationCode + ",departureTime=" + departureTime
                         + ", arrivalStationCode=" + arrivalStationCode + ") " + routeDetails);
        }
        return routeDetails;
    }
    
    /**
     * Returns details of the route if the train matches the given route ; <code>null</code> otherwise.
     * 
     * @param departureStationCode
     * @param departureTimeLowerBound
     * @param departureTimeUpperBound
     * @param arrivalStationCode
     */
    public RouteDetails matchRoute(String departureStationCode, Date departureTime, String arrivalStationCode) {
        
        if (logger.isDebugEnabled()) {
            logger.debug("matchRoute(departureStationCode=" + departureStationCode + ", departureTime=" + departureTime
                         + ", arrivalStationCode=" + arrivalStationCode + ")");
        }
        
        Date departureTimeLowerBound = DateUtils.addHours(departureTime, -1);
        Date departureTimeUpperBound = DateUtils.addHours(departureTime, +1);
        
        Iterator<TrainStop> itTrainStops = this.trainStops.iterator();
        
        while (itTrainStops.hasNext()) {
            TrainStop firstTrainStop = itTrainStops.next();
            
            if (firstTrainStop.getStationCode().equals(departureStationCode)) {
                
                if (firstTrainStop.getDepartureDateTime().before(departureTimeLowerBound)
                    || firstTrainStop.getDepartureDateTime().after(departureTimeUpperBound)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Departure station '" + departureStationCode + "' found BUT departure time '"
                                     + firstTrainStop.getDepartureDateTime() + "' does NOT match");
                    }
                    return null;
                }
                while (itTrainStops.hasNext()) {
                    TrainStop secondTrainStop = itTrainStops.next();
                    if (secondTrainStop.getStationCode().equals(arrivalStationCode)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Train '" + this.code + "', departureTime '" + firstTrainStop.getDepartureDateTime() + "' from '"
                                         + firstTrainStop.getStationCode() + "' to '" + secondTrainStop.getStationCode()
                                         + "' matches the given route");
                        }
                        RouteDetails routeDetails = new RouteDetails();
                        routeDetails.setTrainId(getId());
                        routeDetails.setTrainCode(getCode());
                        routeDetails.setTrainType(getType());
                        routeDetails.setDepartureStationCode(firstTrainStop.getStationCode());
                        routeDetails.setDepartureTime(firstTrainStop.getDepartureDateTime());
                        routeDetails.setArrivalStationCode(secondTrainStop.getStationCode());
                        routeDetails.setArrivalTime(secondTrainStop.getDepartureDateTime());
                        return routeDetails;
                    }
                    // continue iteration to find arrival station
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Departure station '" + departureStationCode + "' found with matching departure time '"
                                 + firstTrainStop.getDepartureDateTime() + "' BUT arrival station '" + arrivalStationCode + "' NOT FOUND");
                }
                return null;
            }
            // continue iteration to find departure station
        }
        logger.debug("Departure station not found on this train");
        return null;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
    
    public void setTrainStops(List<TrainStop> trainStops) {
        this.trainStops = trainStops;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("code", this.code).append("type", this.type).append("version",
                                                                                                                          this.version)
            .append("trainStops", this.trainStops).append("seats", this.seats).toString();
    }
}
