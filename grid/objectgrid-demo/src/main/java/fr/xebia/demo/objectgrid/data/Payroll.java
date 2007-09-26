package fr.xebia.demo.objectgrid.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.ibm.websphere.objectgrid.em.annotations.Index;
import com.ibm.websphere.projector.annotations.Basic;
import com.ibm.websphere.projector.annotations.Entity;
import com.ibm.websphere.projector.annotations.Id;
import com.ibm.websphere.projector.annotations.ManyToOne;
import com.ibm.websphere.projector.annotations.Version;

@Entity
public class Payroll implements Serializable, Comparable<Payroll> {

    private static final long serialVersionUID = 1L;

    @Id
    protected Long id;

    @Index
    @ManyToOne
    protected Employee employee;

    @Basic
    protected Date date;

    @Basic
    protected BigDecimal salary;

    @Version
    protected int version;

    @Basic
    protected String comments;

    public Payroll() {
        super();
    }

    public Payroll(long id, Employee employee, Date date, BigDecimal salary, int version, String comments) {
        this();
        this.id = id;
        this.employee = employee;
        this.date = date;
        this.salary = salary;
        this.version = version;
        this.comments = comments;
    }

    public int compareTo(Payroll other) {
        return new CompareToBuilder().append(this.employee, other.employee).append(this.date, other.date)
                .toComparison();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj instanceof Payroll) == false) {
            return false;
        }
        Payroll other = (Payroll) obj;
        return new EqualsBuilder().append(this.employee, other.employee).append(this.date, other.date).isEquals();
    }

    public String getComments() {
        return comments;
    }

    public Date getDate() {
        return date;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.employee).append(this.date).toHashCode();
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("employee", this.employee).append("date",
                this.date).append("salary", this.salary).append("version", this.version).toString();
    }

}
