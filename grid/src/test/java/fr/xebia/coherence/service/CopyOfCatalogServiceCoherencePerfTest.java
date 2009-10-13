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
@ContextConfiguration(locations = { "/spring-coherence.xml" })
public class CopyOfCatalogServiceCoherencePerfTest {

	@Resource
	private CatalogService interceptedCatalogService;
	
	private Random r = new Random();

	@Test
	public void testCommuneByName() throws Exception {
		
		try {
			for (int i=0; i< 1000; i++) {
				String filter = getFilter();
				List<Commune> communes = interceptedCatalogService
						.getCommuneByName(filter);
				//System.out.println("* "+filter+" "+communes.size());
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
