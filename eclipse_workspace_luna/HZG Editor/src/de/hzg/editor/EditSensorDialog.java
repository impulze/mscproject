package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.SensorDescription;

public class EditSensorDialog extends EditDialog<SensorDescription> {
	private static final long serialVersionUID = 7612051986074038024L;
	private SensorDescription sensorDescription;

	public EditSensorDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select sensor to edit");

		final JLabel sensorNameLabel = new JLabel("Sensor name:");
		final JTextField sensorNameTextField = new JTextField();

		sensorNameTextField.setColumns(20);
		sensorNameLabel.setLabelFor(sensorNameTextField);

		getContentPanel().add(sensorNameLabel);
		getContentPanel().add(sensorNameTextField);

		final Window usedOwner = owner;
		final SessionFactory usedSessionFactory = sessionFactory;

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = usedSessionFactory.openSession();
				final String name = sensorNameTextField.getText();
				final List<SensorDescription> result;

				try {
					@SuppressWarnings("unchecked")
					final List<SensorDescription> tempResult = (List<SensorDescription>)session
						.createQuery("FROM SensorDescription WHERE name = :name")
						.setParameter("name", name)
						.list();

					result = tempResult;
					handleResult(result, name);
				} catch (Exception exception) {
					final String[] messages = { "Sensor could not be loaded.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(usedOwner, "Sensor not loaded", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(usedOwner);
					dialog.setVisible(true);
					return;
				} finally {
					session.close();
				}
			}

			public void handleResult(List<SensorDescription> result, String name) {
				if (result.size() == 0) {
					final String message = String.format(
						"The sensor with the name '%s' is not in the database.\n" +
						"Try to look it up via 'Edit -> List sensors'.",
						name);
					JOptionPane.showMessageDialog(EditSensorDialog.this, message, "Sensor not found", JOptionPane.ERROR_MESSAGE);
				} else if (result.size() != 1) {
					final String message = String.format(
							"The sensor with the name '%s' occurs multiple times in the database.\n" +
							"This is a program error and needs to be fixed manually by accessing the database.",
							name);
					JOptionPane.showMessageDialog(EditSensorDialog.this, message, "Duplicate sensor", JOptionPane.ERROR_MESSAGE);					
				} else {
					sensorDescription = result.get(0);
					setVisible(false);
					dispose();
				}
			}
		});
	}

	protected SensorDescription getResult() {
		return sensorDescription;
	}
}