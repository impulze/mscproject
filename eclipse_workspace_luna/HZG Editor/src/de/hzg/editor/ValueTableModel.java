package de.hzg.editor;

import java.awt.Window;
import java.text.DateFormat;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.hibernate.SessionFactory;

import de.hzg.values.ValueData;

public class ValueTableModel<T extends ValueData<E>, E> extends AbstractTableModel {
	private static final long serialVersionUID = -5496894386062885766L;
	private List<T> values;
	@SuppressWarnings("unused")
	private final SessionFactory sessionFactory;
	@SuppressWarnings("unused")
	private final Window owner;
	private final Class<E> classType;
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);

	public ValueTableModel(Window owner, SessionFactory sessionFactory, Class<E> classType) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.classType = classType;
	}

	public List<T> getValues() {
		return values;
	}

	public void setValues(List<T> values) {
		this.values = values;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (values == null) {
			return 0;
		}

		return values.size();
	}

	public Object getValueAt(int row, int column) {
		if (values.size() < row) {
			return null;
		}

		final T value = values.get(row);
		final String procedureName = value.getProcedureInstance().getProcedureDescription().getName();
		final String sensorName = value.getProcedureInstance().getSensor().getName();

		switch (column) {
			// TODO: why?
			case 0: return dateFormat.format(value.getDate());
			case 1: return sensorName + "/" + procedureName;
			case 2: return value.getValue();
			default: assert(false);
		}

		return null;
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { String.class /* TODO: see above */, String.class, classType };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Date", "Sensor/Procedure", "Value" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		return false;
	}
}