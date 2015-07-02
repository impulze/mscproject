package de.hzg.editor;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.hzg.sensors.SensorDescription;

public class SensorDescriptionCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -3950663079389697341L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final SensorDescription sensorDescription = (SensorDescription)value;

		return super.getTableCellRendererComponent(table, sensorDescription.getName(), isSelected, hasFocus, row, column);
	}
}