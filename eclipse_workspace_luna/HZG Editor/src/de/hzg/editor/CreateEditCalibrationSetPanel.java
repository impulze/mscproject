package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyInstance;

public class CreateEditCalibrationSetPanel extends SplitPanel implements DataProvider  {
	private static final long serialVersionUID = 9060506476811395650L;
	private final DateTimePicker startDatePicker = new DateTimePicker();
	private final DateTimePicker endDatePicker = new DateTimePicker(startDatePicker.getDate());
	private final ObservedPropertyInstanceComboBox observedPropertyInstanceComboBox;
	private final JTextField parameterTextFields[] = new JTextField[6];
	private final Window owner;
	private final SessionFactory sessionFactory;
	private CalibrationSet calibrationSet;
	private RemoveListener removeListener;
	private SequentialGroup horizontalButtonGroup;
	private ParallelGroup verticalButtonGroup;
	private boolean editFunctionsShown = false;
	private static NumberFormat formatter = new DecimalFormat("0.00000E0");

	public CreateEditCalibrationSetPanel(Window owner, SessionFactory sessionFactory, CalibrationSet calibrationSet) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.calibrationSet = calibrationSet;
		this.observedPropertyInstanceComboBox = new ObservedPropertyInstanceComboBox(owner, sessionFactory);

		for (int i = 0; i < parameterTextFields.length; i++) {
			parameterTextFields[i] = new JFormattedTextField(formatter);
			parameterTextFields[i].setColumns(12);
		}

		createForm();

