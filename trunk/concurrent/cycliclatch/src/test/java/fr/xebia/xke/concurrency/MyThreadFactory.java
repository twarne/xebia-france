package fr.xebia.xke.concurrency;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadFactory implements ThreadFactory {

	private final AtomicInteger id = new AtomicInteger(0);
	private final String name;

	public MyThreadFactory(String name) {
		this.name = name;
	}

	public Thread newThread(Runnable r) {
		return new Thread(r, "Thread " + name + " #" + id.getAndIncrement());
	}

}
