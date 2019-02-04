package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * BookOrderEvent is An event that is sent when a client of the store wishes to buy a book
 * The event holding the customer info for creating a reciept once the ordeer have been completed
 * */

public class BookOrderEvent implements Event<OrderReceipt> {

	private String fBookTitle;
	private int fTickReq;
	private Customer fCustomer;


	public BookOrderEvent(String bookTitle, int tickReq, Customer c) {
		super();
		this.fBookTitle = bookTitle;
		this.fTickReq = tickReq;
		this.fCustomer = c;

	}

	public String getBookTitle() {
		return fBookTitle;
	}

	public int getTickReq() {
		return fTickReq;
	}

	public Customer getCustomer() {
		return fCustomer;
	}

	public String getfBookTitle() {
		return fBookTitle;
	}

	public int getfTickReq() {
		return fTickReq;
	}

	public Customer getfCustomer() {
		return fCustomer;
	}

	@Override
	public String toString() {
		return "BookOrderEvent "+fBookTitle;
	}

}
