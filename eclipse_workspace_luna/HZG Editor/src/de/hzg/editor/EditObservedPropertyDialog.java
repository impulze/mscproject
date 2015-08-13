package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.ObservedPropertyDescription;

public class EditObservedPropertyDialog extends EditDialog<ObservedPropertyDescription> {
	private static final long serialVersionUID = 7612051986074038024L;
	private ObservedPropertyDescription observedPropertyDescription;

	public EditObservedPropertyDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select observed property to edit");

		final JLabel sensorNameLabel = new JLabel("Observed property name:");
		final ObservedPropertyDescriptionComboBox observedPropertyDescriptionComboBox = new ObservedPropertyDescriptionComboBox(owner, sessionFactory);

		sensorNameLabel.setLabelFor(observedPropertyDescriptionComboBox);

		getContentPanel().add(sensorNameLabel);
		getContentPanel().add(observedPropertyDescriptionComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditObservedPropertyDialog.this.observedPropertyDescription = (ObservedPropertyDescription)observedPropertyDescriptionComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected ObservedPropertyDescription getResult() {
		return observedPropertyDescription;
	}
}