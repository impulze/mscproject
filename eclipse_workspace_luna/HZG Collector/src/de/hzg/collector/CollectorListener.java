package de.hzg.collector;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class CollectorListener implements ServletContextListener {
	private Thread workThread;

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event)  {
		final Logger rootLogger = Logger.getLogger("");
		final WebLogStream webLogStream = new WebLogStream();
		final Formatter formatter = new SimpleFormatter();
		final WebLogHandler webLogHandler = new WebLogHandler(webLogStream, formatter);

		webLogHandler.setLevel(Level.ALL);
		rootLogger.addHandler(webLogHandler);
		event.getServletContext().setAttribute("webLogHandler", webLogHandler);

		if (workThread == null || !workThread.isAlive()) {
			final Runnable collectorRunnable = new Collector();
			workThread = new Thread(collectorRunnable);
			workThread.start();
		}
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event)  {
		final Logger rootLogger = Logger.getLogger("");
		final Object webLogHandlerObject = event.getServletContext().getAttribute("webLogHandler");
		final WebLogHandler webLogHandler = (WebLogHandler) webLogHandlerObject;

		rootLogger.removeHandler(webLogHandler);

		try {
			workThread.interrupt();
		} catch (Exception exception) {
			// Handle thread exception
		}
	}
}