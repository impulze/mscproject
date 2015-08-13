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

import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.ProcedureInstance;
import de.hzg.measurement.Sensor;

public class AddProcedureInstanceDialog extends EditDialog<ProcedureInstance> {
	private static final long serialVersionUID = -8473878538864023713L;
	private ProcedureInstance procedureInstance;

	public AddProcedureInstanceDialog(Window owner, SessionFactory sessionFactory, Sensor sensor) {
		super(owner, "Add procedure instance");

		final JLabel addressLabel = new JLabel("Address:");
		final JTextField addressTextField = new JTextField("0");
		addressLabel.setLabelFor(addressTextField);
		addressTextField.setColumns(5);
		((AbstractDocument)addressTextField.getDocument()).setDocumentFilter(new IntegerDocumentFilter(owner));

		getContentPanel().add(addressLabel);
		getContentPanel().add(addressTextField);

		final JLabel nameLabel = new JLabel("Procedure name:");
		final ProcedureDescriptionComboBox procedureDescriptionComboBox = new ProcedureDescriptionComboBox(owner, sessionFactory);
		nameLabel.setLabelFor(procedureDescriptionComboBox);

		getContentPanel().add(nameLabel);
		getContentPanel().add(procedureDescriptionComboBox);

		final Window usedOwner = owner;
		final SessionFactory usedSessionFactory = sessionFactory;
		final Sensor usedSensor = sensor; 

		setOKListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final ProcedureDescription procedureDescription = (ProcedureDescription)procedureDescriptionComboBox.getSelectedItem();
				final Session session = usedSessionFactory.openSession();
				final List<ProcedureInstance> sensorProcedureInstances = usedSensor.getProcedureInstances();
				final List<ProcedureInstance> procedureDescriptionProcedureInstances = procedureDescription.getProcedureInstances();
				procedureInstance = new ProcedureInstance();
				Transaction transaction = null;

				try {
					transaction = session.beginTransaction();

					procedureInstance.setAddress(Integer.parseInt(addressTextField.getText()));
					procedureInstance.setSensor(usedSensor);
					procedureInstance.setProcedureDescription(procedureDescription);
					procedureInstance.setParameter1(0.0);
					procedureInstance.setParameter2(0.0);
					procedureInstance.setParameter3(0.0);
					procedureInstance.setParameter4(0.0);
					procedureInstance.setParameter5(0.0);
					procedureInstance.setParameter6(0.0);
					sensorProcedureInstances.add(procedureInstance);
					procedureDescriptionProcedureInstances.add(procedureInstance);
					session.update(procedureDescription);
					session.update(usedSensor);
					session.save(procedureInstance);
					transaction.commit();
					setVisible(false);
					dispose();
				} catch (Exception exception) {
					if (transaction != null) {
						transaction.rollback();
					}

					sensorProcedureInstances.remove(procedureInstance);
					procedureDescriptionProcedureInstances.remove(procedureInstance);

					final String[] messages = { "Procedure instance could not be created.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(usedOwner, "Sensor not loaded", messages, exception);
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

	protected ProcedureInstance getResult() {
		return procedureInstance;
	}
}