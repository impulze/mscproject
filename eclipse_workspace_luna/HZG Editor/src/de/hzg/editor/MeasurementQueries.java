package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.Sensor;
import de.hzg.measurement.ProcedureDescription;

public class MeasurementQueries {
	public static List<String> getProcedureDescriptionNames(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<String> result;

		try {
			@SuppressWarnings("unchecked")
			final List<String> tempResult = (List<String>)session
				.createQuery("SELECT DISTINCT sd.name FROM ProcedureDescription sd")
				.list();
			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Procedure names could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure names could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}

	public static List<ProcedureDescription> getProcedureDescriptions(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<ProcedureDescription> result;

		try {
			@SuppressWarnings("unchecked")
			final List<ProcedureDescription> tempResult = (List<ProcedureDescription>)session
				.createQuery("FROM ProcedureDescription")
				.list();

			for (final ProcedureDescription sensorDescription: tempResult) {
				sensorDescription.initProcedureDescription();
			}

			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Procedure descriptions could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure descriptions could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}

	public static List<Sensor> getSensors(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<Sensor> result;

		try {
			@SuppressWarnings("unchecked")
			final List<Sensor> tempResult = (List<Sensor>)session
				.createQuery("FROM Sensor")
				.list();

			for (final Sensor sensor: tempResult) {
				sensor.initSensor();
			}

			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Sensors could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensors could not loaded", messages, exception);
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
