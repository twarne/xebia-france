package fr.xebia.xke.concurrency.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.xebia.xke.concurrency.Basket;
import fr.xebia.xke.concurrency.MyThreadFactory;

public class MyBlockingBasket implements Basket {

	private final ScheduledExecutorService consumerPool;
	private final BlockingQueue<String> queue;
	private final Consumer consumer;
	private final int timeout;

	public MyBlockingBasket(int limit, int timeout) {
		consumerPool = Executors.newScheduledThreadPool(1, new MyThreadFactory(
				"Consumer"));
		queue = new LinkedBlockingQueue<String>(50);
		this.timeout = timeout;
		consumer = new Consumer(queue, timeout);
	}

	public void put(String data) {
		try {
			queue.put(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		System.out.println("MyBasket.start()");
		consumerPool.scheduleAtFixedRate(consumer, timeout, timeout,
				TimeUnit.SECONDS);
	}

	public void stop() {
		System.out.println("MyBasket.stop()");
		consumerPool.shutdownNow();
		try {
			consumerPool.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
	}

	public int getSize() {
		return consumer.getCompteur();
	}

	public int getResetCount() {
		return consumer.getNbCountReached();
	}

	private final class Consumer implements Runnable {
		private final BlockingQueue<String> queue;
		private final List<String> messages = new ArrayList<String>();
		private int compteur = 0;
		private long timeout;

		private int nbTimeout = 0;
		private int nbCountReached = 0;
		
		long start = System.nanoTime();

		Consumer(BlockingQueue<String> queue, long timeout) {
			log("Consumer.Consumer()");
			this.queue = queue;

			this.timeout = timeout;
		}

		public void run() {
			log("Consumer.run()");
			
			log("TimeOut,  " + getElapsedTime(start) + "s");
			nbTimeout++;
			process();
		}

		private float getElapsedTime(long start) {
			return ((float) (System.nanoTime() - start))
					/ ((float) (1000 * 1000 * 1000));
		}

		private void process() {
			queue.drainTo(messages);
			if (messages.size() > 0) {
				log("messages #" + messages.size());
				compteur = compteur + messages.size();
			}
			messages.clear();
		}

		private int getCompteur() {
			return compteur;
		}

		private int getNbTimeout() {
			return nbTimeout;
		}

		private int getNbCountReached() {
			return nbCountReached;
		}

		private long getTimeout() {
			return timeout;
		}

		// The new timeout value will be used at the next timeout or overflow.
		private void setTimeout(long timeout) {
			log("Consumer.setTimeout(" + timeout + ")");
			this.timeout = timeout;
		}
	}

	private void log(Object o) {
		System.out.println(Thread.currentThread().getName() + " " + o);
	}

}
