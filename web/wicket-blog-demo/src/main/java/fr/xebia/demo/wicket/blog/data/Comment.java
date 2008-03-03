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
import java.util.Date;

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
@Table(name = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean approved;

    private String author;

    private String email;

    private String content;

    private Date date;

    private Long postId;
    
    public Comment() {
        super();
    }
    
    public Comment(Long postId) {
        super();
        this.postId = postId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    @Column(name = "approved")
    public Boolean getApproved() {
        return this.approved;
    }

    public void setApproved(Boolean pApproved) {
        this.approved = pApproved;
    }

    @Column(name = "author")
    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String pAuthor) {
        this.author = pAuthor;
    }

    @Column(name = "author_email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String pAuthorEmail) {
        this.email = pAuthorEmail;
    }

    @Column(name = "content")
    public String getContent() {
        return this.content;
    }

    public void setContent(String pContent) {
        this.content = pContent;
    }

    @Column(name = "date")
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date pDate) {
        this.date = new Date(pDate.getTime());
    }

    @Column(name = "post_id")
    public Long getPostId() {
        return this.postId;
    }

    public void setPostId(Long pPostId) {
        this.postId = pPostId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ((obj instanceof Comment) == false) {
            return false;
        }
        Comment otherComment = (Comment) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(getId(), otherComment.getId());
        equalsBuilder.append(getApproved(), otherComment.getApproved());
        equalsBuilder.append(getAuthor(), otherComment.getAuthor());
        equalsBuilder.append(getEmail(), otherComment.getEmail());
        equalsBuilder.append(getContent(), otherComment.getContent());
        equalsBuilder.append(getDate(), otherComment.getDate());
        equalsBuilder.append(getPostId(), otherComment.getPostId());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getId());
        hashCodeBuilder.append(getApproved());
        hashCodeBuilder.append(getAuthor());
        hashCodeBuilder.append(getEmail());
        hashCodeBuilder.append(getContent());
        hashCodeBuilder.append(getDate());
        hashCodeBuilder.append(getPostId());
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        builder.append("approved", approved);
        builder.append("author", author);
        builder.append("authorEmail", email);
        builder.append("date", date);
        builder.append("postId", postId);
        return builder.toString();
    }
}
