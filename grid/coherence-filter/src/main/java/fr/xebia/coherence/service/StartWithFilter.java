/**
 * 
 */
package fr.xebia.coherence.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

import com.tangosol.util.Filter;
import com.tangosol.util.MapIndex;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.IndexAwareFilter;

import fr.xebia.coherence.bean.Commune;

public class StartWithFilter implements Filter, Serializable,
		IndexAwareFilter {

	private static final long serialVersionUID = -3465211716479504319L;
	private String begin;

	public StartWithFilter(String begin) {

		this.begin = begin;

	}

	public boolean evaluate(Object paramObject) {
		// System.out.println("evaluate "+paramObject);
		Commune c = (Commune) paramObject;
		return c.getName().startsWith(begin);
	}

	public Filter applyIndex(Map mapIndexes, Set setKeys) {

		int initialSize = setKeys.size();
		MapIndex index = (MapIndex) mapIndexes.get(new ReflectionExtractor(
				"getName"));

		if (index == null)
			return this;

		// System.out.println("Use index ");
		Map mapValues = index.getIndexContents();
		// System.out.println("indexContent " + mapValues);
		String sPrefix = begin;
		SortedMap mapTail = ((SortedMap) mapValues).tailMap(sPrefix);
		Set setMatch = new HashSet();
		// System.out.println("Iterate");
		for (Iterator iter = mapTail.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			String sValue = (String) entry.getKey();

			if (!(sValue.startsWith(sPrefix))) {
				// System.out.println("Break ! "+sValue);
				break;
			}
			setMatch.addAll((Set) entry.getValue());

		}
		setKeys.retainAll(setMatch);
		// System.out.println(" keys "+initialSize+"->"+setKeys.size());
		// System.out.println(setKeys);
		// System.out.println("/Iterate");
		return null;
	}

	public int calculateEffectiveness(Map mapIndexes, Set setKeys) {
		System.out
				.println("CatalogServiceWithCoherence.StartWithFilter.calculateEffectiveness()");
		return 0;
	}

	public boolean evaluateEntry(Entry entry) {
		//System.out.println("-----" + entry);
		return false;
	}

}