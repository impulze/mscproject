package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.hzg.common.SensorClassesConfiguration;

public class EditSensorJavaClassDialog extends EditDialog<SensorJavaClass> {
	private static final long serialVersionUID = -1282131025164002098L;
	private SensorJavaClass sensorJavaClass;

	public EditSensorJavaClassDialog(Window owner, SensorClassesConfiguration sensorClassesConfiguration) {
		super(owner, "Select sensor class to edit");

		final JLabel sensorClassLabel = new JLabel("Sensor class:");
		final JTextField sensorClassTextField = new JTextField();

		sensorClassTextField.setColumns(20);
		sensorClassLabel.setLabelFor(sensorClassTextField);

		getContentPanel().add(sensorClassLabel);
		getContentPanel().add(sensorClassTextField);

		final Window usedOwner = owner;
		final SensorClassesConfiguration usedSensorClassesConfiguration = sensorClassesConfiguration;

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String name = sensorClassTextField.getText();
				final SensorJavaClass sensorJavaClass = new SensorJavaClass(usedSensorClassesConfiguration);

				try {
					sensorJavaClass.setName(name);
				} catch (InvalidIdentifierException exception) {
					final String[] messages = { "Sensor class has an invalid identifier.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(usedOwner, "Sensor class has an invalid identifier", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(usedOwner);
					dialog.setVisible(true);
					return;
				}

				try {
					sensorJavaClass.load();
				} catch (IOException exception) {
					final String[] messages = { "Sensor class could not be loaded.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(usedOwner, "Sensor class not loaded", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(usedOwner);
					dialog.setVisible(true);
					return;
				}

				EditSensorJavaClassDialog.this.sensorJavaClass = sensorJavaClass;
				setVisible(false);
				dispose();
			}
		});
	}

	protected SensorJavaClass getResult() {
		return sensorJavaClass;
	}
}