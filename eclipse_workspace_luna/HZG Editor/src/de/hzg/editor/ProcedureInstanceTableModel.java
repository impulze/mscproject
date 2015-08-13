package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.ProcedureInstance;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ProcedureInstanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4685270309147674179L;
	private List<ProcedureInstance> procedureInstances;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public ProcedureInstanceTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<ProcedureInstance> getProcedureInstances() {
		return procedureInstances;
	}

	public void setProcedureInstances(List<ProcedureInstance> procedureInstances) {
		this.procedureInstances = procedureInstances;
	}

	public int getColumnCount() {
		return 8;
	}

	public int getRowCount() {
		if (procedureInstances == null) {
			return 0;
		}

		return procedureInstances.size();
	}

	public Object getValueAt(int row, int column) {
		if (procedureInstances.size() < row) {
			return null;
		}

		final ProcedureInstance procedureInstance = procedureInstances.get(row);

		switch (column) {
			case 0: return procedureInstance.getProcedureDescription();
			case 1: return procedureInstance.getAddress();
			case 2: return procedureInstance.getParameter1();
			case 3: return procedureInstance.getParameter2();
			case 4: return procedureInstance.getParameter3();
			case 5: return procedureInstance.getParameter4();
			case 6: return procedureInstance.getParameter5();
			case 7: return procedureInstance.getParameter6();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final ProcedureInstance procedureInstance = procedureInstances.get(row);
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: procedureInstance.setProcedureDescription((ProcedureDescription)value); break;
			case 1: procedureInstance.setAddress((Integer)value); break;
			case 2: procedureInstance.setParameter1((Double)value); break;
			case 3: procedureInstance.setParameter2((Double)value); break;
			case 4: procedureInstance.setParameter3((Double)value); break;
			case 5: procedureInstance.setParameter4((Double)value); break;
			case 6: procedureInstance.setParameter5((Double)value); break;
			case 7: procedureInstance.setParameter6((Double)value); break;
			default: assert(false);
		}

		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			session.update(procedureInstance);

			if (column == 0) {
				final ProcedureDescription procedureDescription = (ProcedureDescription)value;

				procedureDescription.getProcedureInstances().add(procedureInstance);
				session.update(procedureDescription);
			}

			session.flush();
			transaction.commit();
			JOptionPane.showMessageDialog(owner, "Procedure instance successfully updated.", "Procedure instance updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Procedure instance could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure instance not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { ProcedureDescription.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class };
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
