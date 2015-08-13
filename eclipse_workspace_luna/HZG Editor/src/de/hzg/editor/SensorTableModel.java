package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.Sensor;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SensorTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -3745151539485746593L;
	private List<Sensor> sensors;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public SensorTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (sensors == null) {
			return 0;
		}

		return sensors.size();
	}

	public Object getValueAt(int row, int column) {
		if (sensors.size() < row) {
			return null;
		}

		final Sensor sensor = sensors.get(row);

		switch (column) {
			case 0: return sensor.getActive();
			case 1: return sensor.getName();
			case 2: return sensor.getDevice();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final Sensor sensor = sensors.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: sensor.setActive((Boolean)value); break;
			case 1: sensor.setName((String)value); break;
			case 2: sensor.setDevice((String)value); break;
			default: assert(false);
		}

		try {
			session.update(sensor);
			session.flush();
			JOptionPane.showMessageDialog(owner, "Sensor successfully updated.", "Sensor updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Sensor could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { Boolean.class, String.class, String.class };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Active", "Name", "Device" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		return true;
	}
}