package de.hzg.editor;

import java.util.List;

import de.hzg.sensors.SensorInstance;

import javax.swing.table.AbstractTableModel;

public class SensorInstanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4685270309147674179L;
	private List<SensorInstance> sensorInstances;

	public List<SensorInstance> getSensorInstance() {
		return sensorInstances;
	}

	public void setSensorInstances(List<SensorInstance> sensorInstances) {
		this.sensorInstances = sensorInstances;
	}

	public int getColumnCount() {
		return 8;
	}

	public int getRowCount() {
		if (sensorInstances == null) {
			return 0;
		}

		return sensorInstances.size();
	}

	public Object getValueAt(int row, int column) {
		if (sensorInstances.size() < row) {
			return null;
		}

		final SensorInstance sensorInstance = sensorInstances.get(row);

		switch (column) {
			case 0: return sensorInstance. getDescription().getName();
			case 1: return sensorInstance.getAddress();
			case 2: return sensorInstance.getParameter1();
			case 3: return sensorInstance.getParameter2();
			case 4: return sensorInstance.getParameter3();
			case 5: return sensorInstance.getParameter4();
			case 6: return sensorInstance.getParameter5();
			case 7: return sensorInstance.getParameter6();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		System.out.println("want to set: " + value);
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { String.class, String.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Description", "Address", "Parameter1", "Parameter2", "Parameter3", "Parameter4", "Parameter5", "Parameter6" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		return true;
	}
}
