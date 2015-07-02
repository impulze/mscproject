package de.hzg.editor;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.hzg.measurement.Probe;
import de.hzg.measurement.SensorDescription;
import de.hzg.measurement.SensorInstance;

public class AddSensorInstanceDialog extends EditDialog<SensorInstance> {
	private static final long serialVersionUID = -8473878538864023713L;
	private SensorInstance sensorInstance;

	public AddSensorInstanceDialog(Window owner, SessionFactory sessionFactory, Probe probe) {
		super(owner, "Add sensor instance");

		final JLabel addressLabel = new JLabel("Address:");
		final JTextField addressTextField = new JTextField("0");
		addressLabel.setLabelFor(addressTextField);
		addressTextField.setColumns(5);
		((AbstractDocument)addressTextField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(owner));

		getContentPanel().add(addressLabel);
		getContentPanel().add(addressTextField);

		final JLabel nameLabel = new JLabel("Sensor name:");
		final SensorDescriptionComboBox sensorDescriptionComboBox = new SensorDescriptionComboBox(owner, sessionFactory);
		nameLabel.setLabelFor(sensorDescriptionComboBox);

		getContentPanel().add(nameLabel);
		getContentPanel().add(sensorDescriptionComboBox);

		final Window usedOwner = owner;
		final SessionFactory usedSessionFactory = sessionFactory;
		final Probe usedProbe = probe; 

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final SensorDescription sensorDescription = (SensorDescription)sensorDescriptionComboBox.getSelectedItem();
				final Session session = usedSessionFactory.openSession();
				final List<SensorInstance> probeSensorInstances = usedProbe.getSensorInstances();
				final List<SensorInstance> sensorDescriptionSensorInstances = sensorDescription.getSensorInstances();
				sensorInstance = new SensorInstance();
				Transaction transaction = null;

				try {
					transaction = session.beginTransaction();

					sensorInstance.setAddress(Integer.parseInt(addressTextField.getText()));
					sensorInstance.setProbe(usedProbe);
					sensorInstance.setSensorDescription(sensorDescription);
					sensorInstance.setParameter1(0.0);
					sensorInstance.setParameter2(0.0);
					sensorInstance.setParameter3(0.0);
					sensorInstance.setParameter4(0.0);
					sensorInstance.setParameter5(0.0);
					sensorInstance.setParameter6(0.0);
					probeSensorInstances.add(sensorInstance);
					sensorDescriptionSensorInstances.add(sensorInstance);
					session.update(sensorDescription);
					session.update(usedProbe);
					session.save(sensorInstance);
					transaction.commit();
					setVisible(false);
					dispose();
				} catch (Exception exception) {
					if (transaction != null) {
						transaction.rollback();
					}

					probeSensorInstances.remove(sensorInstance);
					sensorDescriptionSensorInstances.remove(sensorInstance);

					final String[] messages = { "Sensor instance could not be created.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(usedOwner, "Probe not loaded", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(usedOwner);
					dialog.setVisible(true);
					return;
				} finally {
					session.close();
				}
			}
		});
	}

	protected SensorInstance getResult() {
		return sensorInstance;
	}
}