/**
 * 
 */
package fr.xebia.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

final public class CyclicLatch {
	private final int initialcount;
	private CountDownLatch latch;
	private final ReentrantLock lock = new ReentrantLock();
	private final long defaultTimeout;
	private final TimeUnit defaultTimeUnit;

	/**
	 * Create a new CyclicLatch with a 15 secondes Timeout;
	 * 
	 * @param initialcount
	 */
	public CyclicLatch(int initialcount) {
		this(initialcount, 15, TimeUnit.SECONDS);
	}

	public CyclicLatch(int initialcount, long defaultTimeout,
			TimeUnit defaultTimeUnit) {
		this.initialcount = initialcount;
		this.defaultTimeout = defaultTimeout;
		this.defaultTimeUnit = defaultTimeUnit;
		this.latch = new CountDownLatch(initialcount);
	}

	public void countDown() {
		try {
			lock.lock();
			latch.countDown();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Await the opening of the Latch
	 * 
	 * @return false if the opening has been done on Timeout, true if the
	 *         opening has been done on overflow.
	 * @throws InterruptedException
	 */
	public boolean await() throws InterruptedException {
		return await(this.defaultTimeout, defaultTimeUnit);
	}

	/**
	 * Await the opening of the Latch with a dedicated timeout (value + unit)
	 * 
	 * @param timeout
	 * @param unit
	 * @return false if the opening has been done on Timeout, true if the
	 *         opening has been done on overflow.
	 * 
	 * @throws InterruptedException
	 */
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {

		log("Waiting for latch of "+this.initialcount+"  & " + timeout + " seconds timeout");
		boolean result = latch.await(timeout, unit);
		/* Reset Latch, on timeout & on overflow */
		try {
			lock.lock();
			log("Reset Latch...." + latch);
			latch = new CountDownLatch(initialcount);
		} finally {
			lock.unlock();
		}
		return result;

	}

	public String toString() {
		return this.latch.toString() + "/[Initial = " + this.initialcount + "]";
	}

	void log(Object o) {
		System.out.println(Thread.currentThread().getName() + " " + o);
	}

}