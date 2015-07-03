package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import org.hibernate.SessionFactory;

import de.hzg.measurement.Probe;

public class EditProbeDialog extends EditDialog<Probe> {
	private static final long serialVersionUID = -4236878158400999625L;
	private Probe probe;

	public EditProbeDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select probe to edit");

		final ProbeComboBox probeComboBox = new ProbeComboBox(owner, sessionFactory);
		final JLabel probeNameLabel = new JLabel("Probe name:");

		probeNameLabel.setLabelFor(probeComboBox);

		getContentPanel().add(probeNameLabel);
		getContentPanel().add(probeComboBox);

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditProbeDialog.this.probe = (Probe)probeComboBox.getSelectedItem();

				setVisible(false);
				dispose();
			}
		});
	}

	protected Probe getResult() {
		return probe;
	}
}