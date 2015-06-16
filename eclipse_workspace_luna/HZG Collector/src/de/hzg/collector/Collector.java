package de.hzg.collector;

public class Collector implements Runnable {
	@Override
	public void run() {
		System.out.println("hi");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException exception) {
			System.err.println("Interrupted!");
		}
	}
}
