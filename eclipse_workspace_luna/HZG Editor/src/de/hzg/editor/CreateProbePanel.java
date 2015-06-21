package de.hzg.editor;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class CreateProbePanel extends CreateEditProbePanel {
	private static final long serialVersionUID = -4192326896883996241L;

	public CreateProbePanel(Window owner, SessionFactory sessionFactory) {
		super(owner, "Create probe", sessionFactory);

		final Window finalOwner = owner;

		setActionButton("Save", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();

				try {
					session.save(getProbe());
					session.flush();
					JOptionPane.showMessageDialog(finalOwner, "Probe successfully saved.", "Probe saved", JOptionPane.INFORMATION_MESSAGE);
					updateSensors();
				} catch (Exception exception) {
					final String[] messages = { "Probe could not be saved.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(finalOwner, "Probe not saved", messages, exception);
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