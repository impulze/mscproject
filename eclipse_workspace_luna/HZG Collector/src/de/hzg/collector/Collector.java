package de.hzg.collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.HibernateUtil;
import de.hzg.measurement.Sensor;

public class Collector implements Runnable {
	private static final Logger logger = Logger.getLogger(Collector.class.getName());
	private List<Thread> sensorHandlerThreads = new ArrayList<Thread>();
	private List<SensorHandler> sensorHandlers = new ArrayList<SensorHandler>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> flushFuture = null;

	public Collector(HibernateUtil hibernateUtil, ClassLoader classLoader) {
		final SessionFactory sessionFactory = hibernateUtil.getSessionFactory();
		final Session session = sessionFactory.openSession();
		final String queryString = "FROM Sensor WHERE active = true";
		final Query query = session.createQuery(queryString);
		@SuppressWarnings("unchecked")
		final List<Sensor> activeSensors = query.list();

		for (final Sensor activeSensor: activeSensors) {
			final SensorHandler sensorHandler;

			logger.info("Starting handler for active sensor: " + activeSensor.getName());

			try {
				sensorHandler = new SensorHandler(hibernateUtil, activeSensor, classLoader);
			} catch (SensorHandlerSetupException exception) {
				logger.severe("Error creating SensorHandler instance for '" + activeSensor.getName() + "'");
				continue;
			}

			sensorHandlers.add(sensorHandler);
			sensorHandlerThreads.add(new Thread(sensorHandler));
		}

		session.close();
	}

	@Override
	public void run() {
		for (final Thread sensorHandlerThread: sensorHandlerThreads) {
			sensorHandlerThread.start();
		}

		flushFuture = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for (final SensorHandler sensorHandler: sensorHandlers) {
					sensorHandler.setNeedsFlush();
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

	public boolean finished() {
		synchronized (this) {
			for (final Iterator<SensorHandler> iterator = sensorHandlers.iterator();
			     iterator.hasNext();) {
				final SensorHandler sensorHandler = iterator.next();

				if (sensorHandler.finished()) {
					final int sensorHandlerThreadIndex = sensorHandlerThreads.indexOf(sensorHandler);
					final Thread sensorHandlerThread = sensorHandlerThreads.get(sensorHandlerThreadIndex);
					iterator.remove();
					sensorHandlerThreads.remove(sensorHandlerThread);
				}
			}

			return sensorHandlers.size() == 0 && flushFuture.isDone();
		}
	}

	public void shutdown() {
		synchronized (this) {
			for (final Iterator<SensorHandler> iterator = sensorHandlers.iterator();
			     iterator.hasNext();) {
				final SensorHandler sensorHandler = iterator.next();
				final int sensorHandlerThreadIndex = sensorHandlerThreads.indexOf(sensorHandler);
				final Thread sensorHandlerThread = sensorHandlerThreads.get(sensorHandlerThreadIndex);

				sensorHandler.shutdown();
				sensorHandlerThread.interrupt();
			}

			flushFuture.cancel(true);
		}
	}
}