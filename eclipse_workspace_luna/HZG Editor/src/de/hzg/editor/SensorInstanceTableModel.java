package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.SensorDescription;
import de.hzg.measurement.SensorInstance;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SensorInstanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4685270309147674179L;
	private List<SensorInstance> sensorInstances;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public SensorInstanceTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<SensorInstance> getSensorInstances() {
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
			case 0: return sensorInstance.getSensorDescription();
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
		final SensorInstance sensorInstance = sensorInstances.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: sensorInstance.setSensorDescription((SensorDescription)value); break;
			case 1: sensorInstance.setAddress((Integer)value); break;
			case 2: sensorInstance.setParameter1((Double)value); break;
			case 3: sensorInstance.setParameter2((Double)value); break;
			case 4: sensorInstance.setParameter3((Double)value); break;
			case 5: sensorInstance.setParameter4((Double)value); break;
			case 6: sensorInstance.setParameter5((Double)value); break;
			case 7: sensorInstance.setParameter6((Double)value); break;
			default: assert(false);
		}

		try {
			session.update(sensorInstance);
			session.flush();
			JOptionPane.showMessageDialog(owner, "Sensor instance successfully updated.", "Sensor instance updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Sensor instance could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor instance not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { SensorDescription.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class };
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
