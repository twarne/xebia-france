package fr.xebia.aop.app;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;

public class ApplicationTest extends TestCase {

	public void testReInstance() throws Exception {
		System.out.println("ApplicationTest.testReInstance()");
		for (int i = 0; i < 5; i++) {
			Application application = new Application();
			application.run();
		}
	}

	public void testSameInstance() throws Exception {
		System.out.println("ApplicationTest.testSameInstance()");
		Application application = new Application();
		for (int i = 0; i < 5; i++) {
			application.run();
		}
	}
	
	public void testSameInstanceMT() throws Exception {
		System.out.println("ApplicationTest.testSameInstanceMT()");
		
		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		Collection<Future<?>> submits = new LinkedList<Future<?>>();
	
		for (int i=0; i < 5; i++)
			submits.add(newCachedThreadPool.submit(new Application()));
		
		for (Future<?> future : submits) {
			future.get();
		}
		
	}
}
