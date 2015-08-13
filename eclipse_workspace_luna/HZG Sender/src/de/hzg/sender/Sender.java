package de.hzg.sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;
import org.hibernate.type.TimestampType;

import de.hzg.common.ExceptionUtil;
import de.hzg.common.HTTPSenderConfiguration;
import de.hzg.common.HibernateUtil;
import de.hzg.measurement.Sensor;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;

public class Sender implements Runnable {
	// how many values can the HZG database accept
	private static final Integer LIMIT = 9999;
	// 0 = 9999 values iteratively until now, x = x seconds interval until now
	private static final Long BULK_FETCH_INTERVAL = new Long(600);
	private static final Logger logger = Logger.getLogger(Sender.class.getName());
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final SessionFactory sessionFactory;
	private final String basicAuth;
	private final String urlString;
	private final String queryString;
	private ScheduledFuture<?> scheduledFuture;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final List<ObservedPropertyInstance> observedPropertyInstances;
	private final Map<ObservedPropertyInstance, Timestamp> lastSetStarts = new HashMap<ObservedPropertyInstance, Timestamp>();
	private final String sqlQueryString;

	Sender(HibernateUtil hibernateUtil, HTTPSenderConfiguration httpSenderConfiguration) {
		final URL url = httpSenderConfiguration.getURL();

		this.sessionFactory = hibernateUtil.getSessionFactory();
		this.basicAuth = httpSenderConfiguration.getBasicAuth();
		this.urlString = url.toString();
		this.queryString = httpSenderConfiguration.getQuery();
		this.observedPropertyInstances = getObservedPropertyInstances(sessionFactory);
		this.sqlQueryString = getQueryString();

		for (final ObservedPropertyInstance observedPropertyInstance: observedPropertyInstances) {
			lastSetStarts.put(observedPropertyInstance, new Timestamp((new Date()).getTime()));
		}
	}

	@SuppressWarnings("unchecked")
	static private List<ObservedPropertyInstance> getObservedPropertyInstances(SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<ObservedPropertyInstance> observedPropertyInstances;

		try {
			observedPropertyInstances= session.createQuery("FROM ObservedPropertyInstance").list();

			for (final ObservedPropertyInstance observedPropertyInstance: observedPropertyInstances) {
				Hibernate.initialize(observedPropertyInstance.getSensor());
				Hibernate.initialize(observedPropertyInstance.getObservedPropertyDescription());
			}
		} finally {
			session.close();
		}

		return observedPropertyInstances;
	}

	public void start() {
		scheduledFuture = scheduler.scheduleAtFixedRate(this, 0, 600, TimeUnit.SECONDS);
	}

	private static String getQueryString() {
		final String values = "MAX(cd.date) AS time, COUNT(cd.value), AVG(cd.value), MAX(cd.value), MIN(cd.value), STDDEV(cd.value)";
		final String sqlQueryString;

		if (BULK_FETCH_INTERVAL.longValue() > 0) {
			if (LIMIT > 0) {
				sqlQueryString = String.format(
					"SELECT " + values + " FROM ( " +
						"SELECT *, last_value(date) over Timestamps - first_value(date) over Timestamps AS slice FROM calculations WHERE observed_property_instance_id = :sid AND date > :timestamp AND date <= :run_begin WINDOW Timestamps as (ORDER BY date) LIMIT %d" +
					" ) AS cd WHERE slice <= INTERVAL '%d seconds' GROUP BY cd.observed_property_instance_id", LIMIT.longValue(), BULK_FETCH_INTERVAL.longValue());
			} else {
				sqlQueryString = String.format(
					"SELECT " + values + " FROM ( " +
						"SELECT *, last_value(date) over Timestamps - first_value(date) over Timestamps AS slice FROM calculations WHERE observed_property_instance_id = :sid AND date > :timestamp AND date <= :run_begin WINDOW Timestamps as (ORDER BY date)" +
					" ) AS cd WHERE slice <= INTERVAL '%d seconds' GROUP BY cd.observed_property_instance_id", BULK_FETCH_INTERVAL.longValue());
			}
		} else {
			if (LIMIT > 0) {
				sqlQueryString = String.format("SELECT " + values + " FROM ( " +
					"SELECT * FROM calculations WHERE observed_property_instance_id = :sid AND date > :timestamp AND date <= :run_begin ORDER BY date LIMIT %d" +
				" ) AS cd GROUP BY cd.observed_property_instance_id", LIMIT.longValue());
			} else {
				sqlQueryString = "SELECT " + values + " FROM calculations AS cd WHERE observed_property_instance_id = :sid AND date > :timestamp AND date <= :run_begin GROUP BY cd.observed_property_instance_id";
			}
		}

		return sqlQueryString;
	}

