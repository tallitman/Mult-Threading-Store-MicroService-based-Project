package bgu.spl.mics.application.passiveObjects;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class InventoryTest {


	@Before
	public void setUp() throws Exception {
		BookInventoryInfo testBook= new BookInventoryInfo("Harry",10,90);
		BookInventoryInfo[] booksInfo = {testBook};
		Inventory.getInstance().load(booksInfo);
	}
	@After
	public void tearDown() {
		BookInventoryInfo[] booksInfo = {};
		Inventory.getInstance().load(booksInfo);
	}
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#getInstance() )()}
         * This is a positive test.
         * We verify that we get same instances with tow different calls to getInstance(), and that mean its a singelton Object.
     */
	@Test
	public void testGetInstance() {
		Inventory inv =Inventory.getInstance();
		Inventory inv2 =Inventory.getInstance();
		assertSame(inv, inv2);
	}
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#load(BookInventoryInfo[])() )()}
         * We verify that load the inserted books.
     */
	@Test
	public void testLoad() {
		
		OrderResult bookFromInv= Inventory.getInstance().take("Harry");
		assertEquals(OrderResult.SUCCESSFULLY_TAKEN, bookFromInv);
		OrderResult bookFromInv2= Inventory.getInstance().take("Potter");
		assertEquals(OrderResult.NOT_IN_STOCK, bookFromInv2);
	}
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#take(String) )()}
         * We verify that the take function is returning the right book from a given Array Of BooksInfo
     */
	@Test
	public void testTake() {
		BookInventoryInfo testBook= new BookInventoryInfo("Harry",10,90);
		BookInventoryInfo[] booksInfo = {testBook};
		Inventory.getInstance().load(booksInfo);
		OrderResult bookFromInv= Inventory.getInstance().take("Harry");
		assertEquals(OrderResult.SUCCESSFULLY_TAKEN, bookFromInv);
		
	}
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#take(String) )()}
         * a negative test - we verify that if we try to take a book that not in the inventory we get "NOT_IN_STOCK"
     */
	@Test
	public void testTakeNotInStock() {
		BookInventoryInfo testBook= new BookInventoryInfo("Harry",10,90);
		BookInventoryInfo[] booksInfo = {testBook};
		Inventory.getInstance().load(booksInfo);
		OrderResult bookFromInv= Inventory.getInstance().take("Potter");
		assertEquals(OrderResult.NOT_IN_STOCK, bookFromInv);
	}
	
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#checkAvailabiltyAndGetPrice() ()}
         * we verify that when we asking for a book name, we get the correct Book price if available.
     */
	@Test
	public void testCheckAvailabiltyAndGetPrice() {
		BookInventoryInfo testBook= new BookInventoryInfo("Harry",10,90);
		BookInventoryInfo[] booksInfo = {testBook};
		Inventory.getInstance().load(booksInfo);
		int result= Inventory.getInstance().checkAvailabiltyAndGetPrice("Harry");
		int exceptedPrice= 90;
		assertEquals(exceptedPrice, result);
	}
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#checkAvailabiltyAndGetPrice() ()}
         * a negative test - we ask for unavailable book we expect the price to be -1
     */
	@Test
	public void testCheckAvailabiltyAndGetPriceNotInStock() {
		BookInventoryInfo testBook= new BookInventoryInfo("Harry",10,90);
		BookInventoryInfo[] booksInfo = {testBook};
		Inventory.getInstance().load(booksInfo);
		int result= Inventory.getInstance().checkAvailabiltyAndGetPrice("Potter");
		int exceptedPrice= -1;
		assertEquals(exceptedPrice, result);
	}
	/**
     * Test method for {@link bgu.spl.mics.application.passiveObjects.Inventory#testPrintInventoryToFile() ()}
        *we creating a file which is a Map of all the books in the inventory, 
		*when we read it back we are verifying that we are getting the same 
        *values of the object we wrote to the file
     */
	@Test
	public void testPrintInventoryToFile() {
		BookInventoryInfo testBook= new BookInventoryInfo("Harry",10,90);
		BookInventoryInfo[] booksInfo = {testBook};
		Inventory.getInstance().load(booksInfo);
		HashMap<String, Integer> exceptedHashMap= new HashMap<>();
		exceptedHashMap.put("Harry", 10);
		Inventory.getInstance().printInventoryToFile("myobject");
		try {
			FileInputStream file= new FileInputStream("myobject");
			ObjectInputStream in= new ObjectInputStream(file);
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> booksAmount= (HashMap<String, Integer>)in.readObject();
			in.close();
			file.close();
			assertEquals(exceptedHashMap, booksAmount);
		}
		catch(IOException ioException) {
			fail("PrintInventoryToFile(): IOException is caught");
		} catch (ClassNotFoundException classNotFoundException) {
			fail("PrintInventoryToFile(): ClassNotFoundException is caught");
		}
	}

}
