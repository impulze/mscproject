package de.hzg.common;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());
	private static final Map<String, SessionFactory> sessionFactories = new HashMap<String, SessionFactory>();

	public static SessionFactory getSessionFactory(String configurationFile) {
		SessionFactory sessionFactory = sessionFactories.get(configurationFile);

		if (sessionFactory != null) {
			return sessionFactory;
		}

		try {
			final Configuration configuration = new Configuration();
			configuration.configure(configurationFile);
			final StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(configuration.getProperties());
			final ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable exception) {
			logger.severe("Creating Hibernate SessionFactory failed.");
			throw exception;
		}

		sessionFactories.put(configurationFile, sessionFactory);
		return sessionFactory;
	}
}