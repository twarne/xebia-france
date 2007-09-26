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
package fr.xebia.demo.objectgrid.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
public class Employee implements Serializable, Comparable<Employee> {

    @Id
    protected Long id;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<Payroll> payrolls = new ArrayList<Payroll>();

    @Basic
    @Index
    protected String lastName;

    @Version
    protected int version;

    @Basic
    protected String firstName;

    private static final long serialVersionUID = 1L;

    @Basic
    protected String email;

    @Basic
    protected String mobilePhoneNumber;

    @Basic
    protected String comments;

    public Employee() {
        super();
    }

    /**
     * @param id
     * @param lastName
     * @param firstName
     * @param email
     * @param mobilePhoneNumber
     * @param version
     */
    public Employee(long id, String lastName, String firstName, String email, String mobilePhoneNumber, int version) {
        this(lastName, firstName, email, mobilePhoneNumber);
        this.id = id;
        this.version = version;
    }

    /**
     * @param lastName
     * @param firstName
     * @param email
     * @param mobilePhoneNumber
     */
    public Employee(String lastName, String firstName, String email, String mobilePhoneNumber) {
        this();
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof Employee) == false) {
            return false;
        }
        Employee other = (Employee) obj;
        return new EqualsBuilder().append(this.lastName, other.lastName).append(this.firstName, other.firstName).isEquals();
    }

    public String getComments() {
        return comments;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public List<Payroll> getPayrolls() {
        return payrolls;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.lastName).append(this.firstName).toHashCode();
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public void setPayrolls(List<Payroll> payrolls) {
        this.payrolls = payrolls;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("lastName", this.lastName).append("firstName", this.firstName)
                .append("version", this.version).toString();
    }

    public int compareTo(Employee other) {
        return new CompareToBuilder().append(this.lastName, other.lastName).append(this.firstName, other.firstName).toComparison();
    }
}
