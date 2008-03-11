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
package fr.xebia.demo.hibernate.search.model;

import static fr.xebia.demo.hibernate.search.model.Constants.DEFAULT_SEQUENCE_ALLOCATION_SIZE;
import static fr.xebia.demo.hibernate.search.model.Constants.DOCUMENT_SEQUENCE;
import static fr.xebia.demo.hibernate.search.model.Constants.DOCUMENT_SEQUENCE_NAME;
import static fr.xebia.demo.hibernate.search.model.Constants.DOCUMENT_TABLE_NAME;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;

import fr.xebia.demo.hibernate.search.analysis.SimpleEnglishAnalyzer;

/**
 * Model class representing a document.
 * 
 * @author <a href="cheubes@xebia.fr">Christophe Heubès</a>
 */
@Entity
@Indexed
@Analyzer(impl = SimpleEnglishAnalyzer.class)
@Table(name = DOCUMENT_TABLE_NAME)
@SequenceGenerator(name = DOCUMENT_SEQUENCE_NAME, sequenceName = DOCUMENT_SEQUENCE, allocationSize = DEFAULT_SEQUENCE_ALLOCATION_SIZE)
public class Document {

	/**
	 * The document's technical unique identifier.
	 */
	protected Long id;
	/**
	 * The document's title.
	 */
	protected String title;
	/**
	 * The document's summary.
	 */
	protected String summary;
	/**
	 * The document's author.
	 */
	protected Author author;

	/**
	 * Default constructor.
	 */
	public Document() {
	}

	/**
	 * Gets the document's identifier.
	 * 
	 * @return the document's identifier.
	 */
	@Id
	@DocumentId
	@SequenceGenerator(name = DOCUMENT_SEQUENCE_NAME, sequenceName = DOCUMENT_SEQUENCE, allocationSize = DEFAULT_SEQUENCE_ALLOCATION_SIZE)
	@GeneratedValue(generator = DOCUMENT_SEQUENCE_NAME)
	@Column(name = "id", nullable = false)
	public Long getId() {
		return this.id;
	}

	/**
	 * Gets the document's title.
	 * 
	 * @return the document's title.
	 */
	@Column(name = "title")
	@Field(index = Index.TOKENIZED, store = Store.NO)
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the document's summary.
	 * 
	 * @return the document's summary.
	 */
	@Column(name = "summary")
	@Field(index = Index.TOKENIZED, store = Store.NO)
	public String getSummary() {
		return summary;
	}

	/**
	 * Gets the document's author.
	 * 
	 * @return the document's author.
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "author_id")
	@Fetch(FetchMode.JOIN)
	@ForeignKey(name = "fk_document_author_id")
	@IndexedEmbedded
	public Author getAuthor() {
		return author;
	}

	/**
	 * Sets the document's identifier.
	 * 
	 * @param id
	 *            the document's identifier to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the document's title.
	 * 
	 * @param title
	 *            the document's title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the document's summary.
	 * 
	 * @param summary
	 *            the document's summary to set.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * Sets the document's author.
	 * 
	 * @param author
	 *            the document's author.
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "Document " + this.getId() + ": " + this.getTitle()
				+ ":\n";
		result += "           > Summary: "
				+ this.getSummary().substring(0, 123) + " ...\n";
		if (this.getAuthor() != null) {
			result += "           > Author: " + this.getAuthor() + "\n";
			result += "                     > Resume: "
					+ this.getAuthor().getResume().substring(0, 123) + " ...";
		} else {
			result += "           > Author: No author.";
		}
		return result;
	}
}
