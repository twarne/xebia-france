package fr.xebia.coherence.service;

import java.util.Map;

import fr.xebia.coherence.bean.Commune;

public interface CacheableService {

	void preload(Map<Integer,Commune> data);
}
