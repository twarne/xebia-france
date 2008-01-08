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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.ibm.websphere.objectgrid.em.annotations.Index;
import com.ibm.websphere.projector.annotations.AccessType;
import com.ibm.websphere.projector.annotations.Basic;
import com.ibm.websphere.projector.annotations.CascadeType;
import com.ibm.websphere.projector.annotations.Entity;
import com.ibm.websphere.projector.annotations.Id;
import com.ibm.websphere.projector.annotations.OneToMany;
import com.ibm.websphere.projector.annotations.Transient;
import com.ibm.websphere.projector.annotations.Version;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@Entity(schemaRoot = true)
public class Train implements Serializable {

    public enum Type {
        HIGH_SPEED, NORMAL
    }

    private static final long serialVersionUID = 1L;

    @Id
    protected Integer id;

    /**
     * Train's business identifier
     */
    //@Basic
    //@Index
    @Transient
    protected String code;

    //@Basic
    @Transient
    protected Type type;

    //@Version
    @Transient
    protected int version;

    //@OneToMany(cascade = CascadeType.ALL)
    @Transient
    protected List<TrainStop> trainStops = new ArrayList<TrainStop>();

    //@OneToMany(cascade = CascadeType.ALL)
    @Transient
    protected List<Seat> seats = new ArrayList<Seat>();

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
        final Train other = (Train) obj;
        return new EqualsBuilder().append(this.code, other.code).isEquals();
    }

    public String getCode() {
        return code;
    }

    public Integer getId() {
        return id;
    }

    public List<Seat> getSeats() {
        return seats;
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.code).toHashCode();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(Integer id) {
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
                this.version).append("trainStops", this.trainStops).append("seats", this.seats).toString();
    }

    private final static Logger logger = Logger.getLogger(Train.class);

    /**
     * Returns <code>true</code> if the train matches the given route.
     * 
     * @param departureStationCode
     * @param departureTimeLowerBound
     * @param departureTimeUpperBound
     * @param arrivalStationCode
     */
    public boolean matchRoute(String departureStationCode, Date departureTime, String arrivalStationCode) {

        if (logger.isDebugEnabled()) {
            logger.debug("matchRoute(departureStationCode=" + departureStationCode + ", departureTime=" + departureTime
                    + ", arrivalStationCode=" + arrivalStationCode + ")");
        }

        Date departureTimeLowerBound = DateUtils.add(departureTime, Calendar.HOUR, -1);
        Date departureTimeUpperBound = DateUtils.add(departureTime, Calendar.HOUR, +1);

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
                    return false;
                }
                while (itTrainStops.hasNext()) {
                    TrainStop secondTrainStop = itTrainStops.next();
                    if (secondTrainStop.getStationCode().equals(arrivalStationCode)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Train '" + this.code + "', departureTime '" + firstTrainStop.getDepartureDateTime() + "' from '"
                                    + firstTrainStop.getStationCode() + "' to '" + secondTrainStop.getStationCode()
                                    + "' matches the given route");
                        }
                        return true;
                    }
                    // continue iteration to find arrival station
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Departure station '" + departureStationCode + "' found with matching departure time '"
                            + firstTrainStop.getDepartureDateTime() + "' BUT arrival station '" + arrivalStationCode + "' NOT FOUND");
                }
                return false;
            }
            // continue iteration to find departure station
        }
        logger.debug("Departure station not found on this train");
        return false;
    }

    public boolean hasAvailableSeat() {
        for (Seat seat : this.seats) {
            if (seat.isBooked() == false) {
                return true;
            }
        }
        return false;
    }
}