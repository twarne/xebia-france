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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.ibm.websphere.projector.annotations.Basic;
import com.ibm.websphere.projector.annotations.Entity;
import com.ibm.websphere.projector.annotations.Id;
import com.ibm.websphere.projector.annotations.Version;

/**
 * @author <a href="mailto:cyrille.leclerc@pobox.com">Cyrille Le Clerc</a>
 */
@Entity
public class Seat implements Serializable, Comparable<Seat> {

    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Basic
    private int number;

    @Basic
    private boolean booked;

    @Version
    private int version;

    public Seat() {
        super();
    }

    public Seat(int id, int number, boolean booked) {
        super();
        this.id = id;
        this.number = number;
        this.booked = booked;
    }

    public int compareTo(Seat other) {
        return new CompareToBuilder().append(this.number, other.number).toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Seat)) {
            return false;
        }
        final Seat other = (Seat) obj;
        return new EqualsBuilder().append(this.number, other.number).isEquals();
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.number).toHashCode();
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("number", this.number).append("booked", this.booked).append(
                "version", this.version).toString();
    }
}
