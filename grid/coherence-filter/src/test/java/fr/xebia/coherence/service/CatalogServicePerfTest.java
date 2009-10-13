package fr.xebia.coherence.service;


import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.xebia.coherence.bean.Commune;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-database.xml" })
public class CatalogServicePerfTest {

	@Resource
	private CatalogService interceptedCatalogService;
	
	private Random r = new Random();

	@Test
	public void testCommuneByName() throws Exception {
		
		try {
			for (int i=0; i< 100; i++) {
				String filter = getFilter();
				List<Commune> communes = interceptedCatalogService
						.getCommuneByName(filter);
				//assertNotNull(communes);
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getFilter() {
		int x = r.nextInt(26);
		return "" + (char) (x+65);
	}

}
