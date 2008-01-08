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
import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.ibm.websphere.projector.annotations.Basic;
import com.ibm.websphere.projector.annotations.Entity;
import com.ibm.websphere.projector.annotations.Id;

/**
 * 
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@Entity
public class TrainStop implements Comparable<TrainStop>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Basic
    private Date departureDateTime;

    @Basic
    private String stationCode;

    public TrainStop() {
        super();
    }

    public TrainStop(int id, Date departureDateTime, String stationCode) {
        super();
        this.id = id;
        this.departureDateTime = departureDateTime;
        this.stationCode = stationCode;
    }

    public int compareTo(TrainStop other) {
        return new CompareToBuilder().append(this.departureDateTime, other.departureDateTime).toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TrainStop)) {
            return false;
        }

        final TrainStop other = (TrainStop) obj;
        return new EqualsBuilder().append(this.departureDateTime, other.departureDateTime).append(this.stationCode, other.stationCode)
                .isEquals();
    }

    public Date getDepartureDateTime() {
        return departureDateTime;
    }

    public int getId() {
        return id;
    }

    public String getStationCode() {
        return stationCode;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.departureDateTime).append(this.stationCode).toHashCode();
    }

    public void setDepartureDateTime(Date departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("time", this.departureDateTime).append("stationCode",
                this.stationCode).toString();
    }
}
