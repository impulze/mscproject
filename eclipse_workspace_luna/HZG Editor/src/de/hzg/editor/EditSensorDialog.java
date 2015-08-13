package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.Sensor;

public class EditSensorDialog extends EditDialog<Sensor> {
	private static final long serialVersionUID = -4236878158400999625L;
	private Sensor sensor;

	public EditSensorDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select sensor to edit");

		final SensorComboBox sensorComboBox = new SensorComboBox(owner, sessionFactory);
		final JLabel sensorNameLabel = new JLabel("Sensor name:");

		sensorNameLabel.setLabelFor(sensorComboBox);

		getContentPanel().add(sensorNameLabel);
		getContentPanel().add(sensorComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditSensorDialog.this.sensor = (Sensor)sensorComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected Sensor getResult() {
		return sensor;
	}
}