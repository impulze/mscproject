package de.hzg.editor;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ObservedPropertyJavaClassTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7111186243559108891L;
	private List<String> observedPropertyClassNames;

	public List<String> getObservedPropertyClassNames() {
		return observedPropertyClassNames;
	}

	public void setObservedPropertyClassNames(List<String> observedPropertyClassNames) {
		this.observedPropertyClassNames = observedPropertyClassNames;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		if (observedPropertyClassNames == null) {
			return 0;
		}

		return observedPropertyClassNames.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (observedPropertyClassNames.size() < row) {
			return null;
		}

		final String observedPropertyClassName = observedPropertyClassNames.get(row);

		return observedPropertyClassName;
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
