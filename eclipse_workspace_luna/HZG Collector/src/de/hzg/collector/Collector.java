package de.hzg.collector;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Collector implements Runnable {
	private static final Logger logger = Logger.getLogger(Collector.class.getName());

	@Override
	public void run() {
		try {
			while (true) {
				logger.log(Level.INFO, "Hi");

				Thread.sleep(1000);
			}
		} catch (InterruptedException exception) {
		}
	}
}