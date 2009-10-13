package fr.xebia.coherence.benchmark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.xebia.coherence.bean.Commune;
import fr.xebia.coherence.persistence.CatalogDao;
import fr.xebia.coherence.service.CacheableService;
import fr.xebia.coherence.service.CatalogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-benchmark.xml" })
public class CacheServer {

	@Resource
	private CatalogService coherenceService;

	@Resource
	private CatalogService indexedCoherenceService;

	@Resource
	private CatalogDao catalogDao;

	@Test
	public void serialBenchMark() throws Exception{
		System.out.println("CatalogBenchMark.setUp()");
		StopWatch stopWatch = new LoggingStopWatch("setup");
		List<Commune> allCommunes = catalogDao.getAllCommunes();
		Map<Integer, Commune> tempMap = new HashMap<Integer, Commune>();
		for (Commune commune : allCommunes) {
			tempMap.put(commune.getInsee(), commune);
		}
		stopWatch.stop();
		((CacheableService) coherenceService).preload(tempMap);
		((CacheableService) indexedCoherenceService).preload(tempMap);
		System.in.read();

	}

}
