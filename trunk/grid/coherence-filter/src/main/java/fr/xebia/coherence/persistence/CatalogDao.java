package fr.xebia.coherence.persistence;

import java.util.List;

import fr.xebia.coherence.bean.Commune;

public interface CatalogDao {

	public Commune getCommuneById(Integer id) ;
	
	public List<Commune> getCommuneByName(String name) ;
	
	public List<Commune> getAllCommunes();
}
