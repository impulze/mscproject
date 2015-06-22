package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.Probe;

public class EditProbeDialog extends EditDialog<Probe> {
	private static final long serialVersionUID = -4236878158400999625L;
	private Probe probe;
	private final SessionFactory sessionFactory;

	public EditProbeDialog(Window owner, SessionFactory sessionFactory) {
		super(owner, "Select probe to edit", "Probe name");
		getNameTextField().setColumns(20);

		this.sessionFactory = sessionFactory;

		final Window finalOwner = owner;

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = EditProbeDialog.this.sessionFactory.openSession();
				final JTextField nameTextField = getNameTextField();
				final String name = nameTextField.getText();
				final List<Probe> result;

				try {
					@SuppressWarnings("unchecked")
					final List<Probe> tempResult = (List<Probe>)session
						.createQuery("FROM Probe WHERE name = :name")
						.setParameter("name", name)
						.list();

					result = tempResult;
					handleResult(result, name);
				} catch (Exception exception) {
					final String[] messages = { "Probes could not be loaded.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(finalOwner, "Probes not loaded", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(finalOwner);
					dialog.setVisible(true);
					return;
				} finally {
					session.close();
				}
			}

			public void handleResult(List<Probe> result, String name) {
				if (result.size() == 0) {
					final String message = String.format(
						"The probe with the name '%s' is not in the database.\n" +
						"Try to look it up via 'Edit -> List probes'.",
						name);
					JOptionPane.showMessageDialog(EditProbeDialog.this, message, "Probe not found", JOptionPane.ERROR_MESSAGE);
				} else if (result.size() != 1) {
					final String message = String.format(
							"The probe with the name '%s' occurs multiple times in the database.\n" +
							"This is a program error and needs to be fixed manually by accessing the database.",
							name);
					JOptionPane.showMessageDialog(EditProbeDialog.this, message, "Duplicate probe", JOptionPane.ERROR_MESSAGE);					
				} else {
					probe = result.get(0);
					Probe.initProbe(probe);
					setVisible(false);
					dispose();
				}
			}
		});
	}

	protected Probe getResult() {
		return probe;
	}
}