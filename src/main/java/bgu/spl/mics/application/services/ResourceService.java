package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store. This
 * class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class. You MAY change
 * constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
	private ResourcesHolder fResourcesHolder;
	private CountDownLatch fLatch;
	private int fTime;
	private ArrayList<Future<DeliveryVehicle>> futures;

	public ResourceService(String name, CountDownLatch latch) {
		super(name);
		this.fLatch = latch;
		fResourcesHolder = ResourcesHolder.getInstance();
		futures = new ArrayList<>();
	}

	@Override
	protected void initialize() {
		// subscribing to time tick's broadcasts
		subscribeBroadcast(TickBroadcast.class, tick -> {
			this.fTime = tick.getCurrentTick();
			if (tick.getDuration() == this.fTime) {
				terminate();
				for (Future<DeliveryVehicle> f : futures) {
					if (!f.isDone())
						f.resolve(null);
				}
			}
		});
		subscribeEvent(ReleaseVehicleEvent.class, released -> {
			fResourcesHolder.releaseVehicle(released.getVehicle());
		});
		subscribeEvent(AcquireEvent.class, acquire -> {
			Future<DeliveryVehicle> vehicle = fResourcesHolder.acquireVehicle();
			futures.add(vehicle);
			complete(acquire, vehicle);
		});
		fLatch.countDown();
	}

}
