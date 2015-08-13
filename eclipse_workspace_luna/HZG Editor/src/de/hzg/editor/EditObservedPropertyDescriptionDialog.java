package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.ObservedPropertyDescription;

public class EditObservedPropertyDescriptionDialog extends EditDialog<ObservedPropertyDescription> {
	private static final long serialVersionUID = 7612051986074038024L;
	private ObservedPropertyDescription observedPropertyDescription;

	public EditObservedPropertyDescriptionDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select observed property description to edit");

		final JLabel observedPropertyDescriptionNameLabel = new JLabel("Observed property description name:");
		final ObservedPropertyDescriptionComboBox observedPropertyDescriptionComboBox = new ObservedPropertyDescriptionComboBox(owner, sessionFactory);

		observedPropertyDescriptionNameLabel.setLabelFor(observedPropertyDescriptionComboBox);

		getContentPanel().add(observedPropertyDescriptionNameLabel);
		getContentPanel().add(observedPropertyDescriptionComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditObservedPropertyDescriptionDialog.this.observedPropertyDescription = (ObservedPropertyDescription)observedPropertyDescriptionComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected ObservedPropertyDescription getResult() {
		return observedPropertyDescription;
	}
}