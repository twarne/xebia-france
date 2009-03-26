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
import java.math.BigInteger;

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
@javax.persistence.Entity
public class Seat implements Serializable, Comparable<Seat> {
    
    private static final long serialVersionUID = 1L;
    
    @Basic
    protected boolean booked;
    
    @Id
    @javax.persistence.Id
    protected long id;
    
    @Basic
    protected int number;
    
    @Basic
    protected BigInteger price;
    
    @Version
    @javax.persistence.Version
    protected int version;

    public Seat() {
        super();
    }

    public Seat(long id, int number, boolean booked, int price) {
        super();
        this.id = id;
        this.number = number;
        this.booked = booked;
        this.price = BigInteger.valueOf(price);
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
        final Seat other = (Seat)obj;
        return new EqualsBuilder().append(this.number, other.number).isEquals();
    }
    
    public long getId() {
        return id;
    }
    
    public int getNumber() {
        return number;
    }
    
    public BigInteger getPrice() {
        return price;
    }
    
    public int getVersion() {
        return version;
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
    
    public void setId(long id) {
        this.id = id;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public void setPrice(BigInteger price) {
        this.price = price;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("number", this.number).append("booked", this.booked)
            .append("price", this.price).append("version", this.version).toString();
    }
}
