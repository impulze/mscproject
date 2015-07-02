package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import de.hzg.common.SensorClassesConfiguration;

public class CreateEditSensorJavaClassPanel extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = 4736407909177124580L;
	private final JTextField classNameTextField;
	private final JTextArea classTextArea;
	private final Window owner;
	private final SensorClassesConfiguration sensorClassesConfiguration;
	private final SensorJavaClass sensorJavaClass;

	public CreateEditSensorJavaClassPanel(Window owner, SensorClassesConfiguration sensorClassesConfiguration, SensorJavaClass sensorJavaClass) {
		this.owner = owner;
		this.sensorClassesConfiguration = sensorClassesConfiguration;
		this.sensorJavaClass = sensorJavaClass;

		classNameTextField = new JTextField();
		classNameTextField.setColumns(30);

		final DataCreator dataCreator = new DataCreator();
		classTextArea = createClassTextArea(dataCreator);
		setupClassTextArea();

		setBottomPanelTitle("Class");
		getBottomPanel().add(dataCreator.createPanel(classTextArea));
		createForm();

		setDataProvider(this);
		sensorJavaClassToFormAndClass();
		setClassFilter();
	}

	public static SensorJavaClass createNewSensorJavaClass(SensorClassesConfiguration sensorClassesConfiguration) {
		final SensorJavaClass sensorJavaClass = new SensorJavaClass(sensorClassesConfiguration);

		try {
			sensorJavaClass.setName("Example");
		} catch (InvalidIdentifierException exception) {
			assert(false);
		}

		sensorJavaClass.setText(getClassTemplate("Example"));

		return sensorJavaClass;
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Save information");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final JLabel lblClassName = new JLabel("Name of Java Class:");
		lblClassName.setLabelFor(classNameTextField);

		final ParallelGroup labelsLayout = topPanelLayout.createParallelGroup(Alignment.TRAILING)
			.addComponent(lblClassName);
		final ParallelGroup inputsLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
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
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblClassName).addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(actionButton)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTextArea createClassTextArea(DataCreator dataCreator) {
		dataCreator.addInformationMessage("For now only very basic operations are supported, see the manual.");
		dataCreator.addInformationMessage("The compiler will be invoked in: '" + sensorClassesConfiguration.getSourceDirectory() + "'");
		dataCreator.addInformationMessage("Compiler and arguments: '" + SensorJavaClass.getCompilerInvocation() + "'");

		final JPanel classInteractions = new JPanel();
		final JButton saveButton = getActionButton("Save class");
		final JButton compileButton = new JButton("Compile");
		final JButton useTemplateButton = new JButton("Use template");

		compileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isDirty()) {
					final String message = String.format(
							"The class it not yet saved.\n" +
							"Do that before clicking compile.");
					JOptionPane.showMessageDialog(owner, message, "Unsaved class", JOptionPane.ERROR_MESSAGE);

				} else {
					SensorJavaClass.compileTasks(owner, sensorClassesConfiguration, sensorJavaClass.getName(), "P");
				}
			}
		});
		useTemplateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				classTextArea.setText(getClassTemplate(classNameTextField.getText()));
			}
		});

		classInteractions.setLayout(new FlowLayout(FlowLayout.LEFT));
		classInteractions.add(saveButton);
		classInteractions.add(compileButton);
		classInteractions.add(useTemplateButton);

		dataCreator.addPanel(classInteractions);

		// popups for the textarea?
		return new JTextArea();
	}

	private void setupClassTextArea() {
	}

	private static String getClassTemplate(String name) {
		return "package de.hzg.sensors;\n" +
"\n" +
"import de.hzg.measurement.BaseSensor;\n" +
"\n" +
"public class " + name + " extends BaseSensor {\n" +
" @Override\n" +
" public double calibrate(double rawValue) {\n" +
" /* The parameters are stored in the base class.\n" +
"  * See the manual for further details.\n" +
"  */\n" +
"  return (((((parameters[5]\n" +
"              * rawValue + parameters[4])\n" +
"              * rawValue + parameters[3])\n" +
"              * rawValue + parameters[2])\n" +
"              * rawValue + parameters[1])\n" +
"              * rawValue + parameters[0]);\n" +
" }\n" +
"}";
	}

	private void sensorJavaClassToFormAndClass() {
		classNameTextField.setText(sensorJavaClass.getName());
		classTextArea.setText(sensorJavaClass.getText());
	}

	private void formToSensorJavaClass() throws InvalidIdentifierException {
		sensorJavaClass.setName(classNameTextField.getText());
	}

	private void classToSensorJavaClass() {
		sensorJavaClass.setText(classTextArea.getText());
	}

	public boolean isDirty() {
		{
			final String cmpString = sensorJavaClass.getName() == null ? "" : sensorJavaClass.getName();

			if (!classNameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = sensorJavaClass.getText() == null ? "" : sensorJavaClass.getText();

			if (!classTextArea.getText().equals(cmpString)) {
				return true;
			}
		}

		return super.isDirty();
	}

	public boolean provide(String title) {
		if (title.equals("Save information")) {
			// first make sure the identifier is valid
			try {
				formToSensorJavaClass();
			} catch (InvalidIdentifierException exception) {
				final String[] messages = { "Sensor class has an invalid identifier.", "An exception occured." };
				final JDialog dialog = new ExceptionDialog(owner, "Sensor class has an invalid identifier", messages, exception);
				dialog.pack();
				dialog.setLocationRelativeTo(owner);
				dialog.setVisible(true);
				return false;
			}
		} else {
			classToSensorJavaClass();
		}

		// always save
		return saveFile();
	}

	private boolean saveFile() {
		// now, if it was loaded from the filesystem or created and already saved, we need to remove the old file
		assert(getSaved() == sensorJavaClass.isLoaded());

		try {
			if  (sensorJavaClass.isLoaded()) {
				final String message = String.format(
					"The sensor class was previously loaded from the filesystem.\n" +
					"Changing the name will result in the old file being deleted and the new one being saved.\n");

				JOptionPane.showMessageDialog(owner, message, "Sensor class may be moved", JOptionPane.WARNING_MESSAGE);
				sensorJavaClass.deleteLoadedInstance();
			}

			// sadly when updating the classname the textarea has to be updated too
			{
				updateClassTextArea();
				classToSensorJavaClass();
			}

			sensorJavaClass.save();
			JOptionPane.showMessageDialog(owner, "Sensor class successfully saved.", "Sensor saved", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException exception) {
			final String[] messages = { "Sensor class cannot be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor class cannot be saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			return false;
		}

		return true;
	}

	private void updateClassTextArea() {
		((AbstractDocument)classTextArea.getDocument()).setDocumentFilter(null);

		try {
			final int introBeginOffset = classTextArea.getLineStartOffset(4);
			final int introEndOffset = classTextArea.getLineEndOffset(4);
			final String classIntro = String.format("public class %s extends BaseSensor {\n", sensorJavaClass.getName());
			classTextArea.replaceRange(classIntro, introBeginOffset, introEndOffset);
		} catch (BadLocationException exception) {
			final String[] messages = { "Sensor class has a wrong class intro.", "This will most likely cause runtime errors, plesae fix this manually.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor class has a wrong class intro", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		}

		setClassFilter();
	}

	private void setClassFilter() {
		try {
			// TODO: seems hackish, always check template to see if this are the right numbers
			final int editBeginOffset = classTextArea.getLineStartOffset(5);
			final int editEndOffset = classTextArea.getText().length() - 2;
			final DocumentFilter lineFilter = new ClassFilter(classTextArea, editBeginOffset, editEndOffset);
			((AbstractDocument)classTextArea.getDocument()).setDocumentFilter(lineFilter);
		} catch (BadLocationException exception) {
			final String[] messages = { "Sensor class has a wrong length.", "This will most likely cause runtime errors, plesae fix this manually.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor class has a wrong length", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		}
	}
}