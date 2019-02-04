package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {
	private String bookTitle;
	private int tick;

	/**
	 * Passive object representing the a book Order Schedule.
	 * <p>
	 */
	public OrderSchedule(String bookTitle, int tick) {
		this.bookTitle = bookTitle;
		this.tick = tick;
	}

	/**
	 * Get the book title
	 *
	 * @return {@link String} representing a the book title
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
	 * Get the book order tick
	 *
	 * @return and integer value represent the time that the book have been ordered
	 */
	public int getTick() {
		return tick;
	}
	
	
	
}
