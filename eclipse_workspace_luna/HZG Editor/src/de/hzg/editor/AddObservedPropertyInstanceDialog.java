package de.hzg.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;
import de.hzg.measurement.Sensor;

public class AddObservedPropertyInstanceDialog extends EditDialog<ObservedPropertyInstance> {
	private static final long serialVersionUID = -8473878538864023713L;
	private ObservedPropertyInstance observedPropertyInstance;

	private CalibrationSet createCalibrationSet(Double parameter) {
		final CalibrationSet calibrationSet = new CalibrationSet();
		final Calendar nowCalendar = Calendar.getInstance();
		final Calendar yearsLaterCalendar = (Calendar)nowCalendar.clone();

		yearsLaterCalendar.add(Calendar.YEAR, 50);

		calibrationSet.setParameter1(parameter);
		calibrationSet.setParameter2(parameter);
		calibrationSet.setParameter3(parameter);
		calibrationSet.setParameter4(parameter);
		calibrationSet.setParameter5(parameter);
		calibrationSet.setParameter6(parameter);
		calibrationSet.setObservedPropertyInstance(observedPropertyInstance);
		calibrationSet.setValidStart(new Timestamp(nowCalendar.getTimeInMillis()));
		calibrationSet.setValidEnd(new Timestamp(yearsLaterCalendar.getTimeInMillis()));

		return calibrationSet;
	}

	public AddObservedPropertyInstanceDialog(Window owner, SessionFactory sessionFactory, Sensor sensor) {
		super(owner, "Add observed property instance");

		final GridBagLayout gridBagLayout = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();

		getContentPanel().setLayout(gridBagLayout);

		gbc.fill = GridBagConstraints.BOTH;

		final JLabel addressLabel = new JLabel("Address:");
		final JTextField addressTextField = new JTextField("0");
		addressLabel.setLabelFor(addressTextField);
		addressTextField.setColumns(5);
		((AbstractDocument)addressTextField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(owner));

		gbc.gridy = 0;
		gbc.gridx = 0;
		gridBagLayout.setConstraints(addressLabel, gbc);
		getContentPanel().add(addressLabel);
		gbc.gridx = 1;
		gridBagLayout.setConstraints(addressTextField, gbc);
		getContentPanel().add(addressTextField);

		final JLabel observedPropertyDescriptionLabel = new JLabel("Observed property description:");
		final ObservedPropertyDescriptionComboBox observedPropertyDescriptionComboBox = new ObservedPropertyDescriptionComboBox(owner, sessionFactory);
		observedPropertyDescriptionLabel.setLabelFor(observedPropertyDescriptionComboBox);

		gbc.gridy = 1;
		gbc.gridx = 0;
		gridBagLayout.setConstraints(observedPropertyDescriptionLabel, gbc);
		getContentPanel().add(observedPropertyDescriptionLabel);
		gbc.gridx = 1;
		gridBagLayout.setConstraints(observedPropertyDescriptionComboBox, gbc);
		getContentPanel().add(observedPropertyDescriptionComboBox);

		final JLabel nameLabel = new JLabel("Name:");
		final JTextField nameTextField = new JTextField();
		nameLabel.setLabelFor(nameTextField);
		nameTextField.setColumns(30);

		gbc.gridy = 2;
		gbc.gridx = 0;
		gridBagLayout.setConstraints(nameLabel, gbc);
		getContentPanel().add(nameLabel);
		gbc.gridx = 1;
		gridBagLayout.setConstraints(nameTextField, gbc);
		getContentPanel().add(nameTextField);

		final JLabel observedPropertyIsRawLabel = new JLabel("Raw:");
		final JCheckBox observedPropertyIsRawCheckBox = new JCheckBox();
		observedPropertyIsRawLabel.setLabelFor(observedPropertyIsRawCheckBox);

		gbc.gridy = 3;
		gbc.gridx = 0;
		gridBagLayout.setConstraints(observedPropertyIsRawLabel, gbc);
		getContentPanel().add(observedPropertyIsRawLabel);
		gbc.gridx = 1;
		gridBagLayout.setConstraints(observedPropertyIsRawCheckBox, gbc);
		getContentPanel().add(observedPropertyIsRawCheckBox);

		final JLabel observedPropertyUseIntervalLabel = new JLabel("Use Interval:");
		final JCheckBox observedPropertyUseIntervalCheckBox = new JCheckBox();
		observedPropertyUseIntervalLabel.setLabelFor(observedPropertyUseIntervalCheckBox);

		gbc.gridy = 4;
		gbc.gridx = 0;
		gridBagLayout.setConstraints(observedPropertyUseIntervalLabel, gbc);
		getContentPanel().add(observedPropertyUseIntervalLabel);
		gbc.gridx = 1;
		gridBagLayout.setConstraints(observedPropertyUseIntervalCheckBox, gbc);
		getContentPanel().add(observedPropertyUseIntervalCheckBox);

		final Window usedOwner = owner;
		final SessionFactory usedSessionFactory = sessionFactory;
		final Sensor usedSensor = sensor; 

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final ObservedPropertyDescription observedPropertyDescription = (ObservedPropertyDescription)observedPropertyDescriptionComboBox.getSelectedItem();

				if (observedPropertyDescription == null) {
					return;
				}

				observedPropertyInstance = new ObservedPropertyInstance();

				final CalibrationSet calibrationSet;

				if (observedPropertyIsRawCheckBox.isSelected()) {
					calibrationSet = createCalibrationSet(1.0);
				} else {
					calibrationSet = createCalibrationSet(0.0);
				}

				final Session session = usedSessionFactory.openSession();
				final List<ObservedPropertyInstance> sensorObservedPropertyInstances = usedSensor.getObservedPropertyInstances();
				final List<ObservedPropertyInstance> observedPropertyDescriptionObservedPropertyInstances = observedPropertyDescription.getObservedPropertyInstances();
				final List<CalibrationSet> calibrationSets = new ArrayList<CalibrationSet>();

				calibrationSets.add(calibrationSet);

				observedPropertyInstance.setAddress(Integer.parseInt(addressTextField.getText()));
				observedPropertyInstance.setSensor(usedSensor);
				observedPropertyInstance.setObservedPropertyDescription(observedPropertyDescription);
				observedPropertyInstance.setName(nameTextField.getText());
				observedPropertyInstance.setActiveCalibrationSet(calibrationSet);
				observedPropertyInstance.setCalibrationSets(calibrationSets);
				observedPropertyInstance.setIsRaw(observedPropertyIsRawCheckBox.isSelected());
				observedPropertyInstance.setUseInterval(observedPropertyUseIntervalCheckBox.isSelected());

				sensorObservedPropertyInstances.add(observedPropertyInstance);
				observedPropertyDescriptionObservedPropertyInstances.add(observedPropertyInstance);

				Transaction transaction = null;

				try {
					transaction = session.beginTransaction();

					session.update(observedPropertyDescription);
					session.update(usedSensor);
					session.persist(observedPropertyInstance);
					session.persist(calibrationSet);

					transaction.commit();
					setVisible(false);
					dispose();
				} catch (Exception exception) {
					if (transaction != null) {
						transaction.rollback();
					}

					sensorObservedPropertyInstances.remove(observedPropertyInstance);
					observedPropertyDescriptionObservedPropertyInstances.remove(observedPropertyInstance);

					final String[] messages = { "Observed property instance could not be created.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(usedOwner, "Observed property instance could not be created", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(usedOwner);
					dialog.setVisible(true);
					return;
				} finally {
					session.close();
				}
			}
		});
	}

	protected ObservedPropertyInstance getResult() {
		return observedPropertyInstance;
	}
}