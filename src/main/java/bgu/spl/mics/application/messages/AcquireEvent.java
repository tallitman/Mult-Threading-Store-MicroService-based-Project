package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * AcquireEvent is An event that is sent when we need to acquire a vehicle in order the send the book the
 * a customer once the order have been completed
 * 
 * */
public class AcquireEvent implements Event<Future<DeliveryVehicle>> {

	@Override
	public String toString() {
		return "AcquireEvent ";
	}

}
