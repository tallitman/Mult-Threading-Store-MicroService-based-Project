package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
/**
 * CheckAvailabilityEvent is a event sent to the Inventory service in order to check if a certain book is available
 * to order
 */
public class CheckAvailabilityEvent implements Event<Integer>{
	
	private String fBookName;
	
	
	public CheckAvailabilityEvent(String book) {
		this.fBookName = book;
	}
	
	public String getBookName() {
		return fBookName;
	}

	@Override
	public String toString() {
		return "CheckAvailabilityEvent [fBookName=" + fBookName + "]";
	}
	
}
