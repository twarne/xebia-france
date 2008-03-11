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
package fr.xebia.demo.hibernate.search.analysis;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Simple analyzer for English language providing a default set of stop words
 * and based on the ({@link PorterStemFilter}).
 * 
 * @author <a href="cheubes@xebia.fr">Christophe Heubès</a>
 */
public class SimpleEnglishAnalyzer extends Analyzer {

	/**
	 * List of typical English stop words.
	 */
	public static final String[] STOP_WORDS = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "000", "$", "about", "after", "all", "also",
			"an", "and", "another", "any", "are", "as", "at", "be", "because",
			"been", "before", "being", "between", "both", "but", "by", "came",
			"can", "come", "could", "did", "do", "does", "each", "else", "for",
			"from", "get", "got", "has", "had", "he", "have", "her", "here",
			"him", "himself", "his", "how", "if", "in", "into", "is", "it",
			"its", "just", "like", "make", "many", "me", "might", "more",
			"most", "much", "must", "my", "never", "now", "of", "on", "only",
			"or", "other", "our", "out", "over", "re", "said", "same", "see",
			"should", "since", "so", "some", "still", "such", "take", "than",
			"that", "the", "their", "them", "then", "there", "these", "they",
			"this", "those", "through", "to", "too", "under", "up", "use",
			"very", "want", "was", "way", "we", "well", "were", "what", "when",
			"where", "which", "while", "who", "will", "with", "would", "you",
			"your", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
			"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
			"z" };

	/**
	 * Contains the stop words used with the StopFilter.
	 */
	private static Set<String> stopTable;

	/**
	 * Builds an analyzer with the default stop words ({@link #STOP_WORDS}).
	 */
	public SimpleEnglishAnalyzer() {
		this(STOP_WORDS);
	}

	/**
	 * Builds an analyzer with the given stop words.
	 * 
	 * @param stopWords
	 *            stop words array.
	 */
	public SimpleEnglishAnalyzer(String[] stopWords) {
		stopTable = StopFilter.makeStopSet(stopWords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String,
	 *      java.io.Reader)
	 */
	@Override
	public final TokenStream tokenStream(String fieldName, Reader reader) {

		if (fieldName == null)
			throw new IllegalArgumentException("fieldName must not be null");
		if (reader == null)
			throw new IllegalArgumentException("reader must not be null");

		TokenStream result = new StandardTokenizer(reader);
		result = new StandardFilter(result);
		result = new LowerCaseFilter(result);
		result = new StopFilter(result, stopTable);
		result = new PorterStemFilter(result);
		return result;
	}

}