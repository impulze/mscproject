package de.hzg.sensors;

import java.util.logging.Logger;

public class BaseSensor {
	private static final Logger logger = Logger.getLogger(BaseSensor.class.getName());

	public BaseSensor() {
		logger.info("BaseSensor created");
	}

	public void logAnother() {
		logger.info("logAnother");
	}
}