package fr.xebia.coherence.service;

import java.util.List;

import fr.xebia.coherence.bean.Commune;
import fr.xebia.coherence.persistence.CatalogDao;

public class CatalogServiceWithDatabase implements CatalogService {
	
	private CatalogDao dao;
	
	public Commune getCommuneById(Integer id)  {
		return dao.getCommuneById(id);
	}
	
	/* (non-Javadoc)
	 * @see fr.xebia.coherence.service.CatalogService#getCommuneByName(java.lang.String)
	 */
	public List<Commune> getCommuneByName(String name) {
		return dao.getCommuneByName(name+"%");
	}
	
	public void setDao(CatalogDao dao) {
		this.dao = dao;
	}

	@Override
	public String getTag() {
		return "Database";
	}
	
}