	private void runOne(Session session) {
		final List<ObservedPropertyInstance> toBeChecked = new ArrayList<ObservedPropertyInstance>(observedPropertyInstances);
		final Timestamp runBegin = new Timestamp(Calendar.getInstance().getTimeInMillis());

		while (!toBeChecked.isEmpty()) {
			final Iterator<ObservedPropertyInstance> iterator = toBeChecked.iterator();

			while (iterator.hasNext()) {
				final ObservedPropertyInstance observedPropertyInstance = iterator.next();
				final SQLQuery query = session.createSQLQuery(sqlQueryString);

				query.addScalar("time", TimestampType.INSTANCE)
					.addScalar("count", LongType.INSTANCE)
					.addScalar("avg", DoubleType.INSTANCE)
					.addScalar("max", DoubleType.INSTANCE)
					.addScalar("min", DoubleType.INSTANCE)
					.addScalar("stddev", DoubleType.INSTANCE)
					.setParameter("timestamp", lastSetStarts.get(observedPropertyInstance))
					.setParameter("sid", observedPropertyInstance.getId())
					.setParameter("run_begin", runBegin);

				final String observedPropertyDescriptionName = observedPropertyInstance.getObservedPropertyDescription().getName();
				@SuppressWarnings("unchecked")
				final List<Object[]> result = query.list();

				if (result.size() == 0) {
					iterator.remove();
					continue;
				}

				final Object[] record = result.get(0);
				final Timestamp newestEntry = (Timestamp)record[0];
				final Long count = (Long)record[1];

				if (handleResult(observedPropertyDescriptionName, newestEntry, count, observedPropertyInstance, record)) {
					lastSetStarts.put(observedPropertyInstance, newestEntry);
				}

				if (count != 0) {
					// there was data between 'now and the start of this run
					if (LIMIT <= 0 || count != LIMIT.longValue()) {
						// either there was no limit and we had data or we had a limit but we don't need another iterator
						iterator.remove();
					}
				} else {
					// there were no items between 'now' and the start of this run, so remove the iterator
					iterator.remove();
				}
			}
		}
	}

	@Override
	public void run() {
		//long runStart = System.currentTimeMillis();

		final Session session = sessionFactory.openSession();

		try {
			runOne(session);
		} catch (Exception exception) {
			final String stackTrace = ExceptionUtil.stackTraceToString(exception);
			logger.severe(stackTrace);
		} finally {
			session.close();

			/*
			long runEnd = System.currentTimeMillis();
			double seconds = (runEnd - runStart) / 1000.0;
			System.out.println("run took: " + seconds + " seconds");
			 */
		}
	}

	private boolean handleResult(String observedPropertyDescriptionName, Timestamp newestEntry, Long count, ObservedPropertyInstance observedPropertyInstance, Object[] record) {
		// TODO: we only have one station right now
		if (observedPropertyInstance.getSensor().getId() != 2 || observedPropertyInstance.getId() != 2) {
			return true;
		}

		// need to extract station, parameter, time, value, max, min, stddev, count
		// TODO: for now hardcoded, get this from sensor?
		final String station = "testenv";
		// TODO: for now hardcoded, already obtainable via observedPropertyDescription.getName()
		final String parameter = "test";

		final Double avg = (Double)record[2];
		final Double max = (Double)record[3];
		final Double min = (Double)record[4];
		final Double stddev = record[5] == null ? 0 : (Double)record[5];

		String queryString = "?" + this.queryString;

		try {
			queryString = queryString.replace("${station}", station);
			queryString = queryString.replace("${parameter}", parameter);
			queryString = queryString.replace("${time}", URLEncoder.encode(dateFormat.format(newestEntry), "UTF-8"));
			queryString = queryString.replace("${value}", avg.toString());
			queryString = queryString.replace("${max}", max.toString());
			queryString = queryString.replace("${min}", min.toString());
			queryString = queryString.replace("${stddev}", stddev.toString());
			queryString = queryString.replace("${count}", count.toString());
		} catch (UnsupportedEncodingException exception) {
			exception.printStackTrace();
			return false;
		}

		try {
			final URL sendURL = new URL(urlString + queryString);
			final HttpURLConnection connection = (HttpURLConnection)sendURL.openConnection();

			// TODO: debugging
			System.out.println(sendURL);

			if (basicAuth != null) {
				final String authString = "Basic " + new String(DatatypeConverter.printBase64Binary(basicAuth.getBytes()));
				connection.setRequestProperty("Authorization", authString);
			}

			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestMethod("GET");
			connection.connect();

			final int responseCode = connection.getResponseCode();
			final byte[] buffer = new byte[4096];
			final InputStream response = connection.getInputStream();

			if (responseCode != 200) {
				int result;
				final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				while ((result = response.read(buffer)) > 0) {
					outputStream.write(buffer, 0, result);
				}

				final String outputString = outputStream.toString("UTF-8");
				final Sensor sensor = observedPropertyInstance.getSensor();
				final ObservedPropertyDescription observedPropertyDescription = observedPropertyInstance.getObservedPropertyDescription();
				final String msg = String.format(
						"The transmission to HZG failed for sensor '%s' observed property description '%s': '%s'",
						sensor.getName(),
						observedPropertyDescription.getName(),
						outputString);

				logger.warning(msg);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void cancel() {
		if (scheduledFuture != null) {
			scheduledFuture.cancel(true);
		}
	}
}