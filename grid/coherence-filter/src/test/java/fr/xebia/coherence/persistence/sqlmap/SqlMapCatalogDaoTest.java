package fr.xebia.coherence.persistence.sqlmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.xebia.coherence.bean.Commune;
import fr.xebia.coherence.persistence.CatalogDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-database.xml" })
public class SqlMapCatalogDaoTest {

	@Autowired
	private CatalogDao catalogDao;

	@Test
	public void testOneId() throws Exception {
		Commune communeById = catalogDao.getCommuneById(1);
		assertNotNull(communeById);
		assertEquals(1, communeById.getId());
	}

	@Test
	public void testOneName() throws Exception {
		assertEquals(2,catalogDao.getCommuneByName("NANTES%").size());
	}
	
	@Test
	public void testGetAll() throws Exception {
		long start= System.currentTimeMillis();
		assertEquals(38206,catalogDao.getAllCommunes().size());
		long stop= System.currentTimeMillis();
		System.out.println("ALL: "+(stop-start));
	}	

}
