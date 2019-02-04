package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store. You must not alter
 * any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public
 * methods).
 */
@SuppressWarnings("serial")
public class Customer implements Serializable {
	private int fId;
	private String fName;
	private String fAddress;
	private int fDistance;
	private List<OrderReceipt> fReceipts;
	private int fCreditCard;
	private int fAvailableAmountInCreditCard;

	public Customer(int id, String name, String address, int distance, int creditCard,
			int availableAmountInCreditCard) {
		super();
		this.fId = id;
		this.fName = name;
		this.fAddress = address;
		this.fDistance = distance;
		this.fCreditCard = creditCard;
		this.fAvailableAmountInCreditCard = availableAmountInCreditCard;
		this.fReceipts = new ArrayList<>();
	}

	@Override
	public String toString() {
		return "Customer [fId=" + fId + ", fName=" + fName + ", fAvailableAmountInCreditCard="
				+ fAvailableAmountInCreditCard + "]";
	}

	/**
	 * Retrieves the name of the customer.
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Retrieves the ID of the customer .
	 */
	public int getId() {
		return fId;
	}

	/**
	 * Retrieves the address of the customer.
	 */
	public String getAddress() {
		return fAddress;
	}

	/**
	 * Retrieves the distance of the customer from the store.
	 */
	public int getDistance() {
		return fDistance;
	}

	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 * <p>
	 * 
	 * @return A list of receipts.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return fReceipts;
	}

	/**
	 * Retrieves the amount of money left on this customers credit card.
	 * <p>
	 * 
	 * @return Amount of money left.
	 */
	public int getAvailableCreditAmount() {
		return fAvailableAmountInCreditCard;
	}

	/**
	 * Retrieves this customers credit card serial number.
	 */
	public int getCreditNumber() {
		return fCreditCard;
	}

	/**
	 * Charge the Customer Credit Card
	 * 
	 */
	public void charge(int amount) {
		fAvailableAmountInCreditCard= fAvailableAmountInCreditCard- amount;
	}

	public void addReceipt(OrderReceipt orderReceipt) {
		fReceipts.add(orderReceipt);
	}

}
