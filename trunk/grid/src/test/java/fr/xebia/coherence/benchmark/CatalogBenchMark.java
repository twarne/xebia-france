package fr.xebia.coherence.benchmark;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.perf4j.GroupedTimingStatistics;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;
import org.perf4j.TimingStatistics;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tangosol.net.CacheFactory;

import fr.xebia.coherence.service.CatalogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring-benchmark.xml" })
public class CatalogBenchMark {

	private static final char SEP = ';';
	
	@Resource
	private CatalogService databaseService;

	@Resource
	private CatalogService coherenceService;

	@Resource
	private CatalogService indexedCoherenceService;

	// private ExecutorService executor = Executors.newCachedThreadPool();

	class WatchedService {
		final CatalogService service;
		final String tag;
		final GroupedTimingStatistics statistics;

		public WatchedService(CatalogService service) {
			super();
			this.service = service;
			this.tag = service.getTag();
			this.statistics = new GroupedTimingStatistics();
		}

		public CatalogService getService() {
			return service;
		}

		public void addStopWatch(StopWatch watch) {
			this.statistics.addStopWatch(watch);
		}

		public SortedMap<String, TimingStatistics> getStatisticsByTag() {
			return statistics.getStatisticsByTag();
		}

		public String getTag() {
			return tag;
		}

	}

	private List<WatchedService> watchedservices = new ArrayList<WatchedService>();

	@Before
	public void startupCoherence() {
		CacheFactory.ensureCluster();

		watchedservices.add(new WatchedService(databaseService));
		watchedservices.add(new WatchedService(coherenceService));
		watchedservices.add(new WatchedService(indexedCoherenceService));
	}

	// @Test
	public void serialBenchMark() throws Exception {
		// System.out.println("CatalogBenchMark.benchMark()");
		System.out.println("Initialisation #1");
		for (int i = 0; i < 26; i++) {
			final String filter = getFilter(i);
			for (WatchedService wservice : watchedservices) {
				wservice.getService().getCommuneByName(filter);
			}
		}
		System.out.println("/Initialisation #1");
	}

	@Test
	public void serialBenchMarkWithPerj4j() throws Exception {
		serialBenchMark();
		for (int c = 0; c < 10; c++)
			for (int i = 0; i < 26; i++) {
				String filter = getFilter(i);
				System.out.println(filter);
				for (WatchedService wservice : watchedservices) {
					StopWatch stopWatch = new LoggingStopWatch(filter);
					wservice.getService().getCommuneByName(filter);
					wservice.addStopWatch(stopWatch);
				}
			}

		System.out.println(getCSVData());
	}

	private String getCSVData() {
		SortedMap<String, List<TimingStatistics>> synthesis = synthesisResults();

		StringBuffer dump = new StringBuffer();
		dump.append("filter").append(SEP);
		for (WatchedService wservice : this.watchedservices) {
			String tag = wservice.getTag();
			// String labels = "count;max;min;mean;std";
			dump.append(tag).append(" ").append("count").append(SEP);
			dump.append(tag).append(" ").append("max").append(SEP);
			dump.append(tag).append(" ").append("min").append(SEP);
			dump.append(tag).append(" ").append("mean").append(SEP);
			dump.append(tag).append(" ").append("std").append(SEP);
		}

		dump.append("\n");

		for (Entry<String, List<TimingStatistics>> entry : synthesis.entrySet()) {

			dump.append(entry.getKey()).append(SEP);
			List<TimingStatistics> values = entry.getValue();
			for (TimingStatistics timingStatistics : values) {
				dump.append(timingStatistics.getCount()).append(SEP);
				dump.append(timingStatistics.getMax()).append(SEP);
				dump.append(timingStatistics.getMin()).append(SEP);

				Formatter formatter = new Formatter(dump);
				formatter.format("%f", timingStatistics.getMean());
				dump.append(SEP);
				formatter.format("%f", timingStatistics.getStandardDeviation());
				dump.append(SEP);
			}
			dump.append("\n");
		}
		return dump.toString();

	}

	private SortedMap<String, List<TimingStatistics>> synthesisResults() {
		SortedMap<String, List<TimingStatistics>> synthesis = new TreeMap<String, List<TimingStatistics>>();

		for (WatchedService wservice : this.watchedservices) {

			SortedMap<String, TimingStatistics> statisticsByTag = wservice
					.getStatisticsByTag();
			Set<Entry<String, TimingStatistics>> entrySet = statisticsByTag
					.entrySet();
			for (Entry<String, TimingStatistics> entry : entrySet) {
				String key = entry.getKey();
				if (!synthesis.containsKey(key))
					synthesis.put(key, new ArrayList<TimingStatistics>());
				synthesis.get(key).add(entry.getValue());
			}

		}
		return synthesis;
	}

	private String getFilter(int x) {
		return "" + (char) (x + 65);
	}

}
