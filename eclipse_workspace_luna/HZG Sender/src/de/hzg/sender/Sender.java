package de.hzg.sender;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DoubleType;
import org.hibernate.type.LongType;
import org.hibernate.type.TimestampType;

import de.hzg.common.HTTPSenderConfiguration;
import de.hzg.common.HibernateUtil;
import de.hzg.measurement.SensorInstance;

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
	private final List<SensorInstance> sensorInstances;
	private final Map<SensorInstance, Timestamp> lastSetStarts = new HashMap<SensorInstance, Timestamp>();
	private final String sqlQueryString;

	Sender(HibernateUtil hibernateUtil, HTTPSenderConfiguration httpSenderConfiguration) {
		final URL url = httpSenderConfiguration.getURL();

		this.sessionFactory = hibernateUtil.getSessionFactory();
		this.basicAuth = httpSenderConfiguration.getBasicAuth();
		this.interval = httpSenderConfiguration.getInterval();
		this.urlString = url.toString();
		this.queryString = httpSenderConfiguration.getQuery();
		this.sensorInstances = getSensorInstances(sessionFactory);
		this.sqlQueryString = getQueryString();

		for (final SensorInstance sensorInstance: sensorInstances) {
			lastSetStarts.put(sensorInstance, new Timestamp(0)); //new Timestamp((new Date()).getTime()));
		}
	}

	@SuppressWarnings("unchecked")
	static private List<SensorInstance> getSensorInstances(SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<SensorInstance> sensorInstances;

		try {
			sensorInstances= session.createQuery("FROM SensorInstance").list();

			for (final SensorInstance sensorInstance: sensorInstances) {
				Hibernate.initialize(sensorInstance.getSensorDescription());
			}
		} finally {
			session.close();
		}

		return sensorInstances;	
	}

	void start() {
		scheduledFuture = scheduler.scheduleAtFixedRate(this, 0, 10, TimeUnit.SECONDS);
	}

	static String getQueryString() {
		final String values = "MAX(cd.timestamp) AS time, COUNT(cd.value), AVG(cd.value), MAX(cd.value), MIN(cd.value), STDDEV(cd.value)";
		final String sqlQueryString;

		if (BULK_FETCH_INTERVAL.longValue() > 0) {
			if (LIMIT > 0) {
				sqlQueryString = "SELECT " + values + " FROM ( " +
						"SELECT * FROM ( " +
							"SELECT *, last_value(timestamp) over Timestamps - first_value(timestamp) over Timestamps AS slice FROM calculations WHERE sensor_instance_id = :sid AND timestamp > :timestamp WINDOW Timestamps as (ORDER BY timestamp)" +
						" ) AS allWithinSlice WHERE slice <= CAST(:interval AS INTERVAL) LIMIT :limit" +
					" ) AS cd GROUP BY cd.sensor_instance_id";
			} else {
				sqlQueryString =
						"SELECT " + values + " FROM ( " +
							"SELECT *, last_value(timestamp) over Timestamps - first_value(timestamp) over Timestamps AS slice FROM calculations WHERE sensor_instance_id = :sid AND timestamp > :timestamp WINDOW Timestamps as (ORDER BY timestamp)" +
						" ) AS cd WHERE slice <= CAST(:interval AS INTERVAL) GROUP BY cd.sensor_instance_id";
			}
		} else {
			if (LIMIT > 0) {
				sqlQueryString = "SELECT " + values + " FROM ( " +
					"SELECT * FROM calculations WHERE sensor_instance_id = :sid AND timestamp > :timestamp ORDER BY timestamp LIMIT :limit" +
				" ) AS cd GROUP BY cd.sensor_instance_id";
			} else {
				sqlQueryString = "SELECT " + values + " FROM calculations AS cd WHERE sensor_instance_id = :sid AND timestamp > :timestamp GROUP BY cd.sensor_instance_id";
			}
		}

		return sqlQueryString;
	}

	@Override
	public void run() {
		long runStart = System.currentTimeMillis();
		System.out.println("new run");
		final List<SensorInstance> toBeChecked = new ArrayList<SensorInstance>(sensorInstances);
		final Session session = sessionFactory.openSession();
		final Map<SensorInstance, Integer> howManies = new HashMap<SensorInstance, Integer>();

		for (final SensorInstance sensorInstance: sensorInstances) {
			howManies.put(sensorInstance, 0);
		}

		while (!toBeChecked.isEmpty()) {
			final Iterator<SensorInstance> iterator = toBeChecked.iterator();

			while (iterator.hasNext()) {
				final SensorInstance sensorInstance = iterator.next();
				final SQLQuery query = session.createSQLQuery(sqlQueryString);

				query.addScalar("time", TimestampType.INSTANCE)
					.addScalar("count", LongType.INSTANCE)
					.addScalar("avg", DoubleType.INSTANCE)
					.addScalar("max", DoubleType.INSTANCE)
					.addScalar("min", DoubleType.INSTANCE)
					.addScalar("stddev", DoubleType.INSTANCE)
					.setParameter("timestamp", lastSetStarts.get(sensorInstance))
					.setParameter("sid", sensorInstance.getId());

				if (LIMIT > 0) {
					query.setParameter("limit", LIMIT);
				}

				if (BULK_FETCH_INTERVAL.longValue() > 0) {
					query.setParameter("interval", BULK_FETCH_INTERVAL.longValue() + " seconds'");
				}

				final String sensorName = sensorInstance.getSensorDescription().getName();
				//final Timestamp oldestLookupTimestamp = lastSetStarts.get(sensorInstance);
				final List<Object[]> result = query.list();

				if (result.size() == 0) {
					System.out.println("removing " + sensorInstance.getId());
					iterator.remove();
					continue;
				}

				System.out.println("have results newest lookup now " + (Timestamp)result.get(0)[0]);

				final Object[] record = result.get(0);
				final Timestamp newestEntry = (Timestamp)record[0];
				final Long count = (Long)record[1];

				lastSetStarts.put(sensorInstance, newestEntry);
				howManies.put(sensorInstance, howManies.get(sensorInstance) + count.intValue());
				System.out.println("count all (" + sensorInstance.getId() + "): " + howManies.get(sensorInstance));

				/*
				if (!handleResult(sensorName, newestEntry, count, sensorInstance, record)) {
					continue;
				}
				*/
			}
		}

		session.close();
		long runEnd = System.currentTimeMillis();

		double seconds = (runEnd - runStart) / 1000.0;
		long millis = (runEnd - runStart) - (long)(seconds * 1000);

		System.out.println("run took: " + seconds + " seconds and " + millis + " millis")
	}

	private boolean handleResult(String sensorName, Timestamp newestEntry, Long count, SensorInstance sensorInstance, Object[] record) {
		// need to extract station, parameter, time, value, max, min, stddev, count
		// TODO: for now hardcoded, get this from probe?
		final String station = "testenv";
		// TODO: for now hardcoded, already obtainable via sensorDescription.getName()
		final String parameter = "test";

		final Double avg = (Double)record[2];
		final Double max = (Double)record[3];
		final Double min = (Double)record[4];
		final Double stddev = (Double)record[5];

		if ("test".equals("test")) {
			//System.out.println(String.format("(sid %d) %s", sensorInstance.getId(), newestEntry));
			return true;
		}

		//Hibernate.initialize(sensorInstance.getProbe());
		//Hibernate.initialize(sensorInstance.getSensorDescription());

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
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return false;
		}

		try {
			final URL sendURL = new URL(urlString + queryString);
			final URLConnection connection = sendURL.openConnection();

			if (basicAuth != null) {
				final String authString = "Basic " + new String(DatatypeConverter.printBase64Binary(basicAuth.getBytes()));
				connection.setRequestProperty("Authorization", authString);
			}

			connection.setRequestProperty("Accept-Charset", "UTF-8");

			final byte[] buffer = new byte[4096];
			final InputStream response = connection.getInputStream();
			int r;

			while ((r = response.read(buffer)) > 0) {
				System.out.write(buffer, 0, r);
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