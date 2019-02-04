package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * TakeBookEvent is a event send to the Inventory Service in order to take a book from the inventory
 */


public class TakeBookEvent implements Event<OrderResult> {
	private String fBookName;
	//book represents the book name
	public TakeBookEvent(String book) {
		this.fBookName = book;
	}
	  /**
		 * Retrieves a String representing the book name
		 * <p>
		 * @return {@link String} representing the book name
		 */
	public String getBookName() {
		return fBookName;
	}

	@Override
	public String toString() {
		return "takeBookEvent [fBookName=" + fBookName + "]";
	}
	
}
