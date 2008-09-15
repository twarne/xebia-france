package fr.xebia.xke.concurrency;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;

import fr.xebia.xke.concurrency.impl.MyBasket;

public class NProducers1ConsumerTimedTest {

	private static final int SIZE_MAX = 100;
	private static final int NB_PRODUCERS = 4;
	private static final int NB_LOOP = 100;
	private static final long PAUSE = 1;

	private static final int TIMEOUT_QUEUE = 2;

	private ExecutorService producers;
	private ThreadFactory threadFactory;
	private long startTest;

	private Basket basket;

	@Before
	public void setSmartBasket() {
		basket = new MyBasket(SIZE_MAX, TIMEOUT_QUEUE);
	}

	@Test
	public void testDoIt() throws Exception {
		threadFactory = new MyThreadFactory("Producer");
		basket.start();
		producers = Executors.newFixedThreadPool(NB_PRODUCERS, threadFactory);

		startTest = System.currentTimeMillis();
		producers.invokeAll(getTasks());

		log("done");
		basket.stop();
		assertEquals((long) NB_PRODUCERS * NB_LOOP, (long) basket.getSize());
		//assertEquals(NB_PRODUCERS * NB_LOOP / SIZE_MAX, basket.getResetCount());

	}

	private List<Callable<Void>> getTasks() {
		List<Callable<Void>> tasks = new ArrayList<Callable<Void>>(NB_PRODUCERS);
		for (int i = 0; i < NB_PRODUCERS; i++) {
			tasks.add(new Callable<Void>() {
				public Void call() throws Exception {
					for (int i = 0; i < NB_LOOP; i++) {
						basket.put(new Date().toString());
						Thread.sleep(PAUSE);
					}
					return null;
				}
			});
		}
		return tasks;
	}

	private void log(Object o) {
		System.out.println((System.currentTimeMillis() - startTest) + " "
				+ Thread.currentThread().getName() + " " + o);
	}

}
