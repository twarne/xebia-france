package fr.xebia.coherence.service;

import java.util.List;

import fr.xebia.coherence.bean.Commune;

public interface CatalogService {

	public abstract List<Commune> getCommuneByName(String name);

	public String getTag();
}