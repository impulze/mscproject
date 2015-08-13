package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.hzg.common.ProcedureClassesConfiguration;
import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.ProcedureInstance;

public class CreateEditProcedurePanel extends SplitPanel implements DataProvider  {
	private static final long serialVersionUID = 4302743064914251776L;
	private final JTextField nameTextField;
	private final JComboBox<String> classNameComboBox;
	private final JTextField unitTextField;
	private final JTextArea metadataTextArea;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private ProcedureDescription procedureDescription;
	private RemoveListener removeListener;
	private SequentialGroup horizontalButtonGroup;
	private ParallelGroup verticalButtonGroup;
	private boolean editFunctionsShown = false;

	public CreateEditProcedurePanel(Window owner, SessionFactory sessionFactory, ProcedureClassesConfiguration procedureClassesConfiguration, ProcedureDescription procedureDescription) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.procedureDescription = procedureDescription;

		nameTextField = new JTextField();
		nameTextField.setColumns(20);

		classNameComboBox = new ProcedureJavaClassComboBox(owner, procedureClassesConfiguration);

		unitTextField = new JTextField();
		unitTextField.setColumns(10);

		final DataCreator dataCreator = new DataCreator();
		metadataTextArea = createMetadataTextArea(dataCreator);
		setupMetadataTextArea();

		setBottomPanelTitle("Metadata");
		getBottomPanel().add(dataCreator.createPanel(metadataTextArea));
		createForm();

		setDataProvider(this);
		procedureDescriptionToFormAndMetadata();
	}

	public static ProcedureDescription createNewProcedureDescription() {
		final ProcedureDescription procedureDescription = new ProcedureDescription();

		procedureDescription.setMetadata(getMetadataTemplate());

		return procedureDescription;
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Save information");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final JLabel lblName = new JLabel("Name:");
		lblName.setLabelFor(nameTextField);

		final JLabel lblClassName = new JLabel("Name of Java Class:");
		lblClassName.setLabelFor(classNameComboBox);

		final JLabel lblUnit = new JLabel("Unit:");
		lblUnit.setLabelFor(unitTextField);

		final ParallelGroup labelsLayout = topPanelLayout.createParallelGroup(Alignment.TRAILING)
			.addComponent(lblName)
			.addComponent(lblClassName)
			.addComponent(lblUnit);
		final ParallelGroup inputsLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(classNameComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(unitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
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
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblName).addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblClassName).addComponent(classNameComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblUnit).addComponent(unitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(verticalButtonGroup)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTextArea createMetadataTextArea(DataCreator dataCreator) {
		final JTextArea newTextArea = new JTextArea();

		dataCreator.addInformationMessage("A valid SensorML 2.0 document is required here.");

		final JPanel metadataInteractions = new JPanel();
		final JButton saveButton = getActionButton("Save metadata");
		final JButton useTemplateButton = new JButton("Use template");

		useTemplateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newTextArea.setText(getMetadataTemplate());
			}
		});

		metadataInteractions.setLayout(new FlowLayout(FlowLayout.LEFT));
		metadataInteractions.add(saveButton);
		metadataInteractions.add(useTemplateButton);

		dataCreator.addPanel(metadataInteractions);

		// popups for the textarea?
		return newTextArea;
	}

	private void setupMetadataTextArea() {
	}

	private static String getMetadataTemplate() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<sml:PhysicalComponent gml:id=\"MY_SENSOR\" xmlns:sml=\"http://www.opengis.net/sensorml/2.0\"\n" +
