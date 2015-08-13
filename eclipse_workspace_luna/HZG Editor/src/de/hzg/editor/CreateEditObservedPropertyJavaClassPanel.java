package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.hzg.common.ObservedPropertyClassesConfiguration;
import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;

public class CreateEditObservedPropertyJavaClassPanel extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = 4736407909177124580L;
	private final JTextField classNameTextField;
	private final JTextArea classTextArea;
	private final Window owner;
	private final ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration;
	private final ObservedPropertyJavaClass observedPropertyJavaClass;
	private boolean inputSaved = false;
	private RemoveListener removeListener;
	private SequentialGroup horizontalButtonGroup;
	private ParallelGroup verticalButtonGroup;
	private boolean editFunctionsShown = false;
	private SessionFactory sessionFactory;

	public CreateEditObservedPropertyJavaClassPanel(Window owner, SessionFactory sessionFactory, ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration, ObservedPropertyJavaClass observedPropertyJavaClass) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.observedPropertyClassesConfiguration = observedPropertyClassesConfiguration;
		this.observedPropertyJavaClass = observedPropertyJavaClass;

		classNameTextField = new JTextField();
		classNameTextField.setColumns(30);

		final DataCreator dataCreator = new DataCreator();
		classTextArea = createClassTextArea(dataCreator);
		setupClassTextArea();

		setBottomPanelTitle("Class");
		getBottomPanel().add(dataCreator.createPanel(classTextArea));
		createForm();

		setDataProvider(this);
		observedPropertyJavaClassToFormAndClass();
		setClassFilter();

		inputSaved = observedPropertyJavaClass.isLoaded();
	}

	public static ObservedPropertyJavaClass createNewObservedPropertyJavaClass(ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration) {
		final ObservedPropertyJavaClass observedPropertyJavaClass = new ObservedPropertyJavaClass(observedPropertyClassesConfiguration);

		try {
			observedPropertyJavaClass.setName("Example");
		} catch (InvalidIdentifierException exception) {
			assert(false);
		}

		observedPropertyJavaClass.setText(getClassTemplate("Example"));

		return observedPropertyJavaClass;
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
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblClassName).addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(verticalButtonGroup)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTextArea createClassTextArea(DataCreator dataCreator) {
		dataCreator.addInformationMessage("For now only very basic operations are supported, see the manual.");
		dataCreator.addInformationMessage("The compiler will be invoked in: '" + observedPropertyClassesConfiguration.getSourceDirectory() + "'");

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
					ObservedPropertyJavaClass.compileTasks(owner, observedPropertyClassesConfiguration, observedPropertyJavaClass.getName());
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
		return "package de.hzg.observed_properties;\n" +
"\n" +
"import de.hzg.measurement.BaseObservedProperty;\n" +
"\n" +
"public class " + name + " extends BaseObservedProperty {\n" +
"\t@Override\n" +
"\tpublic double getCalculationValue(int binaryValue) {\n" +
"\t/* The parameters are stored in the base class.\n" +
"\t * See the manual for further details.\n" +
"\t */\n" +
"\t\treturn (((((parameters[5]\n" +
"\t\t            * binaryValue + parameters[4])\n" +
"\t\t            * binaryValue + parameters[3])\n" +
"\t\t            * binaryValue + parameters[2])\n" +
"\t\t            * binaryValue + parameters[1])\n" +
"\t\t            * binaryValue + parameters[0]);\n" +
" }\n" +
"}";
	}

	private void observedPropertyJavaClassToFormAndClass() {
		classNameTextField.setText(observedPropertyJavaClass.getName());
		classTextArea.setText(observedPropertyJavaClass.getText());
	}

	private void formToObservedPropertyJavaClass() throws InvalidIdentifierException {
		observedPropertyJavaClass.setName(classNameTextField.getText());
	}

	private void classToObservedPropertyJavaClass() {
		observedPropertyJavaClass.setText(classTextArea.getText());
	}

	private boolean dirtyName() {
		{
			final String cmpString = observedPropertyJavaClass.getName() == null ? "" : observedPropertyJavaClass.getName();

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
			final String cmpString = observedPropertyJavaClass.getText() == null ? "" : observedPropertyJavaClass.getText();

			if (!classTextArea.getText().equals(cmpString)) {
				return true;
			}
		}

		return super.isDirty();
	}

	public boolean provide(String title) {
		String oldName = null;

		if (title.equals("Save information")) {
			// first make sure the identifier is valid
			final boolean deleteOldFile = observedPropertyJavaClass.isLoaded() && (dirtyName() || !inputSaved);

			try {
				formToObservedPropertyJavaClass();
			} catch (InvalidIdentifierException exception) {
				final String[] messages = { "Observed property class has an invalid identifier.", "An exception occured." };
				final JDialog dialog = new ExceptionDialog(owner, "Observed property class has an invalid identifier", messages, exception);
				dialog.pack();
				dialog.setLocationRelativeTo(owner);
				dialog.setVisible(true);
				return false;
			}

			if (deleteOldFile) {
				oldName = observedPropertyJavaClass.getNameLoaded();
				final String message = String.format(
					"The observed property class was previously loaded from the filesystem.\n" +
					"Changing the name will result in the old file being deleted and the new one being saved.\n");

				JOptionPane.showMessageDialog(owner, message, "Observed property class will be moved", JOptionPane.WARNING_MESSAGE);
				try {
					observedPropertyJavaClass.deleteLoadedInstance();
				} catch (IOException exception) {
					final String[] messages = { "Old observed property class cannot be deleted.", "An exception occured.", "Please do this manually in the directory of observed property classes." };
					final JDialog dialog = new ExceptionDialog(owner, "Old observed property class cannot be deleted", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				}
			}

			// name was changed, update classtextarea
			updateClassTextArea();
			classToObservedPropertyJavaClass();
		} else {
			classToObservedPropertyJavaClass();
		}

		// always save
		final boolean result =  saveFile();

		if (oldName != null) {
			updateDescriptions(oldName, observedPropertyJavaClass.getName());
		}

		return result;
	}

	private void updateDescriptions(String oldName, String newName) {
		final Session session = sessionFactory.openSession();

		try {
			final int result = session
				.createQuery("UPDATE ObservedPropertyDescription SET className = :newClassName WHERE className = :oldClassName")
				.setParameter("newClassName", newName)
				.setParameter("oldClassName", oldName)
				.executeUpdate();
			JOptionPane.showMessageDialog(owner, "" + result + " observed property descriptions successfully updated for the new name.", "Observed property descriptions updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			final String[] messages = { "Observed property descriptions could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property descriptions could not be updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}
	private boolean saveFile() {
		try {
			observedPropertyJavaClass.save();
			JOptionPane.showMessageDialog(owner, "Observed property class successfully saved.", "Observed property saved", JOptionPane.INFORMATION_MESSAGE);
			inputSaved = true;
		} catch (IOException exception) {
			final String[] messages = { "Observed property class cannot be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property class cannot be saved", messages, exception);
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
			classTextArea.replaceRange(observedPropertyJavaClass.getName(), introBeginOffset, introEndOffset);
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
			final String[] messages = { "Observed property class has a wrong length.", "This will most likely cause runtime errors, plesae fix this manually.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property class has a wrong length", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		}
	}

	static boolean removeObservedPropertyJavaClass(ObservedPropertyJavaClass observedPropertyJavaClass, Window owner, SessionFactory sessionFactory) {
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the observed property Java class and all observed property descriptions and instances referring to it and the calibration sets for those instances.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return false;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		transaction = session.beginTransaction();
		try {
			@SuppressWarnings("unchecked")
			final List<ObservedPropertyDescription> tempResult = (List<ObservedPropertyDescription>)session
				.createQuery("FROM ObservedPropertyDescription WHERE classname = :classname")
				.setParameter("classname", observedPropertyJavaClass.getName())
				.list();

			for (final ObservedPropertyDescription observedPropertyDescription: tempResult) {
				observedPropertyDescription.initObservedPropertyDescription();

				for (final ObservedPropertyInstance observedPropertyInstance: observedPropertyDescription.getObservedPropertyInstances()) {
					for (final CalibrationSet calibrationSet: observedPropertyInstance.getCalibrationSets()) {
						session.delete(calibrationSet);
					}

					session.delete(observedPropertyInstance);
				}

				session.delete(observedPropertyDescription);
			}

			session.flush();
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Observed property descriptions and instances could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property descriptions and instances could not be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		try {
			observedPropertyJavaClass.deleteLoadedInstance();
		} catch (IOException exception) {
			final String[] messages = { "Observed property class cannot be deleted.", "An exception occured.", "Please do this manually in the directory of observed property classes." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property class cannot be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		}

		return true;
	}

	void showEditFunctions() {
		if (editFunctionsShown) {
			return;
		}

		final JButton removeButton = new JButton("Remove observed property class");

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				removeObservedPropertyJavaClass(observedPropertyJavaClass, owner, sessionFactory);

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