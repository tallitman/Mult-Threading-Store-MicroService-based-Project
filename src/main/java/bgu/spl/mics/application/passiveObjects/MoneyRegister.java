package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.CopyOnWriteArrayList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Passive object representing the store finance management. It should hold a
 * list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton. You must
 * not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
@SuppressWarnings("serial")
public class MoneyRegister implements Serializable {
	private CopyOnWriteArrayList<OrderReceipt> fReceipt;

	private static class MoneyRegisterWrapper {
		static MoneyRegister INSTANCE = new MoneyRegister();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return MoneyRegisterWrapper.INSTANCE;
	}

	private MoneyRegister() {
		fReceipt = new CopyOnWriteArrayList<OrderReceipt>();
	}

	/**
	 * Saves an order receipt in the money register.
	 * <p>
	 * 
	 * @param r The receipt to save in the money register.
	 */
	public void file(OrderReceipt r) {
		if (!fReceipt.contains(r)) {
			fReceipt.add(r);
		}
	}

	/**
	 * Retrieves the current total earnings of the store.
	 */
	public int getTotalEarnings() {
		int ans = 0;
		for (OrderReceipt r : fReceipt) {
			ans += r.getPrice();
		}
		return ans;
	}

	/**
	 * Charges the credit card of the customer a certain amount of money.
	 * <p>
	 * 
	 * @param amount amount to charge
	 */
	public void chargeCreditCard(Customer c, int amount) {
		c.charge(amount);
	}

	/**
	 * Prints to a file named @filename a serialized object List<OrderReceipt> which
	 * holds all the order receipts currently in the MoneyRegister This method is
	 * called by the main method in order to generate the output..
	 */
	public void printOrderReceipts(String filename) {
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(fReceipt);
			out.close();
			file.close();
		} catch (IOException ex) {
			System.out.println("IOEXception is caught When Trying To Write The MoneyRegister");
		}
	}

}
