package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory. It holds a collection
 * of {@link BookInventoryInfo} for all the books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton. You must
 * not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private ConcurrentHashMap<String, BookInventoryInfo> fBooksHash;

	/**
	 * Retrieves the single instance of this class.
	 */
	private static class MyWrapper {
		private static Inventory INSTANCE = new Inventory();
	}

	private Inventory() {
		fBooksHash = new ConcurrentHashMap<String, BookInventoryInfo>();
	}
	/**
	 * Get the instance of the Inventory, implemented as a Thread-safe singleton
	 *
	 * @return a Inventory SingleTon instance
	 */

	public static Inventory getInstance() {
		return MyWrapper.INSTANCE;
	}

	/**
	 * Initializes the store inventory. This method adds all the items given to the
	 * store inventory.
	 * <p>
	 * 
	 * @param inventory Data structure containing all data necessary for
	 *                  initialization of the inventory.
	 */
	public void load(BookInventoryInfo[] inventory) {

		for (int i = 0; i < inventory.length; i++) {
			fBooksHash.put(inventory[i].getBookTitle(), inventory[i]);
		}
	}

	/**
	 * Attempts to take one book from the store.
	 * <p>
	 * 
	 * @param book Name of the book to take from the store
	 * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN. The
	 *         first should not change the state of the inventory while the second
	 *         should reduce by one the number of books of the desired type.
	 */
	public OrderResult take(String book) {
		BookInventoryInfo tBook = fBooksHash.get(book);
		if (tBook == null)
			return OrderResult.NOT_IN_STOCK;
		synchronized (tBook) {
			if (tBook == null || tBook.getAmountInInventory() == 0)
				return OrderResult.NOT_IN_STOCK;
			else {
				tBook.decreaseAmount();
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
		}

	}

	/**
	 * Checks if a certain book is available in the inventory.
	 * <p>
	 * 
	 * @param book Name of the book.
	 * @return the price of the book if it is available, -1 otherwise.
	 */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo tBook = fBooksHash.get(book);
		if (tBook == null)
			return -1;
		if (tBook.getAmountInInventory() == 0) {
			return -1;
		} else
			return tBook.getPrice();
	}

	/**
	 * 
	 * <p>
	 * Prints to a file name @filename a serialized object HashMap<String,Integer>
	 * which is a Map of all the books in the inventory. The keys of the Map (type
	 * {@link String}) should be the titles of the books while the values (type
	 * {@link Integer}) should be their respective available amount in the
	 * inventory. This method is called by the main method in order to generate the
	 * output.
	 */
	public void printInventoryToFile(String filename) {
		HashMap<String, Integer> booksAmounts = new HashMap<>();
		for (BookInventoryInfo books : fBooksHash.values()) {
			booksAmounts.put(books.getBookTitle(), books.getAmountInInventory());
		}
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(booksAmounts);
			out.close();
			file.close();

		} catch (IOException ex) {
			System.out.println("IOEXception is caught When Trying To Write The Inventory");
		}
	}

	@Override
	public String toString() {
		for (BookInventoryInfo mybook : fBooksHash.values())
			System.out.println(mybook.getAmountInInventory());
		return "";
	}

}
