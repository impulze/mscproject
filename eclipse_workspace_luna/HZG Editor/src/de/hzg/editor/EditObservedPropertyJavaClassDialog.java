package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import de.hzg.common.ObservedPropertyClassesConfiguration;

public class EditObservedPropertyJavaClassDialog extends EditDialog<ObservedPropertyJavaClass> {
	private static final long serialVersionUID = -1282131025164002098L;
	private ObservedPropertyJavaClass observedPropertyJavaClass;

	public EditObservedPropertyJavaClassDialog(Window owner, ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration) {
		super(owner, "Select observed property class to edit");

		final JLabel observedPropertyJavaClassLabel = new JLabel("Observed property class:");
		final ObservedPropertyJavaClassComboBox observedPropertyJavaClassComboBox = new ObservedPropertyJavaClassComboBox(owner, observedPropertyClassesConfiguration);

		observedPropertyJavaClassLabel.setLabelFor(observedPropertyJavaClassComboBox);

		getContentPanel().add(observedPropertyJavaClassLabel);
		getContentPanel().add(observedPropertyJavaClassComboBox);

		final Window usedOwner = owner;
		final ObservedPropertyClassesConfiguration usedObservedPropertyClassesConfiguration = observedPropertyClassesConfiguration;

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String name = (String) observedPropertyJavaClassComboBox.getSelectedItem();
				final ObservedPropertyJavaClass observedPropertyJavaClass = ObservedPropertyJavaClass.loadByName(usedObservedPropertyClassesConfiguration, usedOwner, name);

				EditObservedPropertyJavaClassDialog.this.observedPropertyJavaClass = observedPropertyJavaClass;
				setVisible(false);
				dispose();
			}
		});
	}

	protected ObservedPropertyJavaClass getResult() {
		return observedPropertyJavaClass;
	}
}