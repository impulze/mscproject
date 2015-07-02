package de.hzg.editor;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SensorClassTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7111186243559108891L;
	private List<String> sensorClassNames;

	public void setSensorClassNames(List<String> sensorClassNames) {
		this.sensorClassNames = sensorClassNames;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		if (sensorClassNames == null) {
			return 0;
		}

		return sensorClassNames.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (sensorClassNames.size() < row) {
			return null;
		}

		final String sensorClassName = sensorClassNames.get(row);

		return sensorClassName;
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { String.class };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Name" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		return false;
	}
}
