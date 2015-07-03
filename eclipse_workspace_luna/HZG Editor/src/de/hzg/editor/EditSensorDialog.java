package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.SensorDescription;

public class EditSensorDialog extends EditDialog<SensorDescription> {
	private static final long serialVersionUID = 7612051986074038024L;
	private SensorDescription sensorDescription;

	public EditSensorDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select sensor to edit");

		final JLabel sensorNameLabel = new JLabel("Sensor name:");
		final SensorDescriptionComboBox sensorDescriptionComboBox = new SensorDescriptionComboBox(owner, sessionFactory);

		sensorNameLabel.setLabelFor(sensorDescriptionComboBox);

		getContentPanel().add(sensorNameLabel);
		getContentPanel().add(sensorDescriptionComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditSensorDialog.this.sensorDescription = (SensorDescription)sensorDescriptionComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected SensorDescription getResult() {
		return sensorDescription;
	}
}