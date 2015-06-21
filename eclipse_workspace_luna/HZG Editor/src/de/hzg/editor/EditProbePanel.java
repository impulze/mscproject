package de.hzg.editor;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class EditProbePanel extends CreateEditProbePanel {
	private static final long serialVersionUID = 528512344511140597L;

	public EditProbePanel(Window owner, SessionFactory sessionFactory) {
		super(owner, "Edit probe", sessionFactory);

		final Window finalOwner = owner;

		setActionButton("Update", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();

				try {
					session.update(getProbe());
					session.flush();
					JOptionPane.showMessageDialog(finalOwner, "Probe successfully edited.", "Probe edited", JOptionPane.INFORMATION_MESSAGE);
					updateSensors();
				} catch (Exception exception) {
					final String[] messages = { "Probe could not be updated.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(finalOwner, "Probe not updated", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(finalOwner);
					dialog.setVisible(true);
				} finally {
					session.close();
				}
			}
		});
	}
}