package de.hzg.editor;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ProcedureJavaClassTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7111186243559108891L;
	private List<String> procedureClassNames;

	public List<String> getProcedureClassNames() {
		return procedureClassNames;
	}

	public void setProcedureClassNames(List<String> procedureClassNames) {
		this.procedureClassNames = procedureClassNames;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		if (procedureClassNames == null) {
			return 0;
		}

		return procedureClassNames.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (procedureClassNames.size() < row) {
			return null;
		}

		final String procedureClassName = procedureClassNames.get(row);

		return procedureClassName;
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
