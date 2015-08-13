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

import de.hzg.common.ProcedureClassesConfiguration;
import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.ProcedureInstance;

public class CreateEditProcedureJavaClassPanel extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = 4736407909177124580L;
	private final JTextField classNameTextField;
	private final JTextArea classTextArea;
	private final Window owner;
	private final ProcedureClassesConfiguration procedureClassesConfiguration;
	private final ProcedureJavaClass procedureJavaClass;
	private boolean inputSaved = false;
	private RemoveListener removeListener;
	private SequentialGroup horizontalButtonGroup;
	private ParallelGroup verticalButtonGroup;
	private boolean editFunctionsShown = false;
	private SessionFactory sessionFactory;

	public CreateEditProcedureJavaClassPanel(Window owner, SessionFactory sessionFactory, ProcedureClassesConfiguration procedureClassesConfiguration, ProcedureJavaClass procedureJavaClass) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.procedureClassesConfiguration = procedureClassesConfiguration;
		this.procedureJavaClass = procedureJavaClass;

		classNameTextField = new JTextField();
		classNameTextField.setColumns(30);

		final DataCreator dataCreator = new DataCreator();
		classTextArea = createClassTextArea(dataCreator);
		setupClassTextArea();

		setBottomPanelTitle("Class");
		getBottomPanel().add(dataCreator.createPanel(classTextArea));
		createForm();

		setDataProvider(this);
		procedureJavaClassToFormAndClass();
		setClassFilter();

		inputSaved = procedureJavaClass.isLoaded();
	}

	public static ProcedureJavaClass createNewProcedureJavaClass(ProcedureClassesConfiguration procedureClassesConfiguration) {
		final ProcedureJavaClass procedureJavaClass = new ProcedureJavaClass(procedureClassesConfiguration);

		try {
			procedureJavaClass.setName("Example");
		} catch (InvalidIdentifierException exception) {
			assert(false);
		}

		procedureJavaClass.setText(getClassTemplate("Example"));

		return procedureJavaClass;
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
		dataCreator.addInformationMessage("The compiler will be invoked in: '" + procedureClassesConfiguration.getSourceDirectory() + "'");
		dataCreator.addInformationMessage("Compiler and arguments: '" + ProcedureJavaClass.getCompilerInvocation() + "'");

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
					ProcedureJavaClass.compileTasks(owner, procedureClassesConfiguration, procedureJavaClass.getName());
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
		return "package de.hzg.procedures;\n" +
"\n" +
"import de.hzg.measurement.BaseProcedure;\n" +
"\n" +
"public class " + name + " extends BaseProcedure {\n" +
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

	private void procedureJavaClassToFormAndClass() {
		classNameTextField.setText(procedureJavaClass.getName());
		classTextArea.setText(procedureJavaClass.getText());
	}

	private void formToProcedureJavaClass() throws InvalidIdentifierException {
		procedureJavaClass.setName(classNameTextField.getText());
	}

	private void classToProcedureJavaClass() {
		procedureJavaClass.setText(classTextArea.getText());
	}

	private boolean dirtyName() {
		{
			final String cmpString = procedureJavaClass.getName() == null ? "" : procedureJavaClass.getName();

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
			final String cmpString = procedureJavaClass.getText() == null ? "" : procedureJavaClass.getText();

			if (!classTextArea.getText().equals(cmpString)) {
				return true;
			}
		}

		return super.isDirty();
	}

	public boolean provide(String title) {
		if (title.equals("Save information")) {
			// first make sure the identifier is valid

			final boolean deleteOldFile = procedureJavaClass.isLoaded() && (dirtyName() || !inputSaved);

			try {
				formToProcedureJavaClass();
			} catch (InvalidIdentifierException exception) {
				final String[] messages = { "Procedure class has an invalid identifier.", "An exception occured." };
				final JDialog dialog = new ExceptionDialog(owner, "Procedure class has an invalid identifier", messages, exception);
				dialog.pack();
				dialog.setLocationRelativeTo(owner);
				dialog.setVisible(true);
				return false;
			}

			if (deleteOldFile) {
				final String message = String.format(
					"The procedure class was previously loaded from the filesystem.\n" +
					"Changing the name will result in the old file being deleted and the new one being saved.\n");

				JOptionPane.showMessageDialog(owner, message, "Procedure class will be moved", JOptionPane.WARNING_MESSAGE);
				try {
					procedureJavaClass.deleteLoadedInstance();
				} catch (IOException exception) {
					final String[] messages = { "Old procedure class cannot be deleted.", "An exception occured.", "Please do this manually in the directory of procedure classes." };
					final JDialog dialog = new ExceptionDialog(owner, "Old procedure class cannot be deleted", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				}
			}

			// name was changed, update classtextarea
			updateClassTextArea();
			classToProcedureJavaClass();
		} else {
			classToProcedureJavaClass();
		}

		// always save
		return saveFile();
	}

	private boolean saveFile() {
		try {
			procedureJavaClass.save();
			JOptionPane.showMessageDialog(owner, "Procedure class successfully saved.", "Procedure saved", JOptionPane.INFORMATION_MESSAGE);
			inputSaved = true;
		} catch (IOException exception) {
			final String[] messages = { "Procedure class cannot be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure class cannot be saved", messages, exception);
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
			classTextArea.replaceRange(procedureJavaClass.getName(), introBeginOffset, introEndOffset);
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
			final String[] messages = { "Procedure class has a wrong length.", "This will most likely cause runtime errors, plesae fix this manually.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure class has a wrong length", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		}
	}

	static boolean removeProcedureJavaClass(ProcedureJavaClass procedureJavaClass, Window owner, SessionFactory sessionFactory) {
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the procedure Java class and all procedure descriptions and instances referring to it.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return false;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		transaction = session.beginTransaction();
		try {
			@SuppressWarnings("unchecked")
			final List<ProcedureDescription> tempResult = (List<ProcedureDescription>)session
				.createQuery("FROM ProcedureDescription WHERE classname = :classname")
				.setParameter("classname", procedureJavaClass.getName())
				.list();

			for (final ProcedureDescription procedureDescription: tempResult) {
				procedureDescription.initProcedureDescription();

				for (final ProcedureInstance procedureInstance: procedureDescription.getProcedureInstances()) {
					session.delete(procedureInstance);
				}

				session.delete(procedureDescription);
			}

			session.flush();
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Procedure descriptions and instances could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure descriptions and instances could not be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		try {
			procedureJavaClass.deleteLoadedInstance();
		} catch (IOException exception) {
			final String[] messages = { "Procedure class cannot be deleted.", "An exception occured.", "Please do this manually in the directory of procedure classes." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure class cannot be deleted", messages, exception);
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

		final JButton removeButton = new JButton("Remove procedure");

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				removeProcedureJavaClass(procedureJavaClass, owner, sessionFactory);

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