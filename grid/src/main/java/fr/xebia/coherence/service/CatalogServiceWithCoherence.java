package fr.xebia.coherence.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.filter.LikeFilter;

import fr.xebia.coherence.bean.Commune;

public class CatalogServiceWithCoherence implements CatalogService,
		CacheableService {

	private final NamedCache cacheCommunes = CacheFactory.getCache("Commune");

	public List<Commune> getCommuneByName(String name) {
		Filter filter = new LikeFilter("getName", name + "%", false);
		return performSearch(filter);
	}

	@SuppressWarnings("unchecked")
	private List<Commune> performSearch(Filter filter) {
		Set<Map.Entry<Object, Commune>> entrySet = cacheCommunes
				.entrySet(filter);

		List<Commune> result = new ArrayList<Commune>(entrySet.size());
		for (Map.Entry<Object, Commune> e : entrySet) {
			result.add(e.getValue());
		}

		return result;
	}
	
	protected NamedCache initializeCache() {
		return CacheFactory.getCache("Commune");
	}


	

	@Override
	public void preload(Map<Integer, Commune> data) {
		if (data == null)
			return;

		if (data.isEmpty())
			return;

		cacheCommunes.putAll(data);
	}

	@Override
	public String getTag() {
		return "Coherence";
	}

}
