package de.hzg.collector;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.ExceptionUtil;
import de.hzg.common.HibernateUtil;
import de.hzg.measurement.Probe;
import de.hzg.measurement.Sensor;
import de.hzg.measurement.SensorDescription;
import de.hzg.measurement.SensorInstance;
import de.hzg.values.BinaryData;
import de.hzg.values.BinaryDataInputStream;
import de.hzg.values.CalculatedData;
import de.hzg.values.RawData;

public class ProbeHandler implements Runnable {
	private static final Logger logger = Logger.getLogger(ProbeHandler.class.getName());
	private final Probe probe;
	private final Communicator communicator;
	private final BinaryDataInputStream binaryDataInputStream;
	private boolean shutdown = false;
	private boolean finished = false;
	private final Map<Integer, Sensor> sensors = new HashMap<Integer, Sensor>();
	private Set<Integer> missingSensors = new HashSet<Integer>();
	private final SessionFactory sessionFactory;
	private Session session;

	public ProbeHandler(HibernateUtil hibernateUtil, Probe probe, ClassLoader classLoader) throws ProbeHandlerSetupException {
		this.probe = probe;
		sessionFactory = hibernateUtil.getSessionFactory();

		try {
			this.communicator = new Communicator(probe.getDevice());
		} catch (CommunicatorSetupException exception) {
			logger.severe("Cannot setup communicator for probe handler with probe '" + probe.getName() + "'");
			throw new ProbeHandlerSetupException();
		}

		try {
			final InputStream inputStream = communicator.getInputStream();
			binaryDataInputStream = new BinaryDataInputStream(inputStream);
		// TODO: this is for development only now
		} catch (Throwable exception) {
			communicator.end();
			throw exception;
		}

		final List<SensorInstance> sensorInstances = probe.getSensorInstances();

		for (final SensorInstance sensorInstance: sensorInstances) {
			final SensorDescription sensorDescription = sensorInstance.getSensorDescription();

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
		session = sessionFactory.openSession();

		try {
			doRun();
		} catch (InterruptedException exception) {
			logger.info("ProbeHandler was interruped.");
		} finally {
			session.close();

			// TODO: for development only
			if (communicator != null) {
				communicator.end();
			}

			synchronized (this) {
				finished = true;
			}
		}
	}

	private void handleBinaryData(BinaryData binaryData) {
		final int address = binaryData.getAddress();
		final Sensor sensor = sensors.get(address);

		if (sensor == null) {
			if (!missingSensors.contains(address)) {
				logger.severe("The probe handler with probe '" + probe.getName() + "' received raw data for address '" + address + "' which no sensor can calibrate.");
				missingSensors.add(address);
			}

			return;
		}

		final SensorInstance sensorInstance = sensor.getSensorInstance();
		final int rawValue = binaryData.getValue();
		final double calculatedValue = sensor.calibrate(rawValue);

		//System.out.println("calibrated data with sensor '" + sensor.getSensorDescription().getName() + "': " + calibratedValue);
		final RawData rawData = new RawData(sensorInstance, rawValue);
		final CalculatedData calculatedData = new CalculatedData(sensorInstance, calculatedValue);

		session.save(rawData);
		session.save(calculatedData);
		session.flush();
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
					final BinaryData binaryData = binaryDataInputStream.readBinaryData();

					if (binaryData != null) {
						handleBinaryData(binaryData);
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