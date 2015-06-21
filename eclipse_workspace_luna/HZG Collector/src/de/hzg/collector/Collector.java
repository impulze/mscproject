package de.hzg.collector;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.hzg.commons.ExceptionUtil;
import de.hzg.sensors.Probe;
import de.hzg.sensors.ProbeDatabase;

public class Collector implements Runnable {
	private static final Logger logger = Logger.getLogger(Collector.class.getName());
	private final Communicator communicator;
	private final ProbeDatabase probeDatabase;
	private final RawDataInputStream rawDataInputStream ;
	private boolean finished = false;
	private boolean shutdown = false;

	public Collector() throws DataSourceSetupException, CommunicatorSetupException {
		communicator = new Communicator();

		try {
			final InputStream inputStream = communicator.getInputStream();
			rawDataInputStream = new RawDataInputStream(inputStream);

			// final DataSource dataSource = setupDataSource();
			probeDatabase = new ProbeDatabase();
			final List<Probe> activeProbes = probeDatabase.getActiveProbes();
			activeProbes.size();
		} catch (Throwable exception) {
			communicator.end();
			throw exception;
		}
	}

	private static DataSource setupDataSource() throws DataSourceSetupException {
		logger.info("Obtaining datasource/connection pool from JDBC driver.");

		// Setup the source programatically:
		/*
		final PoolProperties poolProperties = new PoolProperties();
		poolProperties.setUrl("jdbc:postgresql://localhost:5432/" + "probe_db");
		poolProperties.setDriverClassName("org.postgresql.Driver");
		poolProperties.setUsername("impulze");
		poolProperties.setPassword("letmein");
		return new DataSource(poolProperties);
		*/

		// Setup via Tomcat XML configuration:
		try {
			Context initContext = new InitialContext();
			Context context  = (Context)initContext.lookup("java:/comp/env");
			return (DataSource)context.lookup("jdbc/LocalProbeDB");
		} catch (NamingException exception) {
			logger.severe("Unable to create naming context while setting up datasource.");
			throw new DataSourceSetupException();
		}
	}

	@Override
	public void run() {
		try {
			doRun();
		} catch (InterruptedException exception) {
			logger.info("Collector was interruped.");
		} finally {
			if (communicator != null) {
				communicator.end();
			}

			synchronized (this) {
				finished = true;
			}
		}
	}

	private void doRun() throws InterruptedException {
		while (true) {
			final boolean run;

			synchronized (this) {
				run = !shutdown;
			}

			try {
				if (run) {
					final RawData rawData = rawDataInputStream.readRawData();

					if (rawData != null) {
						//System.out.println("(addr: " + rawData.getAddress() + ") " + rawData.getValue());
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