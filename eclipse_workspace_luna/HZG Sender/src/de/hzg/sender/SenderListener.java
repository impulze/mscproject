package de.hzg.sender;

import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.hzg.common.Configuration;
import de.hzg.common.ConfigurationSetupException;
import de.hzg.common.ExceptionUtil;
import de.hzg.common.HibernateUtil;
import de.hzg.common.HibernateUtilSetupException;

@WebListener
public class SenderListener implements ServletContextListener {
	private Sender sender;
	private Logger logger;

	public SenderListener() {
	}

	public void contextInitialized(ServletContextEvent event) {
		final ServletContext servletContext = event.getServletContext();

		logger = Logger.getLogger(SenderListener.class.getName());

		final Configuration configuration;
		final HibernateUtil hibernateUtil;
		final Sender sender;

		try {
			try {
				configuration = new Configuration();
				servletContext.setAttribute("configuration",  configuration);
			} catch (ConfigurationSetupException exception) {
				logger.severe("Error creating configuration.");
				throw exception;
			}

			try {
				hibernateUtil = new HibernateUtil(configuration);
				servletContext.setAttribute("hibernateUtil", hibernateUtil);
			} catch (HibernateUtilSetupException exception) {
				logger.severe("Error creating hibernate utility.");
				throw exception;
			}

			try {
				sender = new Sender(hibernateUtil, configuration.getHTTPSenderConfiguration());
			} catch (Exception exception) {
				logger.severe("Error creating sender.");
				throw exception;
			}
		} catch (Throwable exception) {
			final String stackTrace = ExceptionUtil.stackTraceToString(exception);
			logger.severe(stackTrace);
			return;
		}

		sender.start();
	}


	public void contextDestroyed(ServletContextEvent event) {
		if (sender != null) {
			sender.cancel();
		}
	}
}