package bgu.spl.mics;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class FutureTest {
	Future<String> future;

	@Before
	public void setUp() throws Exception{
		future = new Future<String>();
	}
	/**
     * Test method for {@link bgu.spl.mics.Future#get()}
         * This is a positive test.
         * We verify that the get method return us the result when the future is already resloved.
     */
	@Test
	public void testGet() {
		future.resolve("this is a test");
		String expectedVal= "this is a test";
		assertEquals(expectedVal, future.get());
	
	}
	/**
     * Test method for {@link bgu.spl.mics.Future#resolve(Object)()}
         * This is a positive test.
         * We verify that the resolve method makes the isDone() return true when called.
     */
	@Test
	public void testResolve() {
		boolean preResolve = future.isDone();
		future.resolve("this is a test");
		assertFalse(preResolve==future.isDone());
	}

	/**
     * Test method for {@link bgu.spl.mics.Future#isDone()}
         * This is a positive test.
         * We verify that isDone method return us the excepted result.
     */
	@Test
	public void testIsDone() {
		
		boolean isDonePre = future.isDone();
		future.resolve("some tests");
		boolean isDoneAfter= future.isDone();
		assertFalse(isDoneAfter==isDonePre);
	}
	/**
     * Test method for {@link bgu.spl.mics.Future#get(long, TimeUnit)()}
         * This is a positive test.
         * We verify that the get method return null because no one was resolve the future
     */
	@Test
	public void testGetLongTimeUnit() throws InterruptedException {
		TimeUnit tu= TimeUnit.SECONDS;
		long timeout= 2;
		long time= System.currentTimeMillis();
		String result = future.get(timeout, tu);
		long time2= System.currentTimeMillis();
		assertTrue(time2-time>=timeout && result==null );

	}
	/**
     * Test method for {@link bgu.spl.mics.Future#get(long, TimeUnit)()}
         * This is a positive test.
         * We verify that the get method return us the correct result after we solved the future object.
     */
	@Test
	public void testGetLongTimeUnitPositive() throws InterruptedException {
		TimeUnit tu= TimeUnit.SECONDS;
		long timeout= 2;
		TimeUnit.SECONDS.sleep(timeout);
		future.resolve("test");
		String result = future.get(timeout, tu);
		assertEquals("test", result);
	}
}
