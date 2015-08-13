package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.CalibrationSet;

public class EditCalibrationSetDialog extends EditDialog<CalibrationSet> {
	private static final long serialVersionUID = -3559000423088727636L;
	private CalibrationSet calibrationSet;

	public EditCalibrationSetDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select calibration set to edit");

		final JLabel calibrationSetLabel = new JLabel("Calibration set:");
		final CalibrationSetComboBox calibrationSetComboBox = new CalibrationSetComboBox(owner, sessionFactory);

		calibrationSetLabel.setLabelFor(calibrationSetComboBox);

		getContentPanel().add(calibrationSetLabel);
		getContentPanel().add(calibrationSetComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditCalibrationSetDialog.this.calibrationSet = (CalibrationSet)calibrationSetComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected CalibrationSet getResult() {
		return calibrationSet;
	}
}