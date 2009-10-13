/**
 * 
 */
package fr.xebia.coherence.service;

import java.util.Map;
import java.util.Set;

import com.tangosol.util.Filter;
import com.tangosol.util.filter.LikeFilter;

public class MyLikeFilter extends LikeFilter {

	private static final long serialVersionUID = 1L;

	public MyLikeFilter(String string, String string2, boolean b) {
		super(string, string2, b);
	}

	@Override
	public Filter applyIndex(Map mapIndexes, Set setKeys) {
		System.out
				.println("CatalogServiceWithCoherence.MyLikeFilter.applyIndex()");
		long start = System.currentTimeMillis();
		Filter f = super.applyIndex(mapIndexes, setKeys);
		System.out.println("Filter " + f + " setKeySize = "
				+ setKeys.size()+" ---"+(System.currentTimeMillis() - start));

		return f;
	}

}