package de.hzg.collector;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class CollectorListener implements ServletContextListener {
	private Thread workThread;
	private Collector collector;
	private Logger logger;

	/*
	 * TODO: link external libraries into tomcat library directory
	 * impulze@ubuntu:~/apache-tomcat-7.0.62/lib$ ln -s /usr/share/java/RXTXcomm.jar
	 * impulze@ubuntu:~/apache-tomcat-7.0.62/lib$ ln -s /usr/share/java/postgresql-jdbc4.jar
	 */

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		final Logger rootLogger = Logger.getLogger("");
		final WebLogStream webLogStream = new WebLogStream();
		final Formatter formatter = new SimpleFormatter();
		final WebLogHandler webLogHandler = new WebLogHandler(webLogStream, formatter);
		final ServletContext servletContext = event.getServletContext();

		webLogHandler.setLevel(Level.ALL);
		rootLogger.addHandler(webLogHandler);
		servletContext.setAttribute("webLogHandler", webLogHandler);

		logger = Logger.getLogger(CollectorListener.class.getName());

		try {
			collector = new Collector();
		} catch (Exception exception) {
			logger.severe("Error creating collector instance.");
			logger.severe(exception.toString());
			removeWebLogHandler(event.getServletContext());
			return;
		}

		workThread = new Thread(collector);
		workThread.start();
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event)  {
		if (collector != null) {
			collector.shutdown();

			logger.info("Waiting for collector instance to shut down.");

			workThread.interrupt();

			while(!collector.finished()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException exception) {
				}
			}
		}

		removeWebLogHandler(event.getServletContext());
	}

	private void removeWebLogHandler(ServletContext servletContext) {
		final Logger rootLogger = Logger.getLogger("");
		final Object webLogHandlerObject = servletContext.getAttribute("webLogHandler");
		final WebLogHandler webLogHandler = (WebLogHandler) webLogHandlerObject;

		rootLogger.removeHandler(webLogHandler);
	}
}