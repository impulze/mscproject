package de.hzg.collector;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.hzg.sensors.BaseSensor;

public class Collector implements Runnable {
	private static final Logger logger = Logger.getLogger(Collector.class.getName());

	@Override
	public void run() {
		try {
			while (true) {
				BaseSensor baseSensor = new BaseSensor();
				logger.log(Level.INFO, "Hi");

				Thread.sleep(1000);
			}
		} catch (InterruptedException exception) {
		}
	}
}