package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;
import de.hzg.measurement.Sensor;

public class MeasurementQueries {
	public static CalibrationSet getActiveCalibrationSet(Window owner, SessionFactory sessionFactory, ObservedPropertyInstance observedPropertyInstance) {
		final Session session = sessionFactory.openSession();
		final CalibrationSet result;

		try {
			final CalibrationSet tempResult = (CalibrationSet)session
				.createQuery("FROM CalibrationSet WHERE active = true AND observedPropertyInstance = :observedPropertyInstance")
				.setParameter("observedPropertyInstance",  observedPropertyInstance)
				.uniqueResult();
			tempResult.initCalibrationSet();
			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Active calibration set could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Active calibration set could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}

	public static List<String> getObservedPropertyDescriptionNames(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<String> result;

		try {
			@SuppressWarnings("unchecked")
			final List<String> tempResult = (List<String>)session
				.createQuery("SELECT DISTINCT sd.name FROM ObservedPropertyDescription sd")
				.list();
			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Observed property names could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property names could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}

	public static List<ObservedPropertyDescription> getObservedPropertyDescriptions(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<ObservedPropertyDescription> result;

		try {
			@SuppressWarnings("unchecked")
			final List<ObservedPropertyDescription> tempResult = (List<ObservedPropertyDescription>)session
				.createQuery("FROM ObservedPropertyDescription")
				.list();

			for (final ObservedPropertyDescription observedPropertyDescription: tempResult) {
				observedPropertyDescription.initObservedPropertyDescription();
			}

			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Observed property descriptions could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property descriptions could not loaded", messages, exception);
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

	public static List<CalibrationSet> getCalibrationSets(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<CalibrationSet> result;

		try {
			@SuppressWarnings("unchecked")
			final List<CalibrationSet> tempResult = (List<CalibrationSet>)session
				.createQuery("FROM CalibrationSet")
				.list();

			for (final CalibrationSet calibrationSet: tempResult) {
				calibrationSet.initCalibrationSet();
			}

			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Calibration sets could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Calibration sets could not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			throw exception;
		} finally {
			session.close();
		}

		return result;
	}

	public static List<ObservedPropertyInstance> getObservedPropertyInstances(Window owner, SessionFactory sessionFactory) {
		final Session session = sessionFactory.openSession();
		final List<ObservedPropertyInstance> result;

		try {
			@SuppressWarnings("unchecked")
			final List<ObservedPropertyInstance> tempResult = (List<ObservedPropertyInstance>)session
				.createQuery("FROM ObservedPropertyInstance")
				.list();

			for (final ObservedPropertyInstance observedPropertyInstance: tempResult) {
				observedPropertyInstance.initObservedPropertyInstance();
			}

			result = tempResult;
		} catch (Exception exception) {
			final String[] messages = { "Observed property instances could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property instances could not loaded", messages, exception);
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
