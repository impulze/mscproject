package de.hzg.collector;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.ExceptionUtil;
import de.hzg.common.HibernateUtil;
import de.hzg.measurement.Procedure;
import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.ProcedureInstance;
import de.hzg.measurement.Sensor;
import de.hzg.values.BinaryData;
import de.hzg.values.BinaryDataInputStream;
import de.hzg.values.CalculatedData;
import de.hzg.values.RawData;

public class SensorHandler implements Runnable {
	private static final Logger logger = Logger.getLogger(SensorHandler.class.getName());
	private final Sensor sensor;
	private final Communicator communicator;
	private final BinaryDataInputStream binaryDataInputStream;
	private boolean shutdown = false;
	private boolean finished = false;
	private final Map<Integer, Procedure> procedures = new HashMap<Integer, Procedure>();
	private Set<Integer> missingProcedures = new HashSet<Integer>();
	private final SessionFactory sessionFactory;
	private Session session;
	private final AtomicBoolean needsFlush = new AtomicBoolean(false);

	public SensorHandler(HibernateUtil hibernateUtil, Sensor sensor, ClassLoader classLoader) throws SensorHandlerSetupException {
		this.sensor = sensor;
		sessionFactory = hibernateUtil.getSessionFactory();

		try {
			this.communicator = new Communicator(sensor.getDevice());
		} catch (CommunicatorSetupException exception) {
			logger.severe("Cannot setup communicator for sensor handler with sensor '" + sensor.getName() + "'");
			throw new SensorHandlerSetupException();
		}

		try {
			final InputStream inputStream = communicator.getInputStream();
			binaryDataInputStream = new BinaryDataInputStream(inputStream);
		// TODO: this is for development only now
		} catch (Throwable exception) {
			communicator.end();
			throw exception;
		}

		final List<ProcedureInstance> procedureInstances = sensor.getProcedureInstances();

		for (final ProcedureInstance procedureInstance: procedureInstances) {
			final ProcedureDescription procedureDescription = procedureInstance.getProcedureDescription();

			// class name for the calculations
			final String className = "de.hzg.procedures." + procedureDescription.getClassName();
			final Class<?> procedureClass;

			try {
				procedureClass = classLoader.loadClass(className);
			} catch (ClassNotFoundException exception) {
				logger.severe("The sensor handler with sensor '" + sensor.getName() + "' uses formula class '" + className + "' which cannot be loaded.");
				throw new SensorHandlerSetupException();
			}

			final Object procedureObject;

			try {
				procedureObject = procedureClass.newInstance();
			} catch (InstantiationException|IllegalAccessException exception) {
				logger.severe("The formula class '" + procedureClass.getName() + "' used in sensor handler with sensor '" + sensor.getName() + "' is not suitable for instantiation.");
				throw new SensorHandlerSetupException();
			}

			final Procedure compiledProcedure = (Procedure)procedureObject;

			compiledProcedure.setProcedureInstance(procedureInstance);
			compiledProcedure.setProcedureDescription(procedureDescription);
			compiledProcedure.updateCalibrationParameters();

			procedures.put(procedureInstance.getAddress(), compiledProcedure);
		}
	}

	@Override
	public void run() {
		logger.info("Handling sensor: " + sensor.getName());
		session = sessionFactory.openSession();

		try {
			doRun();
		} catch (InterruptedException exception) {
			logger.info("SensorHandler was interruped.");
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
		final Procedure compiledProcedure = procedures.get(address);

		if (sensor == null) {
			if (!missingProcedures.contains(address)) {
				logger.severe("The sensor handler with sensor '" + sensor.getName() + "' received raw data for address '" + address + "' which no sensor can calibrate.");
				missingProcedures.add(address);
			}

			return;
		}

		final ProcedureInstance procedureInstance = compiledProcedure.getProcedureInstance();
		final int rawValue = binaryData.getValue();
		final double calculatedValue = compiledProcedure.calibrate(rawValue);

		//System.out.println("calibrated data with sensor '" + sensor.getProcedureDescription().getName() + "': " + calibratedValue);
		final RawData rawData = new RawData(procedureInstance, rawValue);
		final CalculatedData calculatedData = new CalculatedData(procedureInstance, calculatedValue);

		session.save(rawData);
		session.save(calculatedData);
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

					if (needsFlush.compareAndSet(true, false)) {
						session.flush();
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

	public void setNeedsFlush() {
		needsFlush.set(true);
	}
}