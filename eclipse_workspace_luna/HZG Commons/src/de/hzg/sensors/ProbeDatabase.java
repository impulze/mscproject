package de.hzg.sensors;

import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.hibernate.Session;

import de.hzg.sensors.HibernateUtil;

public class ProbeDatabase {
	private static final Logger logger = Logger.getLogger(ProbeDatabase.class.getName());

	public List<Probe> getActiveProbes() {
		final Session session = HibernateUtil.getInstance().getSessionFactory().openSession();
		final String queryString = "FROM de.hzg.sensors.Probe WHERE active = true";// PROBE WHERE PROBE.active = true";
		final Query query = session.createQuery(queryString);

		@SuppressWarnings("unchecked")
		final List<Probe> activeProbes = query.list();

		for (final Probe probe: activeProbes) {
			System.out.println("Active probe: " + probe.getName());
		}
		return activeProbes;
	}
}