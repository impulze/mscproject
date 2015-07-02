package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.SensorDescription;

public class CreateEditSensorPanel extends SplitPanel {
	private static final long serialVersionUID = 4302743064914251776L;
	private final JTextField nameTextField;
	private final JComboBox<String> classNameComboBox;
	private final JTextField unitTextField;
	private final JTextArea metadataTextArea;
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

		classNameComboBox = new JComboBox<String>();

		unitTextField = new JTextField();
		unitTextField.setColumns(10);

		final DataCreator dataCreator = new DataCreator();
		metadataTextArea = createMetadataTextArea(dataCreator);
		setupMetadataTextArea();

		setBottomPanelTitle("Metadata");
		getBottomPanel().add(dataCreator.createPanel(metadataTextArea));
		createForm();

		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<String> result = (List<String>)session
				.createQuery("SELECT DISTINCT sd.className FROM SensorDescription sd")
				.list();

			Collections.sort(result);

			for (final String className : result) {
				classNameComboBox.addItem(className);
			}
		} catch (Exception exception) {
			final String[] messages = { "Sensor Java classnames could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor Java classnames not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		sensorDescriptionToFormAndMetadata();
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
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblClassName).addComponent(classNameComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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

	private JTextArea createMetadataTextArea(DataCreator dataCreator) {
		dataCreator.addInformationMessage("A valid SensorML 2.0 document is required here.");

		final JPanel metadataInteractions = new JPanel();
		final JButton saveButton = getActionButton("Save metadata");
		final JButton useTemplateButton = new JButton("Use template");

		useTemplateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				metadataTextArea.setText(getMetadataTemplate());
			}
		});

		metadataInteractions.setLayout(new FlowLayout(FlowLayout.LEFT));
		metadataInteractions.add(saveButton);
		metadataInteractions.add(useTemplateButton);

		dataCreator.addPanel(metadataInteractions);

		// popups for the textarea?
		return new JTextArea();
	}

	private void setupMetadataTextArea() {
	}

	protected static String getMetadataTemplate() {
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

	protected void sensorDescriptionToFormAndMetadata() {
		nameTextField.setText(sensorDescription.getName());
		classNameComboBox.setSelectedItem(sensorDescription.getClassName());
		unitTextField.setText(sensorDescription.getUnit());
		metadataTextArea.setText(sensorDescription.getMetadata());
	}

	protected void formToSensorDescription() {
		if (nameTextField.getText().length() != 0) {
			sensorDescription.setName(nameTextField.getText());
		}

		final String selectedClassName = (String)classNameComboBox.getSelectedItem();

		if (selectedClassName != null && selectedClassName.length() != 0) {
			sensorDescription.setClassName(selectedClassName);
		}

		if (unitTextField.getText().length() != 0) {
			sensorDescription.setUnit(unitTextField.getText());
		}

		dirty = true;
	}

	protected void metadataToSensorDescription() {
		if (metadataTextArea.getText().length() != 0) {
			sensorDescription.setMetadata(metadataTextArea.getText());
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

			if (classNameComboBox.getSelectedItem() != null && classNameComboBox.getSelectedItem().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = sensorDescription.getUnit() == null ? "" : sensorDescription.getUnit();

			if (!unitTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = sensorDescription.getMetadata() == null ? "" : sensorDescription.getMetadata();

			if (!metadataTextArea.getText().equals(cmpString)) {
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