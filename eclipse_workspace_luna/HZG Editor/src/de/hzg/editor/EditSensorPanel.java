package de.hzg.editor;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.SensorDescription;

public class EditSensorPanel extends CreateEditSensorPanel implements DataProvider {
	private static final long serialVersionUID = 1243275400682168004L;

	public EditSensorPanel(Window owner, SessionFactory sessionFactory, SensorDescription sensorDescription) {
		super(owner, sessionFactory, sensorDescription);

		setDataProvider(this);
		setTitle("Edit sensor");
	}

	public void provide() {
		final Session session = getSessionFactory().openSession();

		try {
			formAndMetadataToSensorDescription();
			session.update(getSensorDescription());
			session.flush();
			setDirty(false);
			JOptionPane.showMessageDialog(getOwner(), "Sensor successfully updated.", "Sensor updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			setDirty(true);
			final String[] messages = { "Sensor could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(getOwner(), "Sensor not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(getOwner());
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}
}
