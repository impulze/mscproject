package de.hzg.collector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.ExceptionUtil;
import de.hzg.common.HibernateUtil;
import de.hzg.common.ObservedPropertyClassesConfiguration;
import de.hzg.measurement.ObservedProperty;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;
import de.hzg.measurement.Sensor;
import de.hzg.values.BinaryData;
import de.hzg.values.BinaryDataInputStream;
import de.hzg.values.CalculatedData;
import de.hzg.values.RawData;

public class SensorHandler implements Runnable {
	private class Interval {
		public Calendar timestamp;
		public DescriptiveStatistics stats = new DescriptiveStatistics();
	};

	// TODO: make this configurable via XML (for now 10 minutes)
	private static int INTERVAL_MS = 10 * 60 * 1000;
	private static final Logger logger = Logger.getLogger(SensorHandler.class.getName());
	private final Sensor sensor;
	private final Communicator communicator;
	private final BinaryDataInputStream binaryDataInputStream;
	private boolean shutdown = false;
	private boolean finished = false;
	// address -> observedPropertyClass
	private final Map<Integer, List<ObservedProperty>> observedProperties = new HashMap<Integer, List<ObservedProperty>>();
	// observedPropertyClass -> List<Double/Integer>
	private final Map<ObservedProperty, Interval> currentIntervals = new HashMap<ObservedProperty, Interval>();
	// observedPropertyClass -> begin timestamp of interval
	private Set<Integer> missingObservedProperties = new HashSet<Integer>();
	private final SessionFactory sessionFactory;
	private Session session;
	private final AtomicBoolean needsFlush = new AtomicBoolean(false);

	public SensorHandler(HibernateUtil hibernateUtil, Sensor sensor, ClassLoader classLoader) throws SensorHandlerSetupException {
		this.sensor = sensor;
		sessionFactory = hibernateUtil.getSessionFactory();

		final Calendar now = Calendar.getInstance();

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

		final List<ObservedPropertyInstance> observedPropertyInstances = sensor.getObservedPropertyInstances();

		for (final ObservedPropertyInstance observedPropertyInstance: observedPropertyInstances) {
			final ObservedPropertyDescription observedPropertyDescription = observedPropertyInstance.getObservedPropertyDescription();

			// class name for the calculations
			final String className = ObservedPropertyClassesConfiguration.CLASSES_PACKAGE + "." + observedPropertyDescription.getClassName();
			final Class<?> observedPropertyClass;

			try {
				observedPropertyClass = classLoader.loadClass(className);
			} catch (ClassNotFoundException exception) {
				logger.severe("The sensor handler with sensor '" + sensor.getName() + "' uses formula class '" + className + "' which cannot be loaded.");
				throw new SensorHandlerSetupException();
			}

			final Object observedPropertyObject;

			try {
				observedPropertyObject = observedPropertyClass.newInstance();
			} catch (InstantiationException|IllegalAccessException exception) {
				logger.severe("The formula class '" + observedPropertyClass.getName() + "' used in sensor handler with sensor '" + sensor.getName() + "' is not suitable for instantiation.");
				throw new SensorHandlerSetupException();
			}

			final ObservedProperty compiledObservedProperty = (ObservedProperty)observedPropertyObject;

			compiledObservedProperty.setObservedPropertyInstance(observedPropertyInstance);
			compiledObservedProperty.updateCalibrationParameters();

			List<ObservedProperty> observedPropertiesForAddress = observedProperties.get(observedPropertyInstance.getAddress());

			if (observedPropertiesForAddress == null) {
				observedPropertiesForAddress = new ArrayList<ObservedProperty>();
				observedProperties.put(observedPropertyInstance.getAddress(), observedPropertiesForAddress);
			}

			observedPropertiesForAddress.add(compiledObservedProperty);

			if (observedPropertyInstance.getUseInterval()) {
				final Interval interval = new Interval();

				interval.timestamp = now;

				currentIntervals.put(compiledObservedProperty, interval);
			}
		}
	}

	@Override
	public void run() {
		logger.info("Handling sensor: " + sensor.getName());
		session = sessionFactory.openSession();
		session.setFlushMode(FlushMode.MANUAL);

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

	private static void addStats(RawData rawData, DescriptiveStatistics stats) {
		rawData.setAverage(stats.getMean());
		rawData.setMin((int)stats.getMin());
		rawData.setMax((int)stats.getMax());
		rawData.setMedian((int)stats.getPercentile(50));
		rawData.setStddev(stats.getStandardDeviation());
	}

	private static void addStats(CalculatedData calculatedData, DescriptiveStatistics stats) {
		calculatedData.setAverage(stats.getMean());
		calculatedData.setMin(stats.getMin());
		calculatedData.setMax(stats.getMax());
		calculatedData.setMedian(stats.getPercentile(50));
		calculatedData.setStddev(stats.getStandardDeviation());
	}

	private void handleBinaryData(BinaryData binaryData) {
		final int address = binaryData.getAddress();
		final List<ObservedProperty> compiledObservedProperties = observedProperties.get(address);

		if (compiledObservedProperties == null) {
			if (!missingObservedProperties.contains(address)) {
				logger.severe("The sensor handler with sensor '" + sensor.getName() + "' received raw data for address '" + address + "' which no sensor class is configured for.");
				missingObservedProperties.add(address);
			}

			return;
		}

		final int binaryValue = binaryData.getValue();
		final Calendar now = Calendar.getInstance();

		for (final ObservedProperty compiledObservedProperty: compiledObservedProperties) {
			final ObservedPropertyInstance observedPropertyInstance = compiledObservedProperty.getObservedPropertyInstance();

			if (observedPropertyInstance.getUseInterval()) {
				final Interval currentIntervalForObservedProperty = currentIntervals.get(compiledObservedProperty);

				final Calendar start = currentIntervalForObservedProperty.timestamp;
				final Double newNumber;

				if (observedPropertyInstance.getIsRaw()) {
					final int newRawValue = compiledObservedProperty.getRawValue(binaryValue);
					newNumber = Double.valueOf(newRawValue);
				} else {
					newNumber = compiledObservedProperty.getCalculationValue(binaryValue);
				}

				if (now.getTimeInMillis() - start.getTimeInMillis() > INTERVAL_MS) {
					final Calendar end = (Calendar)start.clone();

					end.add(Calendar.MILLISECOND, INTERVAL_MS);

					if (observedPropertyInstance.getIsRaw()) {
						final RawData rawData = new RawData(end.getTime(), observedPropertyInstance);

						addStats(rawData, currentIntervalForObservedProperty.stats);
						session.save(rawData);
					} else {
						final CalculatedData calculatedData = new CalculatedData(end.getTime(), observedPropertyInstance);

						addStats(calculatedData, currentIntervalForObservedProperty.stats);
						session.save(calculatedData);
					}

					// set new interval begin = old + interval, reset list and add new value (after interval ended)
					currentIntervalForObservedProperty.timestamp = end;
					currentIntervalForObservedProperty.stats.clear();
				}

				currentIntervalForObservedProperty.stats.addValue(newNumber);
			} else {
				if (observedPropertyInstance.getIsRaw()) {
					final int rawValue = compiledObservedProperty.getRawValue(binaryValue);
					final RawData rawData = new RawData(now.getTime(), observedPropertyInstance);

					rawData.setValue(rawValue);
					session.save(rawData);
				} else {
					final double calculatedValue = compiledObservedProperty.getCalculationValue(binaryValue);
					final CalculatedData calculatedData = new CalculatedData(now.getTime(), observedPropertyInstance);

					calculatedData.setValue(calculatedValue);
					session.save(calculatedData);
				}
			}
		}
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