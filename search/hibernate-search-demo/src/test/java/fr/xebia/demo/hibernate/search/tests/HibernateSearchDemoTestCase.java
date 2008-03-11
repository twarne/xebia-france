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
package fr.xebia.demo.hibernate.search.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import fr.xebia.demo.hibernate.search.analysis.SimpleEnglishAnalyzer;
import fr.xebia.demo.hibernate.search.model.Author;
import fr.xebia.demo.hibernate.search.model.Document;

/**
 * Test case for Xebia Hibernate Search demonstration.
 * 
 * @author <a href="cheubes@xebia.fr">Christophe Heubès</a>
 */
public class HibernateSearchDemoTestCase extends TestCase {

	/**
	 * Log4j logger.
	 */
	protected static final Logger logger = Logger
			.getLogger(HibernateSearchDemoTestCase.class);

	/**
	 * Hibernate session for tests.
	 */
	protected Session testSession;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		logger.debug("=====================================================");
		logger.debug("Start Hibernate Search Demonstration TestCase Set up:");
		super.setUp();
		logger.debug("     - Set up DB config...");
		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.setProperty("hibernate.dialect",
				"org.hibernate.dialect.HSQLDialect");
		cfg.setProperty("hibernate.connection.driver_class",
				"org.hsqldb.jdbcDriver");
		cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:demo");
		cfg.setProperty("hibernate.connection.username", "sa");
		cfg.setProperty("hibernate.connection.password", "");
		cfg.setProperty("hibernate.connection.pool_size", "1");
		cfg.setProperty("hibernate.connection.autocommit", "false");
		cfg.setProperty("hibernate.cache.provider_class",
				"org.hibernate.cache.HashtableCacheProvider");
		cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		cfg.setProperty("hibernate.show_sql", "false");
		cfg.setProperty("hibernate.search.default.indexBase", "target");
		logger.debug("     - Add class to DB config...");
		cfg.addAnnotatedClass(Document.class);
		cfg.addAnnotatedClass(Author.class);
		logger.debug("     - Open Hibernate test session...");
		this.testSession = cfg.buildSessionFactory().openSession();
		logger.debug("     - Populate DB...");
		this.populateDB();
		logger.debug("Hibernate Search Demonstration TestCase Set up Done.");
		logger.debug("=====================================================\n");
		this.logDBContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		logger.debug("=====================================================");
		logger
				.debug("Start Hibernate Search Demonstration TestCase Tear Down:");
		logger.debug("     - Empty DB...");
		this.emptyDB();
		logger.debug("     - Close Hibernate test session...");
		this.testSession.close();
		super.tearDown();
		logger
				.debug("Hibernate Search Demonstration TestCase Tear Down finished.\n");
		logger.debug("=====================================================\n");
	}

	/**
	 * Simple search test.
	 */
	public void testSimpleSearch() {
		String queryString = "java";
		logger.info("=====================================================");
		logger.info("Simple Test (query == \"" + queryString + "\"):\n");
		this.logSearhResult(this.searchDocuments(queryString));
		logger.info("=====================================================\n");
		queryString = "father";
		logger.info("=====================================================");
		logger.info("Simple Test (query == \"" + queryString + "\"):\n");
		this.logSearhResult(this.searchDocuments(queryString));
		logger.info("=====================================================\n");
	}

	/**
	 * Simple case insensitive search test.
	 */
	public void testSimpleCaseSearch() {
		String queryString = "JAVA";
		logger.info("=====================================================");
		logger.info("Simple Case Test (query == \"" + queryString + "\"):\n");
		this.logSearhResult(this.searchDocuments(queryString));
		logger.info("=====================================================\n");
	}

	/**
	 * Approximate search test.
	 */
	public void testApproximateSearch() {
		String queryString = "jav~";
		logger.info("=====================================================");
		logger.info("Proximity Test (query == \"" + queryString + "\"):\n");
		this.logSearhResult(this.searchDocuments(queryString));
		logger.info("=====================================================\n");
	}

	/**
	 * Proximity search test.
	 */
	public void testProximitySearch() {
		String queryString = "\"engineer UK\"~5";
		logger.info("=====================================================");
		logger.info("Proximity Test (query == \"" + queryString + "\"):\n");
		this.logSearhResult(this.searchDocuments(queryString));
		logger.info("=====================================================\n");
	}

	/**
	 * Searches documents matching a Lucene query string.
	 * 
	 * @param queryString
	 *            a Lucene query string.
	 * @return Matching documents.
	 */
	private List<Document> searchDocuments(String queryString) {
		FullTextSession searchSession = Search
				.createFullTextSession(this.testSession);
		Transaction tx = searchSession.beginTransaction();
		MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {
				"title", "summary", "author.lastName", "author.firstName",
				"author.resume" }, new SimpleEnglishAnalyzer());
		FullTextQuery query;
		try {
			query = searchSession.createFullTextQuery(
					parser.parse(queryString), Document.class);
		} catch (ParseException pe) {
			logger.error("Error while parsing query.", pe);
			return new ArrayList<Document>(0);
		}
		List<Document> result = query.list();
		tx.commit();
		return result;
	}

	/**
	 * Logs a document search result.
	 * 
	 * @param result
	 *            the list of document to log.
	 */
	private void logSearhResult(List<Document> result) {
		logger.info("Results list:");
		for (Document doc : result) {
			logger.info(doc);
		}
	}

	/**
	 * Log the content of the test data base.
	 */
	private void logDBContent() {
		Criteria crit = this.testSession.createCriteria(Document.class);
		List<Document> docList = crit.list();
		logger.debug("=====================================================");
		logger.debug("Document list :");
		for (Document doc : docList) {
			logger.debug(doc);
		}
		logger.debug("End of Document list.");
		logger.debug("=====================================================\n");
	}

	/**
	 * Populates test data base with test datas.
	 */
	private void populateDB() {
		logger.debug("            - Begin transaction.");
		Transaction tx = this.testSession.beginTransaction();

		// Authors
		Author bTate = new Author(
				"Bruce",
				"Tate",
				"Bruce A. Tate is a kayaker, mountain biker, and father of two. In his spare time, he is an independent consultant in Austin, Texas. In 2001, he founded J2Life, LLC, a consulting firm that specializes in Java persistence frameworks and lightweight development methods. His customers have included FedEx, Great West Life, TheServerSide, and BEA. He speaks at conferences and Java user's groups around the nation. Before striking out on his own, Bruce spent 13 years at IBM working on database technologies, object-oriented infrastructure, and Java. He was recruited away from IBM to help start the client services practice in an Austin startup called Pervado Systems. He later served a brief stint as CTO of IronGrid, which built nimble Java performance tools. Bruce is the author of four books, including the bestselling \"Bitter Java\", and the recently released Better, Faster, Lighter Java, from O'Reilly. First rule of kayak: When in doubt, paddle like Hell.");
		Author aOram = new Author(
				"Andy",
				"Oram",
				"Andy Oram is an editor at O'Reilly Media, which is a highly respected book publisher and technology information provider. An employee of the company since 1992, Andy currently specializes in free software and open source technologies. His work for O'Reilly includes the first books ever published commercially in the United States on Linux, and the 2001 title Peer-to-Peer. His modest programming and system administration skills are mostly self-taught. Andy is also a member of Computer Professionals for Social Responsibility and writes often for the O'Reilly Network and other publications. Topics include policy issues related to the Internet and trends affecting technical innovation and its effects on society. His web site is www.praxagora.com/andyo. Andy works at the O'Reilly office in Cambridge, Massachusetts and lives nearby with his wife, two children, and a six-foot grand piano that can often be heard late at night.");
		Author rMiles = new Author(
				"Russell",
				"Miles",
				"Russell Miles is a software engineer for General Dynamics UK where he works with Java and Distributed Systems, although his passion at the moment is Aspect Orientation and in particular AspectJ. To ensure that he has as little spare time as possible, Russ contributes to various open source projects while working on books for O'Reilly. He currently is studying at Oxford University in England for an MSc in Software Engineering.");
		// Documents
		Document beyondJava = new Document();
		beyondJava.setTitle("Beyond Java");
		beyondJava
				.setSummary("In Beyond Java, Bruce Tate, author of the Jolt Award-winning Better, Faster, Lighter Java, chronicles the rise of the most successful language of all time, and then lays out, in painstaking detail, the compromises the founders had to make to establish success. If you are agree with the book's premise--that Java's reign is coming to an end--then this book will help you start to build your skills accordingly. Beyond Java will teach you what a new language needs to succeed, so when things do change, you'll be more prepared. And even if you think Java is here to stay, you can use the best techniques from frameworks introduced in this book to improve what you're doing in Java today.");
		beyondJava.setAuthor(bTate);
		this.testSession.persist(beyondJava);

		Document fromJavaToRuby = new Document();
		fromJavaToRuby.setTitle("From Java to Ruby");
		fromJavaToRuby
				.setSummary("If you're trying to adopt Ruby in your organization and need some help, this is the book for you. Based on a decision tree (a concept familiar to managers and executives,) Java to Ruby stays above the low-level technical debate to examine the real benefits and risks to adoption. Java to Ruby is packed with interviews of Ruby customers and developers, so you can see what types of projects are likely to succeed, and which ones are likely to fail. Ruby and Rails may be the answer, but first you need to be sure you're asking the right question. By addressing risk and fitness of purpose, Java to Ruby makes sure you're asking the right questions first. Because technology adoption is only the beginning, Java to Ruby walks you through the whole lifecycle of prototype, ramp up, and production and deployment.");
		fromJavaToRuby.setAuthor(bTate);
		this.testSession.persist(fromJavaToRuby);

		Document beautifulCode = new Document();
		beautifulCode.setTitle("Beautiful Code");
		beautifulCode
				.setSummary("How do the experts solve difficult problems in software development? In this unique and insightful book, leading computer scientists offer case studies that reveal how they found unusual, carefully designed solutions to high-profile projects. You will be able to look over the shoulder of major coding and design experts as they work through their project's architecture, the tradeoffs made in its construction, and when it was important to break rules.");
		beautifulCode.setAuthor(aOram);
		this.testSession.persist(beautifulCode);

		Document aspectJCookbook = new Document();
		aspectJCookbook.setTitle("AspectJ Cookbook");
		aspectJCookbook
				.setSummary("This hands-on book shows readers why and how common Java development problems can be solved by using new Aspect-oriented programming (AOP) techniques. With a wide variety of code recipes for solving day-to-day design and coding problems using AOP's unique approach, AspectJ Cookbook demonstrates that AOP is more than just a concept; it's a development process that will benefit users in an immediate and visible manner.");
		aspectJCookbook.setAuthor(rMiles);
		this.testSession.persist(aspectJCookbook);

		tx.commit();
		logger.debug("            - Transaction commited.");
	}

	/**
	 * Delete all test datas from data base.
	 */
	private void emptyDB() {
		logger.debug("            - Begin transaction.");
		Transaction tx = this.testSession.beginTransaction();

		Criteria crit = this.testSession.createCriteria(Document.class);
		List<Document> docList = crit.list();
		for (Document doc : docList) {
			testSession.delete(doc);
		}

		crit = this.testSession.createCriteria(Author.class);
		List<Author> authorList = crit.list();
		for (Author author : authorList) {
			testSession.delete(author);
		}

		tx.commit();
		logger.debug("            - Transaction commited.");
	}

}
