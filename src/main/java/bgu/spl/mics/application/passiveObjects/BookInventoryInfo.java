package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.Serializable;

/**
 * Passive data-object representing a information about a certain book in the
 * inventory. You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public
 * methods).
 */
@SuppressWarnings("serial")
public class BookInventoryInfo implements Serializable {
	private String fBookTitle;
	private int fAmountInInventory;
	private int fPrice;
	private ReadWriteLock rw=new ReentrantReadWriteLock();

	public BookInventoryInfo(String bookTitle, int amountInInventory, int price) {
		this.fBookTitle = bookTitle;
		this.fAmountInInventory = amountInInventory;
		this.fPrice = price;
	}

	/**
	 * Retrieves the title of this book.
	 * <p>
	 * 
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		
		return fBookTitle;
	}

	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * 
	 * @return amount of available books.
	 */
	public int getAmountInInventory() {
		rw.readLock().lock();
		try {
			return fAmountInInventory;
		}
		finally {
			rw.readLock().unlock();
		}
	}

	/**
	 * Retrieves the price for book.
	 * <p>
	 * 
	 * @return the price of the book.
	 */
	public int getPrice() {

		return fPrice;
	}

	/**
	 * Decrease the amount of this book type in the inventory.
	 * <p>
	 * 
	 */
	public void decreaseAmount() {
		rw.writeLock().lock();
		try {
			fAmountInInventory--;
		}
		
		finally {
			rw.writeLock().unlock();
		}
		
}
}