		setDataProvider(this);
		calibrationSetToForm();
	}

	public static CalibrationSet createNewCalibrationSet() {
		final Double parameter = 0.0;
		final CalibrationSet calibrationSet = new CalibrationSet();
		final Calendar now = Calendar.getInstance();
		final Calendar end = (Calendar)now.clone();

		end.add(Calendar.YEAR, 50);

		calibrationSet.setValidStart(new Timestamp(now.getTimeInMillis()));
		calibrationSet.setValidEnd(new Timestamp(end.getTimeInMillis()));
		calibrationSet.setParameter1(parameter);
		calibrationSet.setParameter2(parameter);
		calibrationSet.setParameter3(parameter);
		calibrationSet.setParameter4(parameter);
		calibrationSet.setParameter5(parameter);
		calibrationSet.setParameter6(parameter);

		return calibrationSet;
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Save information");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final JLabel lblStartDate = new JLabel("Start:");
		lblStartDate.setLabelFor(startDatePicker);

		final JLabel lblEndDate = new JLabel("End:");
		lblEndDate.setLabelFor(endDatePicker);

		final JLabel lblObservedPropertyInstance = new JLabel("Observed property instance:");
		lblObservedPropertyInstance.setLabelFor(observedPropertyInstanceComboBox);

		final JLabel lblParameters[] = new JLabel[6];

		for (int i = 0; i < parameterTextFields.length; i++) {
			lblParameters[i] = new JLabel("Parameter " + (i + 1) + ":");
			lblParameters[i].setLabelFor(parameterTextFields[i]);
		}

		final ParallelGroup labelsLayout = topPanelLayout.createParallelGroup(Alignment.TRAILING)
			.addComponent(lblStartDate)
			.addComponent(lblEndDate)
			.addComponent(lblObservedPropertyInstance);

		for (int i = 0; i < parameterTextFields.length; i++) {
			labelsLayout.addComponent(lblParameters[i]);
		}

		final ParallelGroup inputsLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addComponent(startDatePicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(endDatePicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(observedPropertyInstanceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);

		for (int i = 0; i < parameterTextFields.length; i++) {
			inputsLayout
			.addComponent(parameterTextFields[i], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		}

		final SequentialGroup labelsWithInputsLayout = topPanelLayout.createSequentialGroup()
			.addGroup(labelsLayout)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(inputsLayout);
		horizontalButtonGroup = topPanelLayout.createSequentialGroup()
			.addComponent(actionButton);
		final ParallelGroup labelsWithInputsAndButtonLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(labelsWithInputsLayout)
			.addGroup(horizontalButtonGroup);
		final SequentialGroup horizontalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(labelsWithInputsAndButtonLayout)
			.addContainerGap(251, Short.MAX_VALUE);

		verticalButtonGroup = topPanelLayout.createParallelGroup(Alignment.BASELINE)
			.addComponent(actionButton);
		final SequentialGroup verticalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblStartDate).addComponent(startDatePicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblEndDate).addComponent(endDatePicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblObservedPropertyInstance).addComponent(observedPropertyInstanceComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));

		for (int i = 0; i < parameterTextFields.length; i++) {
			verticalLayoutWithGaps
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblParameters[i]).addComponent(parameterTextFields[i], GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
		}

		verticalLayoutWithGaps
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(verticalButtonGroup)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private void calibrationSetToForm() {
		startDatePicker.setDate(new Date(calibrationSet.getValidStart().getTime()));
		endDatePicker.setDate(new Date(calibrationSet.getValidStart().getTime()));
		observedPropertyInstanceComboBox.setSelectedItem(calibrationSet.getObservedPropertyInstance());
		parameterTextFields[0].setText(Double.toString(calibrationSet.getParameter1()));
		parameterTextFields[1].setText(Double.toString(calibrationSet.getParameter2()));
		parameterTextFields[2].setText(Double.toString(calibrationSet.getParameter3()));
		parameterTextFields[3].setText(Double.toString(calibrationSet.getParameter4()));
		parameterTextFields[4].setText(Double.toString(calibrationSet.getParameter5()));
		parameterTextFields[5].setText(Double.toString(calibrationSet.getParameter6()));
	}

	private void formToCalibrationSet() {
		calibrationSet.setValidStart(new Timestamp(startDatePicker.getDate().getTime()));
		calibrationSet.setValidEnd(new Timestamp(endDatePicker.getDate().getTime()));
		
		calibrationSet.setObservedPropertyInstance((ObservedPropertyInstance)observedPropertyInstanceComboBox.getSelectedItem());
		calibrationSet.setParameter1(Double.valueOf(parameterTextFields[0].getText()));
		calibrationSet.setParameter2(Double.valueOf(parameterTextFields[1].getText()));
		calibrationSet.setParameter3(Double.valueOf(parameterTextFields[2].getText()));
		calibrationSet.setParameter4(Double.valueOf(parameterTextFields[3].getText()));
		calibrationSet.setParameter5(Double.valueOf(parameterTextFields[4].getText()));
		calibrationSet.setParameter6(Double.valueOf(parameterTextFields[5].getText()));
	}

	public boolean isDirty() {
		{
			if (calibrationSet.getValidStart() != null) {
				if (!startDatePicker.getDate().equals(new Date(calibrationSet.getValidStart().getTime()))) {
					return true;
				}
			}

			if (calibrationSet.getValidEnd() != null) {
				if (!endDatePicker.getDate().equals(new Date(calibrationSet.getValidEnd().getTime()))) {
					return true;
				}
			}

			if (calibrationSet.getObservedPropertyInstance() != null) {
				final ObservedPropertyInstance selectedObservedPropertyInstance = (ObservedPropertyInstance)observedPropertyInstanceComboBox.getSelectedItem();

				if (!calibrationSet.getObservedPropertyInstance().getId().equals(selectedObservedPropertyInstance.getId())) {
					return true;
				}
			}
		}

		{
			final Double cmpParameters[] = new Double[6];

			cmpParameters[0] = calibrationSet.getParameter1();
			cmpParameters[1] = calibrationSet.getParameter2();
			cmpParameters[2] = calibrationSet.getParameter3();
			cmpParameters[3] = calibrationSet.getParameter4();
			cmpParameters[4] = calibrationSet.getParameter5();
			cmpParameters[5] = calibrationSet.getParameter6();

			for (int i = 0; i < cmpParameters.length; i++) {
				if (cmpParameters[i] != null) {
					if (!cmpParameters[i].equals(Double.valueOf(parameterTextFields[i].getText()))) {
						return true;
					}
				} else {
					if (!parameterTextFields[i].getText().equals("")) {
						return true;
					}
				}
			}
		}

		return super.isDirty();
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();
		Transaction transaction = null;
		List<CalibrationSet> calibrationSets = null;

		try {
			formToCalibrationSet();

			if (calibrationSet.getObservedPropertyInstance() == null) {
				final String message = String.format(
					"You did not select an observed property instance.\n" +
					"Do that before saving.");
				JOptionPane.showMessageDialog(owner, message, "Unsaved class", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			transaction = session.beginTransaction();

			calibrationSets = calibrationSet.getObservedPropertyInstance().getCalibrationSets();
			calibrationSets.add(calibrationSet);

			session.update(calibrationSet.getObservedPropertyInstance());

			if (!getSaved()) {
				session.save(calibrationSet);
			} else {
				session.update(calibrationSet);
			}

			transaction.commit();	
			JOptionPane.showMessageDialog(owner, "Calibration set successfully saved.", "Calibration set saved", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			if (calibrationSets != null) {
				calibrationSets.remove(calibrationSet);
			}

			final String[] messages = { "Calibration set could not be saved", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Calibration set description not saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	static void activeError(Window owner, ObservedPropertyInstance observedPropertyInstance) {
		final String message = String.format(
				"This calibration set is active. Please edit the observed property instance '%s' and choose another active calibration set.",
				observedPropertyInstance.getName());
		JOptionPane.showMessageDialog(owner, message, "Calibration set is active", JOptionPane.ERROR_MESSAGE);
	}

	static boolean removeCalibrationSet(CalibrationSet calibrationSet, Window owner, SessionFactory sessionFactory) {
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the calibration set.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return false;
		}

		final ObservedPropertyInstance observedPropertyInstance = calibrationSet.getObservedPropertyInstance();

		if (observedPropertyInstance.getActiveCalibrationSet().getId().equals(calibrationSet.getId())) {
			activeError(owner, observedPropertyInstance);
			return false;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			session.delete(calibrationSet);

			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Calibration set could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Calibration set could not be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			return false;
		} finally {
			session.close();
		}

		return true;
	}

	void showEditFunctions() {
		if (editFunctionsShown) {
			return;
		}

		final JButton removeButton = new JButton("Remove calibration set");

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				removeCalibrationSet(calibrationSet, owner, sessionFactory);

				if (removeListener != null) {
					removeListener.onRemove();
				}
			}
		});
		horizontalButtonGroup
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(removeButton);
		verticalButtonGroup.addComponent(removeButton);
		showBottom(true);
		editFunctionsShown = true;
	}

	void setRemoveListener(RemoveListener removeListener) {
		this.removeListener = removeListener;
	}
}
