package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.ProcedureDescription;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ProcedureDescriptionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4233320038919524556L;
	private List<ProcedureDescription> procedureDescriptions;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public ProcedureDescriptionTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<ProcedureDescription> getProcedureDescriptions() {
		return procedureDescriptions;
	}

	public void setProcedureDescriptions(List<ProcedureDescription> procedureDescriptions) {
		this.procedureDescriptions = procedureDescriptions;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		if (procedureDescriptions == null) {
			return 0;
		}

		return procedureDescriptions.size();
	}

	public Object getValueAt(int row, int column) {
		if (procedureDescriptions.size() < row) {
			return null;
		}

		final ProcedureDescription procedureDescription = procedureDescriptions.get(row);

		switch (column) {
			case 0: return procedureDescription.getName();
			case 1: return procedureDescription.getClassName();
			case 2: return procedureDescription.getUnit();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final ProcedureDescription procedureDescription = procedureDescriptions.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: procedureDescription.setName((String)value); break;
			case 1: procedureDescription.setClassName((String)value); break;
			case 2: procedureDescription.setUnit((String)value); break;
			default: assert(false);
		}

		try {
			session.update(procedureDescription);
			session.flush();
			JOptionPane.showMessageDialog(owner, "Procedure successfully updated.", "Procedure updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Procedure could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure not updated", messages, exception);
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