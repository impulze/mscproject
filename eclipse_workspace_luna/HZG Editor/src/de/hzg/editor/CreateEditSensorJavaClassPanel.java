package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private boolean inputSaved = false;

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

		inputSaved = sensorJavaClass.isLoaded();
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
		final JTextArea newTextArea = new JTextArea();

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
				((AbstractDocument)classTextArea.getDocument()).setDocumentFilter(null);
				newTextArea.setText(getClassTemplate(classNameTextField.getText()));
				setClassFilter();
			}
		});

		classInteractions.setLayout(new FlowLayout(FlowLayout.LEFT));
		classInteractions.add(saveButton);
		classInteractions.add(compileButton);
		classInteractions.add(useTemplateButton);

		dataCreator.addPanel(classInteractions);

		return newTextArea;
	}

	private void setupClassTextArea() {
	}

	private static String getClassTemplate(String name) {
		return "package de.hzg.sensors;\n" +
"\n" +
"import de.hzg.measurement.BaseSensor;\n" +
"\n" +
"public class " + name + " extends BaseSensor {\n" +
"\t@Override\n" +
"\tpublic double calibrate(double rawValue) {\n" +
"\t/* The parameters are stored in the base class.\n" +
"\t * See the manual for further details.\n" +
"\t */\n" +
"\t\treturn (((((parameters[5]\n" +
"\t\t            * rawValue + parameters[4])\n" +
"\t\t            * rawValue + parameters[3])\n" +
"\t\t            * rawValue + parameters[2])\n" +
"\t\t            * rawValue + parameters[1])\n" +
"\t\t            * rawValue + parameters[0]);\n" +
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

	private boolean dirtyName() {
		{
			final String cmpString = sensorJavaClass.getName() == null ? "" : sensorJavaClass.getName();

			if (!classNameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		return false;
	}

	public boolean isDirty() {
		if (dirtyName()) {
			return true;
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

			final boolean deleteOldFile = sensorJavaClass.isLoaded() && (dirtyName() || !inputSaved);

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

			if (deleteOldFile) {
				final String message = String.format(
					"The sensor class was previously loaded from the filesystem.\n" +
					"Changing the name will result in the old file being deleted and the new one being saved.\n");

				JOptionPane.showMessageDialog(owner, message, "Sensor class will be moved", JOptionPane.WARNING_MESSAGE);
				try {
					sensorJavaClass.deleteLoadedInstance();
				} catch (IOException exception) {
					final String[] messages = { "Old sensor class cannot be deleted.", "An exception occured.", "Please do this manually in the directory of sensor classes." };
					final JDialog dialog = new ExceptionDialog(owner, "Old sensor class cannot be deleted", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				}

				// when the old file is deleted (name changed) update classtextarea
				{
					updateClassTextArea();
					classToSensorJavaClass();
				}
			}
		} else {
			classToSensorJavaClass();
		}

		// always save
		return saveFile();
	}

	private boolean saveFile() {
		try {
			sensorJavaClass.save();
			JOptionPane.showMessageDialog(owner, "Sensor class successfully saved.", "Sensor saved", JOptionPane.INFORMATION_MESSAGE);
			inputSaved = true;
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

		// TODO: hack, always set class name
		final Pattern introPattern = Pattern.compile("public class (.+?) ");
		final Matcher matcher = introPattern.matcher(classTextArea.getText());

		if (matcher.find()) {
			final int introBeginOffset = matcher.start(1);
			final int introEndOffset = matcher.end(1);
			classTextArea.replaceRange(sensorJavaClass.getName(), introBeginOffset, introEndOffset);
		}

		setClassFilter();
	}

	private void setClassFilter() {
		try {
			// TODO: seems hackish, always check template to see if this are the right numbers
			// TODO: allow writing imports
			final int editBeginOffset = classTextArea.getLineStartOffset(5) - 1;
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