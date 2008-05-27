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

import static fr.xebia.demo.hibernate.search.model.Constants.AUTHOR_SEQUENCE;
import static fr.xebia.demo.hibernate.search.model.Constants.AUTHOR_SEQUENCE_NAME;
import static fr.xebia.demo.hibernate.search.model.Constants.AUTHOR_TABLE_NAME;
import static fr.xebia.demo.hibernate.search.model.Constants.DEFAULT_SEQUENCE_ALLOCATION_SIZE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import fr.xebia.demo.hibernate.search.analysis.SimpleEnglishAnalyzer;

/**
 * Model class representing a document's author.
 * 
 * @author <a href="cheubes@xebia.fr">Christophe Heubès</a>
 */
@Entity
@Indexed
@Analyzer(impl = SimpleEnglishAnalyzer.class)
@Table(name = AUTHOR_TABLE_NAME)
public class Author {

	/**
	 * The author's technical unique identifier.
	 */
	protected Long id;
	/**
	 * The author's last name.
	 */
	protected String lastName;
	/**
	 * The author's first name.
	 */
	protected String firstName;
	/**
	 * The author's resume.
	 */
	protected String resume;

	/**
	 * Default constructor.
	 */
	public Author() {
	}

	/**
	 * Fully qualified constructor.
	 * 
	 * @param firstName
	 *            the author's first name.
	 * @param lastName
	 *            the author's last name.
	 * @param resume
	 *            the author's resume name.
	 */
	public Author(String firstName, String lastName, String resume) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.resume = resume;
	}

	/**
	 * Gets the author's identifier.
	 * 
	 * @return the author's id.
	 */
	@Id
	@DocumentId
	@SequenceGenerator(name = AUTHOR_SEQUENCE_NAME, sequenceName = AUTHOR_SEQUENCE, allocationSize = DEFAULT_SEQUENCE_ALLOCATION_SIZE)
	@GeneratedValue(generator = AUTHOR_SEQUENCE_NAME)
	@Column(name = "id", nullable = false)
	public Long getId() {
		return id;
	}

	/**
	 * Gets the authors's last name.
	 * 
	 * @return the author's last name.
	 */
	@Column(name = "last_name")
	@Field(index = Index.TOKENIZED, store = Store.NO)
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gets the authors's first name.
	 * 
	 * @return the author's first name.
	 */
	@Column(name = "first_name")
	@Field(index = Index.TOKENIZED, store = Store.NO)
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gets the authors's resume.
	 * 
	 * @return the author's resume.
	 */
	@Column(name = "resume")
	@Field(index = Index.TOKENIZED, store = Store.NO)
	public String getResume() {
		return resume;
	}

	/**
	 * Sets the author's identifier.
	 * 
	 * @param id
	 *            the identifier to set.
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Sets the author's last name.
	 * 
	 * @param id
	 *            the last name to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the author's first name.
	 * 
	 * @param id
	 *            the first name to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the author's resume.
	 * 
	 * @param id
	 *            the resume to set.
	 */
	public void setResume(String resume) {
		this.resume = resume;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getFirstName() + " " + this.getLastName() + " ("
				+ this.getId() + ")";
	}

}
