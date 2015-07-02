package de.hzg.editor;

import java.awt.Window;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.hibernate.SessionFactory;

import de.hzg.sensors.SensorDescription;

public class CreateEditSensorPanel extends SplitPanel {
	private static final long serialVersionUID = 4302743064914251776L;
	private final JTextField nameTextField;
	private final JTextField classNameTextField;
	private final JTextField unitTextField;
	private final JTextArea textArea;
	private boolean dirty = false;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private SensorDescription sensorDescription;

	public CreateEditSensorPanel(Window owner, SessionFactory sessionFactory, SensorDescription sensorDescription) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.sensorDescription = sensorDescription;

		nameTextField = new JTextField();
		nameTextField.setColumns(20);

		classNameTextField = new JTextField();
		classNameTextField.setColumns(30);

		unitTextField = new JTextField();
		unitTextField.setColumns(10);

		final DataCreator dataCreator = new DataCreator();
		textArea = createTextArea(dataCreator);
		setupTextArea();

		setBottomPanelTitle("Metadata");
		getBottomPanel().add(dataCreator.createPanel(textArea));
		createForm();

		sensorDescriptionToFormAndMetadata();
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Save information");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final JLabel lblName = new JLabel("Name:");
		lblName.setLabelFor(nameTextField);

		final JLabel lblClassName = new JLabel("Name of Java Class:");
		lblName.setLabelFor(classNameTextField);

		final JLabel lblUnit = new JLabel("Unit:");
		lblName.setLabelFor(unitTextField);

		final ParallelGroup labelsLayout = topPanelLayout.createParallelGroup(Alignment.TRAILING)
			.addComponent(lblName)
			.addComponent(lblClassName)
			.addComponent(lblUnit);
		final ParallelGroup inputsLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(unitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
		final SequentialGroup labelsWithInputsLayout = topPanelLayout.createSequentialGroup()
			.addGroup(labelsLayout)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(inputsLayout);
		final ParallelGroup labelsWithInputsAndButtonLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(labelsWithInputsLayout)
			.addComponent(actionButton);
		final SequentialGroup horizontalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(labelsWithInputsAndButtonLayout)
			.addContainerGap(251, Short.MAX_VALUE);

		final SequentialGroup verticalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblName).addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblClassName).addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblUnit).addComponent(unitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(actionButton)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTextArea createTextArea(DataCreator dataCreator) {
		dataCreator.addInformationMessage("Do this.");
		dataCreator.addInformationMessage("Do that.");
		dataCreator.addInformationMessage("Do everything.");

		// popups for the textarea?
		return new JTextArea();
	}

	private void setupTextArea() {
	}

	private void sensorDescriptionToFormAndMetadata() {
		nameTextField.setText(sensorDescription.getName());
		classNameTextField.setText(sensorDescription.getClassName());
		unitTextField.setText(sensorDescription.getUnit());

		textArea.setText("Some metadata to edit here.");
	}

	protected void formAndMetadataToSensorDescription() {
		if (nameTextField.getText().length() != 0) {
			sensorDescription.setName(nameTextField.getText());
		}

		if (classNameTextField.getText().length() != 0) {
			sensorDescription.setName(classNameTextField.getText());
		}

		if (unitTextField.getText().length() != 0) {
			sensorDescription.setUnit(unitTextField.getText());
		}

		dirty = true;
	}

	protected boolean isDirty() {
		{
			final String cmpString = sensorDescription.getName() == null ? "" : sensorDescription.getName();

			if (!nameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = sensorDescription.getClassName() == null ? "" : sensorDescription.getClassName();

			if (!classNameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = sensorDescription.getUnit() == null ? "" : sensorDescription.getUnit();

			if (!unitTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		return dirty;
	}

	protected Window getOwner() {
		return owner;
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected SensorDescription getSensorDescription() {
		return sensorDescription;
	}

	protected void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}