" xmlns:swe=\"http://www.opengis.net/swe/2.0\"\n" +
" xmlns:gml=\"http://www.opengis.net/gml/3.2\"\n" +
" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\"\n" +
" xmlns:gco=\"http://www.isotc211.org/2005/gco\"\n" +
" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
" xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
" xsi:schemaLocation=\"http://www.opengis.net/sensorml/2.0 ./sensorML/2.0.0/sensorML.xsd\">\n" +
"   <!-- ================================================= -->\n" +
"   <!--                  System Description               -->\n" +
"   <!-- ================================================= -->\n" +
"   <gml:description> Temperature sensor on my window </gml:description>\n" +
"   <gml:identifier codeSpace=\"uid\">myCompany.com.63547</gml:identifier>\n" +
"    <!-- ================================================= -->\n" +
"   <!--             Observed Property = Output            -->\n" +
"   <!-- ================================================= -->\n" +
"   <sml:outputs>\n" +
"      <sml:OutputList>\n" +
"         <sml:output name=\"temp\">\n" +
"            <swe:Quantity definition=\"http://sweet.jpl.nasa.gov/2.2/quanTemperature.owl#Temperature\">\n" +
"               <swe:label>Air Temperature</swe:label>\n" +
"               <swe:uom code=\"Cel\"/>\n" +
"            </swe:Quantity>\n" +
"         </sml:output>\n" +
"      </sml:OutputList>\n" +
"   </sml:outputs>\n" +
"   <!-- ================================================= -->\n" +
"   <!--                  Sensor Location                  -->\n" +
"   <!-- ================================================= -->\n" +
"   <sml:position>\n" +
"      <gml:Point gml:id=\"stationLocation\" srsName=\"http://www.opengis.net/def/crs/EPSG/0/4326\">\n" +
"         <gml:coordinates>47.8 88.56</gml:coordinates>\n" +
"      </gml:Point>\n" +
"   </sml:position>\n" +
"</sml:PhysicalComponent>";
	}

	private void procedureDescriptionToFormAndMetadata() {
		nameTextField.setText(procedureDescription.getName());
		classNameComboBox.setSelectedItem(procedureDescription.getClassName());
		unitTextField.setText(procedureDescription.getUnit());
		metadataTextArea.setText(procedureDescription.getMetadata());
	}

	private void formToSensorDescription() {
		if (nameTextField.getText().length() == 0) {
			procedureDescription.setName(null);
		} else {
			procedureDescription.setName(nameTextField.getText());
		}

		final String selectedClassName = (String)classNameComboBox.getSelectedItem();

		if (selectedClassName == null) {
			procedureDescription.setClassName(null);
		} else {
			assert(selectedClassName.length() != 0);
			procedureDescription.setClassName(selectedClassName);
		}

		if (unitTextField.getText().length() == 0) {
			procedureDescription.setUnit(null);
		} else {
			procedureDescription.setUnit(unitTextField.getText());
		}
	}

	private void metadataToSensorDescription() {
		if (metadataTextArea.getText().length() == 0) {
			procedureDescription.setMetadata(null);
		} else {
			procedureDescription.setMetadata(metadataTextArea.getText());
		}
	}

	public boolean isDirty() {
		{
			final String cmpString = procedureDescription.getName() == null ? "" : procedureDescription.getName();

			if (!nameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = procedureDescription.getClassName() == null ? "" : procedureDescription.getClassName();

			if (classNameComboBox.getSelectedItem() != null && !classNameComboBox.getSelectedItem().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = procedureDescription.getUnit() == null ? "" : procedureDescription.getUnit();

			if (!unitTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = procedureDescription.getMetadata() == null ? "" : procedureDescription.getMetadata();

			if (!metadataTextArea.getText().equals(cmpString)) {
				return true;
			}
		}

		return super.isDirty();
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			if (title.equals("Save information")) {
				formToSensorDescription();
			} else {
				metadataToSensorDescription();
			}

			if (!getSaved()) {
				session.save(procedureDescription);
				session.flush();
			} else {
				session.update(procedureDescription);
				session.flush();
			}

			JOptionPane.showMessageDialog(owner, "Procedure successfully saved.", "Procedure saved", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Procedure could not be saved", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure not saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	static boolean removeProcedureDescription(ProcedureDescription procedureDescription, Window owner, SessionFactory sessionFactory) {
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the procedure and all procedure instances for this procedure.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return false;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			for (final ProcedureInstance procedureInstance: procedureDescription.getProcedureInstances()) {
				session.delete(procedureInstance);
			}
			session.delete(procedureDescription);
			session.flush();
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Procedure could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure could not be deleted", messages, exception);
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

		final JButton removeButton = new JButton("Remove procedure");

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				removeProcedureDescription(procedureDescription, owner, sessionFactory);

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