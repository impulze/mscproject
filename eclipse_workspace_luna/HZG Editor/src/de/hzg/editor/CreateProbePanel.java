package de.hzg.editor;

import java.awt.Window;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.Probe;
import de.hzg.sensors.SensorInstance;

public class CreateProbePanel extends CreateEditProbePanel implements DataProvider {
	private static final long serialVersionUID = -7577066116377617216L;

	public CreateProbePanel(Window owner, SessionFactory sessionFactory) {
		super(owner, sessionFactory, createEmptyProbe());

		setDataProvider(this);
		setTitle("Create probe");
	}

	private static Probe createEmptyProbe() {
		final Probe probe = new Probe();

		probe.setSensorInstances(new ArrayList<SensorInstance>());

		return probe;
	}

	public void provide() {
		final Session session = getSessionFactory().openSession();

		try {
			formToProbe();
			session.save(getProbe());
			session.flush();
			setDirty(false);
			JOptionPane.showMessageDialog(getOwner(), "Probe successfully saved.", "Probe saved", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception exception) {
			setDirty(true);
			final String[] messages = { "Probe could not be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(getOwner(), "Probe not saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(getOwner());
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}
}
