package de.hzg.collector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.HibernateUtil;
import de.hzg.measurement.Probe;

public class Collector implements Runnable {
	private static final Logger logger = Logger.getLogger(Collector.class.getName());
	private List<Thread> probeHandlerThreads = new ArrayList<Thread>();
	private List<ProbeHandler> probeHandlers = new ArrayList<ProbeHandler>();

	public Collector(HibernateUtil hibernateUtil, ClassLoader classLoader) {
		final SessionFactory sessionFactory = hibernateUtil.getSessionFactory();
		final Session session = sessionFactory.openSession();
		final String queryString = "FROM Probe WHERE active = true";
		final Query query = session.createQuery(queryString);
		@SuppressWarnings("unchecked")
		final List<Probe> activeProbes = query.list();

		for (final Probe activeProbe: activeProbes) {
			final ProbeHandler probeHandler;

			logger.info("Starting handler for active probe: " + activeProbe.getName());

			try {
				probeHandler = new ProbeHandler(hibernateUtil, activeProbe, classLoader);
			} catch (ProbeHandlerSetupException exception) {
				logger.severe("Error creating ProbeHandler instance for '" + activeProbe.getName() + "'");
				continue;
			}

			probeHandlers.add(probeHandler);
			probeHandlerThreads.add(new Thread(probeHandler));
		}

		session.close();
	}

	@Override
	public void run() {
		for (final Thread probeHandlerThread: probeHandlerThreads) {
			probeHandlerThread.start();
		}
	}

	public boolean finished() {
		synchronized (this) {
			for (final Iterator<ProbeHandler> iterator = probeHandlers.iterator();
			     iterator.hasNext();) {
				final ProbeHandler probeHandler = iterator.next();

				if (probeHandler.finished()) {
					final int probeHandlerThreadIndex = probeHandlerThreads.indexOf(probeHandler);
					final Thread probeHandlerThread = probeHandlerThreads.get(probeHandlerThreadIndex);
					iterator.remove();
					probeHandlerThreads.remove(probeHandlerThread);
				}
			}

			return probeHandlers.size() == 0;
		}
	}

	public void shutdown() {
		synchronized (this) {
			for (final Iterator<ProbeHandler> iterator = probeHandlers.iterator();
			     iterator.hasNext();) {
				final ProbeHandler probeHandler = iterator.next();
				final int probeHandlerThreadIndex = probeHandlerThreads.indexOf(probeHandler);
				final Thread probeHandlerThread = probeHandlerThreads.get(probeHandlerThreadIndex);

				probeHandler.shutdown();
				probeHandlerThread.interrupt();
			}
		}
	}
}