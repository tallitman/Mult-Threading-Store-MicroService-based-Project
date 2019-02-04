package bgu.spl.mics.application.services;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this
 * micro-service. It keeps track of the amount of ticks passed since
 * initialization and notifies all other micro-services about the current time
 * tick using {@link Tick Broadcast}. This class may not hold references for
 * objects which it is not responsible for: {@link ResourcesHolder},
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class. You MAY change
 * constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private int fSpeed;
	private int fDuration;
	private int fCurrentTime;
	private Timer fTimer;
	private CountDownLatch fLatch;

	public TimeService(int speed, int duration, CountDownLatch latch) {
		super("TimeService");
		fSpeed = speed;
		fDuration = duration;
		fLatch = latch;
		fTimer = new Timer();
		fCurrentTime = 0;
	}
	/**
	 * the isDone function is used to check if the program time has ended 
	 * and need to stop sending the tick broadcasts	
	 * 
	 * @return boolean - returns true if the current tick is equals to the program duration
	 * */
	public boolean isDone() {
		return fCurrentTime == fDuration;
	}

	@Override
	protected void initialize() {
		try {
			fLatch.await();
			/**
			 * TimeServiceTask is the task that our program timer need to run each Tick
			 * 
			 * */
			class TimeServiceTask extends TimerTask {
				public void run() {
					fCurrentTime++;
					if (fCurrentTime >= fDuration + 1) { 
						this.cancel();
						fTimer.cancel();
						terminate();

					} else {
						sendBroadcast(new TickBroadcast(fCurrentTime, fDuration));
					}
				}
			}
			fTimer.schedule(new TimeServiceTask(), 0, fSpeed);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		subscribeBroadcast(TickBroadcast.class, tick -> {
			if (tick.getDuration() == fCurrentTime) {
				fTimer.cancel();
				terminate();
			}
		});
	}

}
