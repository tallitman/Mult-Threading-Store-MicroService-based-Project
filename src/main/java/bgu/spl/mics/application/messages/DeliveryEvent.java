package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
/**
 * DeliveryEvent is a event sent to the Logistic-Service in order to execute a delivery
 * once the order event is handled properly
 * @param fDistance represent the customer fDistance from the store
 * @param fAddress represent the customer fDistance from the store
 */
public class DeliveryEvent implements Event<Boolean> {
	private int fDistance;
	private String fAddress;
	public DeliveryEvent(String address,int distance) {
		super();
		this.fDistance = distance;
		this.fAddress = address;
	}

	public String getAddress() {
		return fAddress;
	}

	public int getDistance() {
		return fDistance;
	}

	@Override
	public String toString() {
		return "DeliveryEvent [fDistance=" + fDistance + ", fAddress=" + fAddress + "]";
	}
	

}
