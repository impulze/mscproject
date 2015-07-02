package de.hzg.editor;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.Probe;

public class EditProbePanel extends CreateEditProbePanel implements DataProvider {
	private static final long serialVersionUID = 3216168400777219563L;

	public EditProbePanel(Window owner, SessionFactory sessionFactory, Probe probe) {
		super(owner, sessionFactory, probe);

		setDataProvider(this);
		setTitle("Edit probe");
	}

	public void provide() {
		final Session session = getSessionFactory().openSession();
		try {
			formToProbe();
			session.update(getProbe());
			session.flush();
			setDirty(false);
			JOptionPane.showMessageDialog(getOwner(), "Probe successfully updated.", "Probe updated", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			setDirty(true);
			final String[] messages = { "Probe could not be updated.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(getOwner(), "Probe not updated", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(getOwner());
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}
}