package de.hzg.collector;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.hzg.common.ExceptionUtil;
import de.hzg.sensors.Probe;
import de.hzg.sensors.Sensor;
import de.hzg.sensors.SensorDescription;
import de.hzg.sensors.SensorInstance;

public class ProbeHandler implements Runnable {
	private static final Logger logger = Logger.getLogger(ProbeHandler.class.getName());
	private final Probe probe;
	private final Communicator communicator;
	private final RawDataInputStream rawDataInputStream;
	private boolean shutdown = false;
	private boolean finished = false;
	private final Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
	private Set<Integer> missingSensors = new HashSet<Integer>();

	public ProbeHandler(Probe probe, ClassLoader classLoader) throws ProbeHandlerSetupException {
		this.probe = probe;

		try {
			this.communicator = new Communicator();
		} catch (CommunicatorSetupException exception) {
			logger.severe("Cannot setup communicator for probe handler with probe '" + probe.getName() + "'");
			throw new ProbeHandlerSetupException();
		}

		try {
			final InputStream inputStream = communicator.getInputStream();
			rawDataInputStream = new RawDataInputStream(inputStream);
		// TODO: this is for development only now
		} catch (Throwable exception) {
			communicator.end();
			throw exception;
		}

		final List<SensorInstance> sensorInstances = probe.getSensorInstances();

		for (final SensorInstance sensorInstance: sensorInstances) {
			final SensorDescription sensorDescription = sensorInstance.getDescription();

			// class name for the calculations
			final String className = sensorDescription.getClassName();
			final Class<?> sensorClass;

			try {
				sensorClass = classLoader.loadClass(className);
			} catch (ClassNotFoundException exception) {
				logger.severe("The probe handler with probe '" + probe.getName() + "' uses formula class '" + className + "' which cannot be loaded.");
				throw new ProbeHandlerSetupException();
			}

			final Object sensorObject;

			try {
				sensorObject = sensorClass.newInstance();
			} catch (InstantiationException|IllegalAccessException exception) {
				logger.severe("The formula class '" + sensorClass.getName() + "' used in probe handler with probe '" + probe.getName() + "' is not suitable for instantiation.");
				throw new ProbeHandlerSetupException();
			}

			final Sensor sensor = (Sensor)sensorObject;

			sensor.setSensorInstance(sensorInstance);
			sensor.setSensorDescription(sensorDescription);
			sensor.updateCalibrationParameters();

			sensors.put(sensorInstance.getAddress(), sensor);
		}
	}

	@Override
	public void run() {
		logger.info("Handling probe: " + probe.getName());

		try {
			doRun();
		} catch (InterruptedException exception) {
			logger.info("ProbeHandler was interruped.");
		} finally {
			// TODO: for development only
			if (communicator != null) {
				communicator.end();
			}

			synchronized (this) {
				finished = true;
			}
		}
	}

	private void handleRawData(RawData rawData) {
		final int address = rawData.getAddress();
		final Sensor sensor = sensors.get(address);

		if (sensor == null) {
			if (!missingSensors.contains(address)) {
				logger.severe("The probe handler with probe '" + probe.getName() + "' received raw data for address '" + address + "' which no sensor can calibrate.");
				missingSensors.add(address);
			}

			return;
		}

		final double calibratedValue = sensor.calibrate(rawData.getValue());

		System.out.println("calibrated data with sensor '" + sensor.getSensorDescription().getName() + "': " + calibratedValue);
	}

	private void doRun() throws InterruptedException {
		// TODO: use condition variables
		while (true) {
			final boolean run;

			synchronized (this) {
				run = !shutdown;
			}

			try {
				if (run) {
					final RawData rawData = rawDataInputStream.readRawData();

					if (rawData != null) {
						handleRawData(rawData);
					}
				} else {
					break;
				}
			} catch (IOException exception) {
				logger.severe("Unable to read raw data from seral port.");
				final String stackTrace = ExceptionUtil.stackTraceToString(exception);
				logger.severe(stackTrace);
			}
		}
	}

	public boolean finished() {
		synchronized (this) {
			return finished;
		}
	}

	public void shutdown() {
		synchronized (this) {
			shutdown = true;
		}
	}
}