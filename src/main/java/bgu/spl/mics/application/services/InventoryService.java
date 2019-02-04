package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock. Holds a
 * reference to the {@link Inventory} singleton of the store. This class may not
 * hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class. You MAY change
 * constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService {
	private CountDownLatch fLatch;
	private int fTime;

	
	public InventoryService(String name, CountDownLatch latch) {
		super(name);
		this.fLatch = latch;
	}

	@Override
	/**
	 * Subscribing the InventoryService to the messageBus
	 * Supplying a callback to each message we subscribed the Mirco-Service 
	 * */
	protected void initialize() {
		// subscribing to time tick's broadcasts
		subscribeBroadcast(TickBroadcast.class, tick -> {
			this.fTime = tick.getCurrentTick();
			if (tick.getDuration() == this.fTime) {
				terminate();
			}
		});
		//subscribing a check availability event which represent a request to verify
		//if a certain book is available to purchase
		subscribeEvent(CheckAvailabilityEvent.class, availabilityEvent -> {
			String bookName = availabilityEvent.getBookName();
			int price = Inventory.getInstance().checkAvailabiltyAndGetPrice(bookName);
			Integer result = new Integer(price);
			complete(availabilityEvent, result);
		});
		subscribeEvent(TakeBookEvent.class, takeBookEvent -> {
			String bookName = takeBookEvent.getBookName();
			OrderResult result= Inventory.getInstance().take(bookName);
			complete(takeBookEvent, result);
		});
		fLatch.countDown();
	}

}
