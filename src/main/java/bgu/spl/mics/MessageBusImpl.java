package bgu.spl.mics;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface. Write your implementation here! Only private fields and methods
 * can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServiceQueueMap; // queue for the
																								// "awayMessage"
	private ConcurrentHashMap<Class<? extends Event<?>>, RoundRobin> eventRoundRobinMap; // roundrobin for every
																							// microservice
	private ConcurrentHashMap<Class<? extends Broadcast>, ArrayList<MicroService>> broadcastServiceMap;// listeners for
																										// each
																										// broadcast
	private ConcurrentHashMap<Event<?>, Future<?>> eventFutureMap;// events and their futures
	private ConcurrentHashMap<MicroService, ArrayList<Class<? extends Message>>> microServiceMessageMap;// microservice
																										// and his
																										// assigned
																										// messages
	private Object lockEvent; // locks for subcribe concurrently
	private Object lockBroadcast;
	private ReadWriteLock readWriteLock; // send events and broadcast concurrently while not

	private static class MyWrapper {
		private static MessageBusImpl INSTANCE = new MessageBusImpl();
	}

	private MessageBusImpl() {
		microServiceQueueMap = new ConcurrentHashMap<>();
		eventRoundRobinMap = new ConcurrentHashMap<>();
		broadcastServiceMap = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		microServiceMessageMap = new ConcurrentHashMap<>();
		lockEvent = new Object();
		lockBroadcast = new Object();
		readWriteLock = new ReentrantReadWriteLock(true);

	};

	public static MessageBusImpl getInstance() {
		return MyWrapper.INSTANCE;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		synchronized (lockEvent) {

			if (eventRoundRobinMap.containsKey(type)) {
				eventRoundRobinMap.get(type).addNext(m);
			} else {
				RoundRobin tempRobin = new RoundRobin();
				tempRobin.addNext(m);
				eventRoundRobinMap.put(type, tempRobin);
			}
			if (microServiceMessageMap.containsKey(m))
				microServiceMessageMap.get(m).add(type);
			else {
				microServiceMessageMap.put(m, new ArrayList<>());
				microServiceMessageMap.get(m).add(type);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (lockBroadcast) {
			if (broadcastServiceMap.containsKey(type)) {
				broadcastServiceMap.get(type).add(m);
			} else {
				ArrayList<MicroService> tempArrayList = new ArrayList<>();
				tempArrayList.add(m);
				broadcastServiceMap.put(type, tempArrayList);
			}
			if (microServiceMessageMap.containsKey(m))
				microServiceMessageMap.get(m).add(type);
			else {
				microServiceMessageMap.put(m, new ArrayList<>());
				microServiceMessageMap.get(m).add(type);
			}
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		@SuppressWarnings("unchecked")
		Future<T> event = (Future<T>) eventFutureMap.get(e);
		event.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		readWriteLock.readLock().lock();
		try {
			for (Class<? extends Broadcast> broadcast : broadcastServiceMap.keySet()) {
				if (b.getClass() == broadcast) {
					ArrayList<MicroService> tempList = new ArrayList<>(broadcastServiceMap.get(broadcast));

					for (MicroService m : tempList) {
						microServiceQueueMap.get(m).add(b);
					}
				}
			}
		} finally {
			readWriteLock.readLock().unlock();
		}

	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		readWriteLock.readLock().lock();
		try {
			Future<T> result = new Future<T>();
			RoundRobin roundRobin = eventRoundRobinMap.get(e.getClass());
			if (roundRobin == null || roundRobin.isEmpty()) {
				return null;
			}
			eventFutureMap.put(e, result);
			MicroService microService = roundRobin.getNext();
			microServiceQueueMap.get(microService).add(e);
			return result;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	@Override
	public void register(MicroService m) {
		if (microServiceQueueMap.containsKey(m))
			return;
		microServiceQueueMap.put(m, new LinkedBlockingQueue<Message>());

	}

	@Override
	public void unregister(MicroService m) {
		readWriteLock.writeLock().lock();
		try {

			LinkedBlockingQueue<Message> q = microServiceQueueMap.get(m);
			while (q!=null &&!q.isEmpty()) {
				Message msg = q.poll();
				if (msg instanceof Event) {
					Future<?> futureToEnd = eventFutureMap.get(msg);
					futureToEnd.resolve(null);
				}
			}
			microServiceQueueMap.remove(m);
			if (microServiceMessageMap.containsKey(m)) {
				ArrayList<?> temp = (ArrayList<Class<? extends Message>>) microServiceMessageMap.get(m);
				for (int k = 0; k < temp.size(); k++) {

					if (Event.class.isAssignableFrom((Class<?>) temp.get(k))) {
						RoundRobin tempRobin = eventRoundRobinMap.get(temp.get(k));
						tempRobin.remove(m);

					} else // must be instanceof broadcast
					{

						ArrayList<MicroService> tempArray = broadcastServiceMap.get(temp.get(k));
						if (tempArray != null)
							tempArray.remove(m);
					}
				}
			}

		} finally {
			readWriteLock.writeLock().unlock();
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException, IllegalStateException {
		if (microServiceQueueMap.get(m) == null)
			throw new IllegalStateException();
		return microServiceQueueMap.get(m).take();
	}

}
