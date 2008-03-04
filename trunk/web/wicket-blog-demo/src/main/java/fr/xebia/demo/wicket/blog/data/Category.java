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
package fr.xebia.demo.wicket.blog.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String description;

    private String name;

    private String nicename;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String pDescription) {
        this.description = pDescription;
    }

    @Column(name = "name", nullable=false)
    public String getName() {
        return this.name;
    }

    public void setName(String pName) {
        this.name = pName;
    }

    @Column(name = "nicename", nullable=false)
    public String getNicename() {
        return this.nicename;
    }

    public void setNicename(String pNicename) {
        this.nicename = pNicename;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ((obj instanceof Category) == false) {
            return false;
        }
        Category otherCategory = (Category) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(getId(), otherCategory.getId());
        equalsBuilder.append(getDescription(), otherCategory.getDescription());
        equalsBuilder.append(getName(), otherCategory.getName());
        equalsBuilder.append(getNicename(), otherCategory.getNicename());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getId());
        hashCodeBuilder.append(getDescription());
        hashCodeBuilder.append(getName());
        hashCodeBuilder.append(getNicename());
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        builder.append("description", description);
        builder.append("name", name);
        builder.append("nicename", nicename);
        return builder.toString();
    }
}
