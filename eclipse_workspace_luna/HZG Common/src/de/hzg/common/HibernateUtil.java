package de.hzg.common;

import java.util.logging.Logger;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
	public static String DEFAULT_PATH = "de/hzg/common/hibernate.cfg.xml";
	private final SessionFactory sessionFactory;
	private final DataSource dataSource;
	private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());

	public HibernateUtil(Configuration configuration, PoolConfigurator poolConfigurator) throws HibernateUtilSetupException {
		final org.hibernate.cfg.Configuration hibernateConfiguration = new org.hibernate.cfg.Configuration();
		hibernateConfiguration.configure(DEFAULT_PATH);
		final PoolConfiguration poolConfiguration;

		try {
			poolConfiguration = getPoolConfiguration(configuration, poolConfigurator);
		} catch (ConfigurationNotFound exception) {
			logger.severe("Unable to extract information from the configuration for the hibernate database.");
			final String stackTrace = ExceptionUtil.stackTraceToString(exception);
			logger.severe(stackTrace);
			throw new HibernateUtilSetupException();
		}

		this.dataSource = new DataSource(poolConfiguration);
		hibernateConfiguration.getProperties().put(Environment.DATASOURCE, dataSource);
		final StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
		serviceRegistryBuilder.applySettings(hibernateConfiguration.getProperties());
		final ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
		sessionFactory = hibernateConfiguration.buildSessionFactory(serviceRegistry);
	}

	public HibernateUtil(Configuration configuration) throws HibernateUtilSetupException {
		this(configuration, null);
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public static PoolConfiguration getPoolConfiguration(Configuration configuration, PoolConfigurator poolConfigurator) throws ConfigurationNotFound {
		final PoolConfiguration poolConfiguration = new PoolProperties();
		final DatabaseConfiguration databaseConfiguration = configuration.getDatabaseConfiguration("probes");
		final String host = databaseConfiguration.getHost();
		final Integer port = databaseConfiguration.getPort();
		final String name = databaseConfiguration.getName();
		final String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, name);

		poolConfiguration.setDriverClassName(databaseConfiguration.getDriver());
		poolConfiguration.setUsername(databaseConfiguration.getUsername());
		poolConfiguration.setPassword(databaseConfiguration.getPassword());
		poolConfiguration.setUrl(url);

		if (poolConfigurator != null) {
			poolConfigurator.configure(poolConfiguration);
		}

		return poolConfiguration;
	}
}