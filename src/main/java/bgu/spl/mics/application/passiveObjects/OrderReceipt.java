package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should be sent to a customer
 * after the completion of a BookOrderEvent. You must not alter any of the given
 * public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public
 * methods).
 */
@SuppressWarnings("serial")
public class OrderReceipt implements Serializable {
	private int fOrderId;
	private static int fCounterId=1;
	private String fSeller;
	private int fCustomer;
	private String fBookTitle;
	private int fPrice;
	private int fIssuedTick;
	private int fOrderTick;
	private int fProccessTick;

	public OrderReceipt(String seller, int customer, String bookTitle, int price, int issuedTick,
			int orderTick, int proccessTick) {
		super();
		this.fOrderId = fCounterId;
		this.fSeller = seller;
		this.fCustomer = customer;
		this.fBookTitle = bookTitle;
		this.fPrice = price;
		this.fIssuedTick = issuedTick;
		this.fOrderTick = orderTick;
		this.fProccessTick = proccessTick;
		fCounterId++;
	}

	/**
	 * Retrieves the orderId of this receipt.
	 */
	public int getOrderId() {
		return fOrderId;
	}

	/**
	 * Retrieves the name of the selling service which handled the order.
	 */
	public String getSeller() {
		return fSeller;
	}

	/**
	 * Retrieves the ID of the customer to which this receipt is issued to.
	 * <p>
	 * 
	 * @return the ID of the customer
	 */
	public int getCustomerId() {
		return fCustomer;
	}

	/**
	 * Retrieves the name of the book which was bought.
	 */
	public String getBookTitle() {
		return fBookTitle;
	}

	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() {
		return fPrice;
	}

	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		return fIssuedTick;
	}

	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {
		return fOrderTick;
	}

	/**
	 * Retrieves the tick in which the treating selling service started processing
	 * the order.
	 */
	public int getProcessTick() {
		return fProccessTick;
	}
}
