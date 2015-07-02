package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.sensors.Probe;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ProbeTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -3745151539485746593L;
	private List<Probe> probes;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public ProbeTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<Probe> getProbes() {
		return probes;
	}

	public void setProbes(List<Probe> probes) {
		this.probes = probes;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (probes == null) {
			return 0;
		}

		return probes.size();
	}

	public Object getValueAt(int row, int column) {
		if (probes.size() < row) {
			return null;
		}

		final Probe probe = probes.get(row);

		switch (column) {
			case 0: return probe.getActive();
			case 1: return probe.getName();
			case 2: return probe.getDevice();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final Probe probe = probes.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: probe.setActive((Boolean)value); break;
			case 1: probe.setName((String)value); break;
			case 2: probe.setDevice((String)value); break;
			default: assert(false);
		}

		try {
			session.update(probe);
			session.flush();
			JOptionPane.showMessageDialog(owner, "Probe successfully updated.", "Probe updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Probe could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Probe not updated", messages, exception);
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