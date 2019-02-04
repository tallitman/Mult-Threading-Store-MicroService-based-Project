package bgu.spl.mics;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import bgu.spl.mics.example.messages.*;
import bgu.spl.mics.example.services.ExampleEventHandlerService;

public class MessageBusTest {
	MicroService tal;
	private ExampleEvent event;
	private ExampleBroadcast broadcast;

	@Before
	public void setUp() throws Exception {
		tal = new ExampleEventHandlerService("tal", new String[] { "2" });
		event = new ExampleEvent("Menahem eve");
		broadcast = new ExampleBroadcast("Menahem bro");

	}

	@After
	public void tearDown() {
		MessageBusImpl.getInstance().unregister(tal);
	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBusImpl#getInstance() )()} This is
	 * a positive test. We verify that we get same instances with tow different
	 * calls to getInstance(), and that mean its a singelton Object.
	 */
	@Test
	public void testGetInstance() {
		MessageBusImpl m1 = MessageBusImpl.getInstance();
		MessageBusImpl m2 = MessageBusImpl.getInstance();
		assertSame(m1, m2);
	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBusImpl#subscribeEvent(Class,
	 * MicroService)() )()} This is a positive test. We verify that after some
	 * microservice calls subcribeEvent, he will get a event with the correct type.
	 */
	@Test
	public void testSubscribeEvent() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeEvent(event.getClass(), tal);
		MessageBusImpl.getInstance().sendEvent(event);
		try { // trying to send a message to the registered microservice
			Message m = MessageBusImpl.getInstance().awaitMessage(tal);
			assertEquals(event, m);
		} catch (IllegalStateException e) {
			fail("SubscribeEvent(): The MicroService " + tal.getName() + " has never registered.");

		} catch (InterruptedException e) {
			fail("SubscribeEvent(): interrupted while waiting for a message ");
		}

	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBusImpl#subscribeBroadcast(Class,
	 * MicroService)() )()} This is a positive test. We verify that after some
	 * microservice calls subscribeBroadcast, he will get a broadcast with the
	 * correct type.
	 */
	@Test
	public void testSubscribeBroadcast() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeBroadcast(broadcast.getClass(), tal);
		MessageBusImpl.getInstance().sendBroadcast(broadcast);
		try { // trying to send a message to the registered microservice
			Message m = MessageBusImpl.getInstance().awaitMessage(tal);
			assertEquals(broadcast, m);
		} catch (IllegalStateException e) {
			fail("SubscribeBroadcast(): The MicroService " + tal.getName() + " has never registered.");

		} catch (InterruptedException e) {
			fail("SubscribeBroadcast(): interrupted while waiting for a message ");
		}
	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBus#complete(Event, Object) )()}
	 * We verify that after some microservice calls complete method, the future
	 * object will be resolved with the transferred result.
	 */
	@Test
	public void testComplete() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeEvent(event.getClass(), tal);
		Future<String> future = MessageBusImpl.getInstance().sendEvent(event);
		MessageBusImpl.getInstance().complete(event, "yabalulu");
		Future<String> futureExecpted = new Future<>();
		futureExecpted.resolve("yabalulu");
		assertEquals(futureExecpted.get(), future.get());

	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBus#sendBroadcast(Broadcast))()}
	 * We verify that after some microservice calls sendBroadcast method, some other
	 * microservice get this broadcast.
	 */
	@Test
	public void testSendBroadcast() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeBroadcast(broadcast.getClass(), tal);
		MessageBusImpl.getInstance().sendBroadcast(broadcast);
		try {
			Message m = MessageBusImpl.getInstance().awaitMessage(tal);
			assertEquals(broadcast, m);
		} catch (InterruptedException e) {
			fail("testSendBroadcast(): interrupted while waiting for a message ");
			e.printStackTrace();
		}

	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBus#sendEvent(Event))()} We verify
	 * that after some microservice calls sendEvent method, some other microservice
	 * get this Event.
	 */
	@Test
	public void testSendEvent() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeEvent(event.getClass(), tal);
		MessageBusImpl.getInstance().sendEvent(event);
		try {
			Message m = MessageBusImpl.getInstance().awaitMessage(tal);
			assertEquals(event, m);
		} catch (InterruptedException e) {
			fail("SendEvent(): interrupted while waiting for a message ");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBus#register(MicroService))()} We
	 * verify that after some microservice register to the messageBus, we can
	 * subscribe him and send him a message.
	 */
	@Test
	public void testRegister() {
		ExampleEventHandlerService newService = new ExampleEventHandlerService("tal", new String[] { "2" });
		MessageBusImpl.getInstance().register(newService);
		MessageBusImpl.getInstance().subscribeEvent(event.getClass(), newService);
		MessageBusImpl.getInstance().sendEvent(event);
		try { // trying to send a message to the registered microservice
			Message m = MessageBusImpl.getInstance().awaitMessage(newService);
			assertEquals(event, m);
		} catch (IllegalStateException e) {
			fail("register(): The MicroService " + newService.getName() + " has never registered.");

		} catch (InterruptedException e) {
			fail("register(): interrupted while waiting for a message ");
		}

		MessageBusImpl.getInstance().unregister(newService);
	}

	/**
	 * Test method for
	 * {@link bgu.spl.mics.MessageBus#unregister(MicroService)(Event))()} We verify
	 * that after some microservice is unregistered from the message bus, tyring to
	 * find message of him give us an exception.
	 */
	@Test
	public void testUnregister() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeEvent(event.getClass(), tal);
		MessageBusImpl.getInstance().sendEvent(event);
		MessageBusImpl.getInstance().unregister(tal);
		try {
			@SuppressWarnings("unused")
			Message m = MessageBusImpl.getInstance().awaitMessage(tal);
			fail("Unregister(): IllegalStateException exepcted!");
		} catch (IllegalStateException e) {
			// test passed

		} catch (InterruptedException e) {
			fail("Unregister(): interrupted while waiting for a message ");

		}

	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBus#awaitMessage(MicroService))()}
	 * This is a positive test. We verify that after some microservice calls
	 * AwaitMessage() , and there is already messages in his queue, he will get the
	 * correct message.
	 */
	@Test
	public void testAwaitMessage() {
		MessageBusImpl.getInstance().register(tal);
		MessageBusImpl.getInstance().subscribeEvent(event.getClass(), tal);
		MessageBusImpl.getInstance().sendEvent(event);
		try {
			Message m = MessageBusImpl.getInstance().awaitMessage(tal);
			assertEquals(event, m);
		} catch (InterruptedException e) {
			fail("AwaitMessage(): interrupted while waiting for a message ");
			e.printStackTrace();
		}

	}

	/**
	 * Test method for {@link bgu.spl.mics.MessageBus#awaitMessage(MicroService))()}
	 * This is a negative test- cause an exception to be thrown. We verify that
	 * after some microservice calls AwaitMessage() and there is no messages in his
	 * queue, and some one interrupt him while waiting
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testAwaitMessageNegative() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				MessageBusImpl.getInstance().register(tal);
				MessageBusImpl.getInstance().sendEvent(event);
				try {
					@SuppressWarnings("unused")
					Message m = MessageBusImpl.getInstance().awaitMessage(tal);
					fail("Exception expected!");
				} catch (IllegalStateException e) {
					// test passed

				} catch (InterruptedException e) {
					// test pass
				}

			}
		});
		t.start();
		try {
			Thread.currentThread().sleep(2000);
			t.interrupt();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
