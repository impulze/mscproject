package de.hzg.collector;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class CollectorListener
 *
 */
@WebListener
public class CollectorListener implements ServletContextListener {
	private Thread workThread_ = null;

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 	
    	if (workThread_ == null || !workThread_.isAlive()) {
    		Runnable collectorRunnable = new Collector();
    		workThread_ = new Thread(collectorRunnable);
    		workThread_.start();	
    	}
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         try {
        	 // TODO: Gracefully handle the interruption in thread
        	 workThread_.interrupt();
         } catch (Exception exception) {
        	 // Handle thread exception
         }
    }
}