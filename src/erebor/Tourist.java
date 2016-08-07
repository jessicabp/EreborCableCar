package erebor;

//Jessica Braddon-Parsons - 13219524

import java.util.concurrent.Semaphore;

public class Tourist implements Runnable {
	boolean top;
	Semaphore queue;
	int delay;

	Tourist(boolean top, Semaphore queue) {
		this.top = top;
		this.queue = queue;
	}

	public void run() {
		if (!top) {
			// tourists spend up to 10 seconds at the summit of the mountain
			delay = 10000;
		} else {
			// tourists spend up to 50 seconds getting to the base of the mountain at the start of the day
			delay = 50000;
		}
		// wait for some time before arriving at Erebor/wanting to leave Erebor
		try {
			Thread.sleep((int)(Math.random()*delay));
		} catch (InterruptedException ie) {}
		if (!top) {
			System.out.println("A tourist arrives at the base station of the cable car.");
		} else {
			System.out.println("A tourist decides to leave the mountain and goes to the summit station.");
		}
		// join the queue
		queue.release();
	}
}