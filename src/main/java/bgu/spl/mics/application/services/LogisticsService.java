package bgu.spl.mics.application.services;

import java.util.concurrent.CountDownLatch;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to
 * customers. Handles {@link DeliveryEvent}. This class may not hold references
 * for objects which it is not responsible for: {@link ResourcesHolder},
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class. You MAY change
 * constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch fLatch;
	private int fTime;

	public LogisticsService(String name, CountDownLatch latch) {
		super(name);
		this.fLatch = latch;
	}

	/**
	 * Subscribing the LogisticsService to the messageBus
	 * Supplying a callback to each message we subscribed the Mirco-Service 
	 * */
	@Override
	protected void initialize() {
		// subscribing to time tick's broadcasts
		subscribeBroadcast(TickBroadcast.class, tick -> {
			this.fTime = tick.getCurrentTick();
			if (tick.getDuration() == this.fTime) {
				terminate();
			}
		});

		subscribeEvent(DeliveryEvent.class, del -> {
			//trying to acquire a veichle
			AcquireEvent acquire = new AcquireEvent();
			Future<Future<DeliveryVehicle>> deliveryVehicle = sendEvent(acquire);
			if (deliveryVehicle == null) {
				complete(del, false);
				return;
			}
			Future<DeliveryVehicle> delivery = deliveryVehicle.get();

			if (delivery == null) {
				complete(del, false);
				return;
			}
			DeliveryVehicle deliver = delivery.get();
			if (deliver != null) {
				//sending out the delivery
				deliver.deliver(del.getAddress(), del.getDistance());
				complete(del, true);
				ReleaseVehicleEvent release = new ReleaseVehicleEvent(deliver);
				sendEvent(release);
			} else {
				complete(del, false);
			}

		});
		fLatch.countDown();
	}

}
