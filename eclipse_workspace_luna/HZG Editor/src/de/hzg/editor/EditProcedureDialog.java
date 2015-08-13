package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.ProcedureDescription;

public class EditProcedureDialog extends EditDialog<ProcedureDescription> {
	private static final long serialVersionUID = 7612051986074038024L;
	private ProcedureDescription procedureDescription;

	public EditProcedureDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select procedure to edit");

		final JLabel sensorNameLabel = new JLabel("Procedure name:");
		final ProcedureDescriptionComboBox procedureDescriptionComboBox = new ProcedureDescriptionComboBox(owner, sessionFactory);

		sensorNameLabel.setLabelFor(procedureDescriptionComboBox);

		getContentPanel().add(sensorNameLabel);
		getContentPanel().add(procedureDescriptionComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditProcedureDialog.this.procedureDescription = (ProcedureDescription)procedureDescriptionComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected ProcedureDescription getResult() {
		return procedureDescription;
	}
}