package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Selling service in charge of taking orders from customers. Holds a reference
 * to the {@link MoneyRegister} singleton of the store. Handles
 * {@link BookOrderEvent}. This class may not hold references for objects which
 * it is not responsible for: {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class. You MAY change
 * constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
	private CountDownLatch fLatch;
	private int fTime;

	public SellingService(String name, CountDownLatch latch) {
		super(name);
		this.fLatch = latch;
	}

	@Override
	protected void initialize() {

		// subscribing to time tick's broadcasts
		subscribeBroadcast(TickBroadcast.class, tick -> {
			this.fTime = tick.getCurrentTick();
			if (tick.getDuration()  == this.fTime) {
				terminate();
			}
		});

		subscribeEvent(BookOrderEvent.class, order -> {
			
			int proccesTick = this.fTime;// collecting receipt information
			// check getting book price if available
			String orderedBook = order.getBookTitle();
			CheckAvailabilityEvent check = new CheckAvailabilityEvent(orderedBook);
			Future<Integer> futurePrice = sendEvent(check);
			if(futurePrice==null) {
				complete(order, null);  
				return;
			}				
			Integer price = futurePrice.get();
			// Customer handling - locking from other threads the ability to charge together
			// the same customer
			// verifying the customer has enough money
			Customer customer = order.getCustomer();
			synchronized (customer) {
				int balance = customer.getAvailableCreditAmount();
				if (balance >= price && price!=-1) {// customer has enough money
					TakeBookEvent takeEvent = new TakeBookEvent(orderedBook);
					Future<OrderResult> takeFuture = sendEvent(takeEvent);
					if(takeFuture == null) {
						complete(order, null);  
						return;
					}
					OrderResult result = takeFuture.get();
					if (result == OrderResult.SUCCESSFULLY_TAKEN) {
						MoneyRegister.getInstance().chargeCreditCard(customer, price);
						OrderReceipt orderReceipt = new OrderReceipt(getName(), customer.getId(),
								order.getBookTitle(), price, proccesTick, order.getTickReq(), proccesTick);
						MoneyRegister.getInstance().file(orderReceipt);
						complete(order, orderReceipt);
						DeliveryEvent delivery = new DeliveryEvent(customer.getAddress(), customer.getDistance());
						sendEvent(delivery);
						
					} else if (result == OrderResult.NOT_IN_STOCK) {
						complete(order, null);
					}

				} else
					complete(order, null);  
			}

		});

		fLatch.countDown();
	}

}
