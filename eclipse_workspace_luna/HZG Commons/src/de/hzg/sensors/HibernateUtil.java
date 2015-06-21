package de.hzg.sensors;

import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());
	private static HibernateUtil hibernateUtil;
	private final SessionFactory sessionFactory;
	private final ServiceRegistry serviceRegistry;

	private HibernateUtil() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			final Configuration configuration = new Configuration();
			configuration.configure("/de/hzg/sensors/hibernate.cfg.xml");
			final StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(configuration.getProperties());
			serviceRegistry = serviceRegistryBuilder.build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable exception) {
			// Make sure you log the exception, as it might be swallowed
			logger.severe("Creating Hibernate SessionFactory failed.");
			throw exception;
		}
	}

	public static HibernateUtil getInstance() {
		if (hibernateUtil == null) {
			hibernateUtil = new HibernateUtil();
		}

		return hibernateUtil;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}