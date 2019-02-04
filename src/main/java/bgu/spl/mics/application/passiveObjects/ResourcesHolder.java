package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;
import java.util.Queue;

import bgu.spl.mics.Future;

/**
 * Passive object representing the resource manager. You must not alter any of
 * the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton. You must
 * not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private Queue<DeliveryVehicle> fVehicles;
	private Object fLocker;
	private Queue<Future<DeliveryVehicle>> fWaitingFutures;

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class MyWrapper {
		private static ResourcesHolder INSTANCE = new ResourcesHolder();
	}

	private ResourcesHolder() {
		fVehicles = new LinkedList<DeliveryVehicle>();
		fLocker = new Object();
		fWaitingFutures = new LinkedList<Future<DeliveryVehicle>>();
		
	}

	/**
	 * Get the instance of the ResourceHolder, implemented as a Thread-safe singleton
	 *
	 * @return a ResourceHolder SingleTon instance
	 */

	public static ResourcesHolder getInstance() {
		return MyWrapper.INSTANCE;
	}

	/**
	 * Tries to acquire a vehicle and gives a future object which will resolve to a
	 * vehicle.
	 * <p>
	 * 
	 * @return {@link Future<DeliveryVehicle>} object which will resolve to a
	 *         {@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> result = new Future<DeliveryVehicle>();
		synchronized (fLocker) {
			if (!fVehicles.isEmpty()) {
				result.resolve(fVehicles.poll());
				return result;
			} else {
				fWaitingFutures.add(result);
				return result;
			}
		}
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * 
	 * @param vehicle {@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		// if there is futures in the futures queue we will resolve them to the waiting
		synchronized (fLocker) {
			if (!fWaitingFutures.isEmpty()) {
				Future<DeliveryVehicle> waitingFuture = fWaitingFutures.poll();
				waitingFuture.resolve(vehicle);
			} else {
				fVehicles.add(vehicle);
			}
		}
	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * 
	 * @param vehicles Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++) {
			this.fVehicles.add(vehicles[i]);
		}
	}

}
