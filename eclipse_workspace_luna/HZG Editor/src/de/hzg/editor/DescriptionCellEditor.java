package de.hzg.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import de.hzg.sensors.Probe;
import de.hzg.sensors.SensorDescription;
import de.hzg.sensors.SensorInstance;

public class DescriptionCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor {
	private static final long serialVersionUID = -1596419497648007649L;
	private final List<SensorDescription> sensorDescriptions;
	private SensorDescription activeSensorDescription;

	public DescriptionCellEditor(Probe probe) {
		final List<SensorInstance> sensorInstances = probe.getSensorInstances();
		final List<SensorDescription> sensorDescriptions = new ArrayList<SensorDescription>(sensorInstances.size());

		for (final SensorInstance sensorInstance: sensorInstances) {
			sensorDescriptions.add(sensorInstance.getSensorDescription());
		}

		this.sensorDescriptions = sensorDescriptions;
	}

	public Object getCellEditorValue() {
		return activeSensorDescription;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		@SuppressWarnings("unchecked")
		final JComboBox<String> cmbBox = (JComboBox<String>)arg0.getSource();
		final String selectedString = (String)cmbBox.getSelectedItem();

		for (final SensorDescription sensorDescription: sensorDescriptions) {
			if (selectedString.equals(sensorDescription.getName())) {
				activeSensorDescription = sensorDescription;
				break;
			}
		}

		fireEditingStopped();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
		assert(value instanceof String);

		final SensorDescription sensorDescriptionToSet = (SensorDescription)value;

		final JComboBox<String> cmbBox = new JComboBox<String>();

		for (final SensorDescription sensorDescription: sensorDescriptions) {
			cmbBox.addItem(sensorDescription.getName());
		}

		cmbBox.setSelectedItem(sensorDescriptionToSet.getName());
		cmbBox.addActionListener(this);

		return cmbBox;
	}
}