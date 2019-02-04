package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;

/**
 * APIService is in charge of the connection between a client and the store. It
 * informs the store about desired purchases using {@link BookOrderEvent}. This
 * class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class. You MAY change
 * constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	private ArrayList<OrderSchedule> fOrderSchedule;
	private ArrayList<BookOrderEvent> fBooksOrder;
	private ArrayList<Future<OrderReceipt>> fFutures;
	private CountDownLatch fLatch;
	private Customer fCusomter;
	private int fTime;

	public APIService(Customer c, ArrayList<OrderSchedule> orderSchedule, CountDownLatch latch) {
		super(c.getName());
		this.fOrderSchedule = orderSchedule;
		this.fBooksOrder = new ArrayList<>();
		this.fFutures= new ArrayList<>();
		this.fLatch= latch;
		this.fCusomter=c;

	}

	/**
	 * Subscribing the APIService to the messageBus
	 * Supplying a callback to each message we subscribed the Mirco-Service 
	 * */
	@Override
	protected void initialize() {
		//sort the order schedualte by the time Tick that it need to be ordered
		fOrderSchedule.sort((OrderSchedule1, OrderSchedule2) -> {
			return (OrderSchedule1.getTick() - OrderSchedule2.getTick());
		});

		subscribeBroadcast(TickBroadcast.class, tick -> {
			fTime = tick.getCurrentTick();
			if (tick.getDuration() == fTime) {
				terminate();
			}
			//trying to to order each book 
			for (int i = 0; i < fOrderSchedule.size(); i++) {
				OrderSchedule orderSchedule = fOrderSchedule.get(i);
				if (orderSchedule.getTick() > this.fTime)
					break;
				if (orderSchedule.getTick() == this.fTime) {
					BookOrderEvent newOrder = new BookOrderEvent(orderSchedule.getBookTitle(),orderSchedule.getTick(),fCusomter);
					fBooksOrder.add(newOrder);
					Future<OrderReceipt> futureObject = sendEvent(newOrder);
					if(futureObject!=null)
					fFutures.add(futureObject);
					
				}
			}
			ArrayList<Future<OrderReceipt>> futures= new ArrayList<>(fFutures);
			for(int i=0;i < futures.size();i++) {
				
				Future<OrderReceipt> futureObject= futures.get(i);
				if(futureObject!=null &&futureObject.isDone()) {
					OrderReceipt orderReceipt= futureObject.get();
					if(orderReceipt!=null)
					{
					fCusomter.addReceipt(orderReceipt);
					fFutures.remove(futureObject);
					}
				}
			}
		
			

		});
		//when we finish initialize our service we need our program to know in order to run safely execute
		fLatch.countDown();
	}

}
