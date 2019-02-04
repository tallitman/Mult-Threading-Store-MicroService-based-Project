package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
/**
 * TickBroadcast is a boradcast send to each micreservice in uot application 
 * the Tick broadcast reprsent to current tick and the program duration
 */

public class TickBroadcast implements Broadcast {
    private int fCurrentTick;
    private int fDuration;
    public TickBroadcast(int currentTick,int duration) {
  	  fCurrentTick = currentTick;
  	  fDuration=duration;
    }
    /**
	 * Retrieves the current time tick
	 * <p>
	 * @return an integer representing the current tick
	 */
    public int getCurrentTick() {
  	  return fCurrentTick;
    }
    /**
	 * Retrieves the program duration time
	 * <p>
	 * @return an integer representing the program duration 
	 */
    public int getDuration() {
    	return fDuration;
    }
    

}