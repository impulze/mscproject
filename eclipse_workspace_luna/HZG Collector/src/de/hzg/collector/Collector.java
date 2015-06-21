package de.hzg.collector;

import java.util.logging.Logger;

import de.hzg.sensors.BaseSensor;

public class Collector implements Runnable {
	private static final Logger logger = Logger.getLogger(Collector.class.getName());

	@Override
	public void run() {
		try {
			logger.finest("entered run() loop");
			BaseSensor baseSensor = new BaseSensor();

			while (true) {
				baseSensor.logAnother();
				Thread.sleep(1000);
			}
		} catch (InterruptedException exception) {
		}
	}
}