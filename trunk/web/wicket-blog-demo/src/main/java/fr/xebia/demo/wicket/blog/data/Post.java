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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    
    private Long id;

    private Boolean commentsAllowed;

    private String content;

    private Date date;

    private Date modified;

    private String author;

    private String status = STATUS_DRAFT;

    private String title;

    private Category category;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return this.id;
    }

    public void setId(Long pId) {
        this.id = pId;
    }

    @Column(name = "comment_status")
    public Boolean getCommentsAllowed() {
        return this.commentsAllowed;
    }

    public void setCommentsAllowed(Boolean pCommentStatus) {
        this.commentsAllowed = pCommentStatus;
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

    @Column(name = "modified")
    public Date getModified() {
        return this.modified;
    }

    public void setModified(Date pModified) {
        this.modified = new Date(pModified.getTime());
    }

    @Column(name = "post_author")
    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String pPostAuthor) {
        this.author = pPostAuthor;
    }

    @Column(name = "status")
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String pStatus) {
        this.status = pStatus;
    }

    @Column(name = "title")
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String pTitle) {
        this.title = pTitle;
    }

    @ManyToOne
    public Category getCategory() {
        return category;
    }

    
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ((obj instanceof Post) == false) {
            return false;
        }
        Post otherPost = (Post) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(getId(), otherPost.getId());
        equalsBuilder.append(getCommentsAllowed(), otherPost.getCommentsAllowed());
        equalsBuilder.append(getContent(), otherPost.getContent());
        equalsBuilder.append(getDate(), otherPost.getDate());
        equalsBuilder.append(getModified(), otherPost.getModified());
        equalsBuilder.append(getAuthor(), otherPost.getAuthor());
        equalsBuilder.append(getStatus(), otherPost.getStatus());
        equalsBuilder.append(getTitle(), otherPost.getTitle());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(getId());
        hashCodeBuilder.append(getCommentsAllowed());
        hashCodeBuilder.append(getContent());
        hashCodeBuilder.append(getDate());
        hashCodeBuilder.append(getModified());
        hashCodeBuilder.append(getAuthor());
        hashCodeBuilder.append(getStatus());
        hashCodeBuilder.append(getTitle());
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
        builder.append("commentStatus", commentsAllowed);
        builder.append("date", date);
        builder.append("modified", modified);
        builder.append("postAuthor", author);
        builder.append("status", status);
        builder.append("title", title);
        return builder.toString();
    }
}
