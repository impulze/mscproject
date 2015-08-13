package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.ObservedPropertyDescription;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ObservedPropertyDescriptionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4233320038919524556L;
	private List<ObservedPropertyDescription> observedPropertyDescriptions;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public ObservedPropertyDescriptionTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<ObservedPropertyDescription> getObservedPropertyDescriptions() {
		return observedPropertyDescriptions;
	}

	public void setObservedPropertyDescriptions(List<ObservedPropertyDescription> observedPropertyDescriptions) {
		this.observedPropertyDescriptions = observedPropertyDescriptions;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (observedPropertyDescriptions == null) {
			return 0;
		}

		return observedPropertyDescriptions.size();
	}

	public Object getValueAt(int row, int column) {
		if (observedPropertyDescriptions.size() < row) {
			return null;
		}

		final ObservedPropertyDescription observedPropertyDescription = observedPropertyDescriptions.get(row);

		switch (column) {
			case 0: return observedPropertyDescription.getName();
			case 1: return observedPropertyDescription.getClassName();
			case 2: return observedPropertyDescription.getUnit();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final ObservedPropertyDescription observedPropertyDescription = observedPropertyDescriptions.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: observedPropertyDescription.setName((String)value); break;
			case 1: observedPropertyDescription.setClassName((String)value); break;
			case 2: observedPropertyDescription.setUnit((String)value); break;
			default: assert(false);
		}

		try {
			session.update(observedPropertyDescription);
			session.flush();
			JOptionPane.showMessageDialog(owner, "Observed property successfully updated.", "Observed property updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Observed property could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property not updated", messages, exception);
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