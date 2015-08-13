package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ObservedPropertyInstanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4685270309147674179L;
	private List<ObservedPropertyInstance> observedPropertyInstances;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public ObservedPropertyInstanceTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<ObservedPropertyInstance> getObservedPropertyInstances() {
		return observedPropertyInstances;
	}

	public void setObservedPropertyInstances(List<ObservedPropertyInstance> observedPropertyInstances) {
		this.observedPropertyInstances = observedPropertyInstances;
	}

	public int getColumnCount() {
		return 12;
	}

	public int getRowCount() {
		if (observedPropertyInstances == null) {
			return 0;
		}

		return observedPropertyInstances.size();
	}

	public Object getValueAt(int row, int column) {
		if (observedPropertyInstances.size() < row) {
			return null;
		}

		final ObservedPropertyInstance observedPropertyInstance = observedPropertyInstances.get(row);
		final CalibrationSet activeCalibrationSet = observedPropertyInstance.getActiveCalibrationSet();

		switch (column) {
			case 0: return observedPropertyInstance.getObservedPropertyDescription();
			case 1: return observedPropertyInstance.getAddress();
			case 2: return observedPropertyInstance.getName();
			case 3: return observedPropertyInstance.getIsRaw();
			case 4: return observedPropertyInstance.getUseInterval();
			case 5: return activeCalibrationSet;
			case 6: return activeCalibrationSet.getParameter1();
			case 7: return activeCalibrationSet.getParameter2();
			case 8: return activeCalibrationSet.getParameter3();
			case 9: return activeCalibrationSet.getParameter4();
			case 10: return activeCalibrationSet.getParameter5();
			case 11: return activeCalibrationSet.getParameter6();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final ObservedPropertyInstance observedPropertyInstance = observedPropertyInstances.get(row);
		CalibrationSet activeCalibrationSet = observedPropertyInstance.getActiveCalibrationSet();
		final Session session = sessionFactory.openSession();

		switch (column) {
			case 0: {
				final ObservedPropertyDescription observedPropertyDescription = (ObservedPropertyDescription)value;

				observedPropertyInstance.setObservedPropertyDescription(observedPropertyDescription);
				observedPropertyDescription.getObservedPropertyInstances().add(observedPropertyInstance);

				break;
			}
			case 1: observedPropertyInstance.setAddress((Integer)value); break;
			case 2: observedPropertyInstance.setName((String)value); break;
			case 3: observedPropertyInstance.setIsRaw((Boolean)value); break;
			case 4: observedPropertyInstance.setUseInterval((Boolean)value); break;
			case 5: {
				activeCalibrationSet = (CalibrationSet)value;

				activeCalibrationSet.setObservedPropertyInstance(observedPropertyInstance);
				observedPropertyInstance.setActiveCalibrationSet(activeCalibrationSet);

				break;
			}
			case 6: activeCalibrationSet.setParameter1((Double)value); break;
			case 7: activeCalibrationSet.setParameter2((Double)value); break;
			case 8: activeCalibrationSet.setParameter3((Double)value); break;
			case 9: activeCalibrationSet.setParameter4((Double)value); break;
			case 10: activeCalibrationSet.setParameter5((Double)value); break;
			case 11: activeCalibrationSet.setParameter6((Double)value); break;
			default: assert(false);
		}

		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			session.update(observedPropertyInstance);

			switch (column) {
				case 0: session.update((ObservedPropertyDescription)value); break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11: session.update(activeCalibrationSet); break;
			}

			session.flush();
			transaction.commit();
			JOptionPane.showMessageDialog(owner, "Observed property instance successfully updated.", "Observed property instance updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Observed property instance could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property instance not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { ObservedPropertyDescription.class, Integer.class, String.class, Boolean.class, Boolean.class, CalibrationSet.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Description", "Address", "Name", "Is Raw", "Use Interval", "Calibration Set", "Parameter1", "Parameter2", "Parameter3", "Parameter4", "Parameter5", "Parameter6" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		return true;
	}
}
