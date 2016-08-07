package erebor;

// Jessica Braddon-Parsons - 13219524

import java.util.concurrent.Semaphore;
import erebor.Station;

class CableCar {
	static boolean top = false;
	static Semaphore baseQueue = new Semaphore(0);
	static Semaphore summitQueue = new Semaphore(0);
	static Semaphore passengers = new Semaphore(0);
	static Semaphore touristsOnSummit = new Semaphore(50);
	
	public static void main(String[] args) {
		// create and start a station at the base and summit of the mountain
		Station base = new Station(false, baseQueue, passengers, touristsOnSummit, "summit", "base");
		Station summit = new Station(true, summitQueue, passengers, touristsOnSummit, "base", "summit");
		Thread bs = new Thread(base);
		Thread ss = new Thread(summit);
		bs.start();
		ss.start();
		
		// run until both base stations are complete
		try {
			bs.join();
		} catch (InterruptedException ie) {}
		
		try {
			ss.join();
		} catch (InterruptedException ie) {}
	}
}