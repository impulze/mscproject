package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import de.hzg.common.SensorClassesConfiguration;

public class EditSensorJavaClassDialog extends EditDialog<SensorJavaClass> {
	private static final long serialVersionUID = -1282131025164002098L;
	private SensorJavaClass sensorJavaClass;

	public EditSensorJavaClassDialog(Window owner, SensorClassesConfiguration sensorClassesConfiguration) {
		super(owner, "Select sensor class to edit");

		final JLabel sensorJavaClassLabel = new JLabel("Sensor class:");
		final SensorJavaClassComboBox sensorJavaClassComboBox = new SensorJavaClassComboBox(owner, sensorClassesConfiguration);

		sensorJavaClassLabel.setLabelFor(sensorJavaClassComboBox);

		getContentPanel().add(sensorJavaClassLabel);
		getContentPanel().add(sensorJavaClassComboBox);

		final Window usedOwner = owner;
		final SensorClassesConfiguration usedSensorClassesConfiguration = sensorClassesConfiguration;

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String name = (String) sensorJavaClassComboBox.getSelectedItem();
				final SensorJavaClass sensorJavaClass = SensorJavaClass.loadByName(usedSensorClassesConfiguration, usedOwner, name);

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