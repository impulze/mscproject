package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.SensorDescription;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SensorDescriptionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4233320038919524556L;
	private List<SensorDescription> sensorDescriptions;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public SensorDescriptionTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<SensorDescription> getSensorDescriptions() {
		return sensorDescriptions;
	}

	public void setSensorDescriptions(List<SensorDescription> sensorDescriptions) {
		this.sensorDescriptions = sensorDescriptions;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (sensorDescriptions == null) {
			return 0;
		}

		return sensorDescriptions.size();
	}

	public Object getValueAt(int row, int column) {
		if (sensorDescriptions.size() < row) {
			return null;
		}

		final SensorDescription sensorDescription = sensorDescriptions.get(row);

		switch (column) {
			case 0: return sensorDescription.getName();
			case 1: return sensorDescription.getClassName();
			case 2: return sensorDescription.getUnit();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final SensorDescription sensorDescription = sensorDescriptions.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: sensorDescription.setName((String)value); break;
			case 1: sensorDescription.setClassName((String)value); break;
			case 2: sensorDescription.setUnit((String)value); break;
			default: assert(false);
		}

		try {
			session.update(sensorDescription);
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
		final Class<?>[] columnClasses = { String.class, String.class, String.class };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Name", "Java Class", "Unit" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		 // TODO: allow this?!
		return true;
	}
}