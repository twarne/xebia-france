package fr.xebia.coherence.service;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.extractor.ReflectionExtractor;

public class CatalogServiceWithIndexedCoherence extends
		CatalogServiceWithCoherence {

	protected NamedCache initializeCache() {
		NamedCache cache = CacheFactory.getCache("IndexedCommune");
		cache.addIndex(new ReflectionExtractor("getName"), true, null); // like
		// filter
		return cache;
	}

	@Override
	public String getTag() {
		return "CoherenceIndexed";
	}
}
