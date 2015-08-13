package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import de.hzg.common.ProcedureClassesConfiguration;

public class EditProcedureJavaClassDialog extends EditDialog<ProcedureJavaClass> {
	private static final long serialVersionUID = -1282131025164002098L;
	private ProcedureJavaClass procedureJavaClass;

	public EditProcedureJavaClassDialog(Window owner, ProcedureClassesConfiguration procedureClassesConfiguration) {
		super(owner, "Select procedure class to edit");

		final JLabel procedureJavaClassLabel = new JLabel("Procedure class:");
		final ProcedureJavaClassComboBox procedureJavaClassComboBox = new ProcedureJavaClassComboBox(owner, procedureClassesConfiguration);

		procedureJavaClassLabel.setLabelFor(procedureJavaClassComboBox);

		getContentPanel().add(procedureJavaClassLabel);
		getContentPanel().add(procedureJavaClassComboBox);

		final Window usedOwner = owner;
		final ProcedureClassesConfiguration usedProcedureClassesConfiguration = procedureClassesConfiguration;

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String name = (String) procedureJavaClassComboBox.getSelectedItem();
				final ProcedureJavaClass procedureJavaClass = ProcedureJavaClass.loadByName(usedProcedureClassesConfiguration, usedOwner, name);

				EditProcedureJavaClassDialog.this.procedureJavaClass = procedureJavaClass;
				setVisible(false);
				dispose();
			}
		});
	}

	protected ProcedureJavaClass getResult() {
		return procedureJavaClass;
	}
}