package bgu.spl.mics;

import java.util.ArrayList;
import java.util.Comparator;

public class RoundRobin {
	private ArrayList<MicroService> microServiceList;
	private int currentRunner;

	public RoundRobin() {
		this.currentRunner = -1;
		microServiceList = new ArrayList<>();
	}

	public synchronized MicroService getNext() {
		if (isEmpty())
			return null;
		currentRunner = (currentRunner + 1) % microServiceList.size();
		return microServiceList.get(currentRunner);
	}

	public synchronized void addNext(MicroService m) {
		if (!microServiceList.contains(m))
			microServiceList.add(m);
		sort();
	}

	public synchronized void remove(MicroService m) {
		microServiceList.remove(m);
		sort();
	}

	public boolean isEmpty() {
		return microServiceList.isEmpty();
	}

	private void sort() {
		microServiceList.sort(new Comparator<MicroService>() {

			@Override
			public int compare(MicroService m1, MicroService m2) {
				return extractInt(m1.getName()) - extractInt(m2.getName());
			}

			int extractInt(String s) {
				String num = s.replaceAll("\\D", "");
				// return 0 if no digits found
				return num.isEmpty() ? 0 : Integer.parseInt(num);
			}
		});

	}

}
