package erebor;

//Jessica Braddon-Parsons - 13219524

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import erebor.Tourist;

public class Station implements Runnable {
	int numberOfTourists = 100;
	int passengersPermittedToTravel = 10;
	int passengersArrived = 0;
	Semaphore totalPassengers = new Semaphore(numberOfTourists);
	Tourist[] tourists = new Tourist[numberOfTourists];
	Thread[] t = new Thread[numberOfTourists];
	
	boolean top;
	Semaphore queue;
	Semaphore passengers;
	Semaphore touristsOnSummit;
	String side, otherSide;

	Station(boolean top, Semaphore queue, Semaphore passengers, Semaphore touristsOnSummit, String otherSide, String side) {
		this.top = top;
		this.queue = queue;
		this.passengers = passengers;
		this.touristsOnSummit = touristsOnSummit;
		this.otherSide = otherSide;
		this.side = side;
	}

	public void run() {
		// create 500 tourists. Start them if this is the base station
		for (int i=0; i<numberOfTourists; i++) {
			tourists[i] = new Tourist(top, queue);
			t[i] = new Thread(tourists[i]);
			if (!top) {
				t[i].start();
			}
		}

		while (totalPassengers.availablePermits() > 0 || touristsOnSummit.availablePermits() < 50) { // there are passengers left to move from this side of the mountain, or passengers still at the top
			if (top == CableCar.top) {
				System.out.println("The cable car is at the " + side + " of the mountain.");
				// if the cable car is at the base make sure the people who board won't go over the summit tourist limit
				if (!top) {
					passengersPermittedToTravel = (touristsOnSummit.availablePermits());
					if (passengersPermittedToTravel > 10) {
						passengersPermittedToTravel = 10;
					}
				} else {
					// if the cable tar is at the top, start a tourist thread for every passenger that came up and adjust semaphores 
					for (int i=0; i<passengers.availablePermits(); i++) {
						try {
							touristsOnSummit.acquire();
							t[passengersArrived].start();
							passengersArrived++;
						} catch (InterruptedException ie) {}
					} 
				}
				// reset the passenger count to 0
				passengers.drainPermits();
				
				// try to pick up 10 passengers from the queue 
				for (int i=0; i<passengersPermittedToTravel; i++) {
					try {
						if (queue.tryAcquire(1, 100, TimeUnit.MILLISECONDS)) {
							passengers.release();
							totalPassengers.acquire();
							// if at the summit release the number of tourists there so more can come up
							if (top) {
								touristsOnSummit.release();
							}
							
						}
					} catch (InterruptedException ie) {}
				}
				System.out.println("The cable car leaves with " + passengers.availablePermits() + " passengers to the " + otherSide + " of the mountain.");

				// move the cable car to the other side of the mountain
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {}
				CableCar.top = !top;
			}
		}

		System.out.println("No more cable cars will run from the " + side + " of the mountain.");
	}
}