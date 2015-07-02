package de.hzg.editor;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.SensorDescription;

public class CreateSensorPanel extends CreateEditSensorPanel implements DataProvider {
	private static final long serialVersionUID = -5095848120481137332L;

	public CreateSensorPanel(Window owner, SessionFactory sessionFactory) {
		super(owner, sessionFactory, createEmptySensorDescription());

		setDataProvider(this);
		setTitle("Create sensor");
	}

	private static SensorDescription createEmptySensorDescription() {
		final SensorDescription sensorDescription = new SensorDescription();

		sensorDescription.setMetadata(getMetadataTemplate());

		return sensorDescription;
	}

	public void provide() {
		final Session session = getSessionFactory().openSession();

		try {
			formToSensorDescription();
			metadataToSensorDescription();
			session.save(getSensorDescription());
			session.flush();
			sensorDescriptionToFormAndMetadata();
			setDirty(false);
			JOptionPane.showMessageDialog(getOwner(), "Sensor successfully saved.", "Sensor saved", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			setDirty(true);
			final String[] messages = { "Sensor could not be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(getOwner(), "Sensor not saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(getOwner());
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}
}
