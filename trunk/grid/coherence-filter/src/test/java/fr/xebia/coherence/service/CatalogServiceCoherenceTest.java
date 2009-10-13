package fr.xebia.coherence.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.xebia.coherence.bean.Commune;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-coherence.xml" })
public class CatalogServiceCoherenceTest {

	@Resource
	private CatalogService interceptedCatalogService;

	
	
	@Test
	public void testCommuneByName() throws Exception {
		
		List<Commune> communes = interceptedCatalogService.getCommuneByName("NANTES");
		System.out.println(communes);
		//asdaysertNotNull(communes);
		assertEquals(2, communes.size());
	}



}
