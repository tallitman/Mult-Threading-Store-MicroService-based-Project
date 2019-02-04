package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
/**
 * ReleaseVehicleEvent is a event sent to the resource service in order to relsease a veichle
 * that finished a delivery and now available again
 * the Class fields represent the released veichle
 */
public class ReleaseVehicleEvent implements Event<Boolean> {
	
	private DeliveryVehicle fVehicle;
	public ReleaseVehicleEvent(DeliveryVehicle veichle) {
		this.fVehicle = veichle;
	}
	
	  /**
		 * Retrieves a DeliveryVehicle representing available Vehicle
		 * <p>
		 * @return {@link DeliveryVehicle} representing the book name
		 */
	public DeliveryVehicle getVehicle() {
		return fVehicle;
	}
	@Override
	public String toString() {
		return "Release Event [fVehicle=" + fVehicle + "]";
	}
	
	
}
