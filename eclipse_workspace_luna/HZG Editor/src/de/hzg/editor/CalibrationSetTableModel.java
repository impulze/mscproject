package de.hzg.editor;

import java.awt.Window;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyInstance;

public class CalibrationSetTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -3767953135976940496L;
	private List<CalibrationSet> calibrationSets;
	private final SessionFactory sessionFactory;
	private final Window owner;

	public CalibrationSetTableModel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
	}

	public List<CalibrationSet> getCalibrationSets() {
		return calibrationSets;
	}

	public void setCalibrationSets(List<CalibrationSet> calibrationSets) {
		this.calibrationSets = calibrationSets;
	}

	public int getColumnCount() {
		return 9;
	}

	public int getRowCount() {
		if (calibrationSets == null) {
			return 0;
		}

		return calibrationSets.size();
	}

	public Object getValueAt(int row, int column) {
		if (calibrationSets.size() < row) {
			return null;
		}

		final CalibrationSet calibrationSet = calibrationSets.get(row);

		switch (column) {
			case 0: return calibrationSet.getValidStart();
			case 1: return calibrationSet.getValidEnd();
			case 2: return calibrationSet.getObservedPropertyInstance();
			case 3: return calibrationSet.getParameter1();
			case 4: return calibrationSet.getParameter2();
			case 5: return calibrationSet.getParameter3();
			case 6: return calibrationSet.getParameter4();
			case 7: return calibrationSet.getParameter5();
			case 8: return calibrationSet.getParameter6();
			default: assert(false);
		}

		return null;
	}

	public void setValueAt(Object value, int row, int column) {
		final CalibrationSet calibrationSet = calibrationSets.get(row);
		final ObservedPropertyInstance observedPropertyInstance = calibrationSet.getObservedPropertyInstance();
		final boolean isActive = observedPropertyInstance.getActiveCalibrationSet().getId().equals(calibrationSet.getId());

		switch (column) {
			case 0: calibrationSet.setValidStart(new Timestamp(((Date)value).getTime())); break;
			case 1: calibrationSet.setValidEnd(new Timestamp(((Date)value).getTime())); break;
			case 2: {
				if (isActive) {
					CreateEditCalibrationSetPanel.activeError(owner, observedPropertyInstance);
					return;
				}

				calibrationSet.setObservedPropertyInstance((ObservedPropertyInstance)value); break;
			}
			case 3: calibrationSet.setParameter1((Double)value); break;
			case 4: calibrationSet.setParameter2((Double)value); break;
			case 5: calibrationSet.setParameter3((Double)value); break;
			case 6: calibrationSet.setParameter4((Double)value); break;
			case 7: calibrationSet.setParameter5((Double)value); break;
			case 8: calibrationSet.setParameter6((Double)value); break;
			default: assert(false);
		}

		final Session session = sessionFactory.openSession();

		try {
			session.update(calibrationSet);
			session.flush();
			JOptionPane.showMessageDialog(owner, "Calibration set successfully updated.", "Observed property updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Calibration set could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Calibration set not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}

	public Class<?> getColumnClass(int column) {
		final Class<?>[] columnClasses = { DateTimePicker.class, DateTimePicker.class, ObservedPropertyInstance.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class };
		return columnClasses[column];
	}

	public String getColumnName(int column) {
		final String[] columnNames = { "Valid Start", "Valid End", "Observed Property", "Parameter 1", "Parameter 2", "Parameter 3", "Parameter 4", "Parameter 5", "Parameter 6" };
		return columnNames[column];
	}

	 public boolean isCellEditable(int row, int column) {
		 // TODO: allow this?!
		return true;
	}
}