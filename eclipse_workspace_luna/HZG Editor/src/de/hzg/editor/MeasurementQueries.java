package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.SensorDescription;

public class MeasurementQueries {
	public static List<String> getSensorDescriptionNames(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<String> result;

		try {
			@SuppressWarnings("unchecked")
			final List<String> tempResult = (List<String>)session
				.createQuery("SELECT DISTINCT sd.name FROM SensorDescription sd")
				.list();
			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Sensor names could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor names could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}

	public static List<SensorDescription> getSensorDescriptions(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<SensorDescription> result;

		try {
			@SuppressWarnings("unchecked")
			final List<SensorDescription> tempResult = (List<SensorDescription>)session
				.createQuery("FROM SensorDescription")
				.list();

			for (final SensorDescription sensorDescription: tempResult) {
				sensorDescription.initSensorDescription();
			}

			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Sensor descriptions could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor descriptions could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}
}
