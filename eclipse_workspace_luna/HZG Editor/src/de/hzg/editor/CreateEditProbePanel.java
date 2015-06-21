package de.hzg.editor;

import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.Window;

import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import de.hzg.sensors.Probe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class CreateEditProbePanel extends CreateEditPanel {
	private static final long serialVersionUID = 5644942285449679689L;
	private final JTextField nameTextField;
	private final JTextField deviceTextField;
	private final JButton actionButton;
	private final JTable table;
	private final SessionFactory sessionFactory;
	private Probe probe = new Probe();
	private boolean dirty = false;

	public CreateEditProbePanel(Window owner, String borderTitle, SessionFactory sessionFactory) {
		super(borderTitle);

		this.sessionFactory = sessionFactory;

		final Window finalOwner = owner;

		nameTextField = new JTextField();
		nameTextField.setColumns(20);
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				probe.setName(nameTextField.getText());
			}

			public void insertUpdate(DocumentEvent arg0) {
				probe.setName(nameTextField.getText());
			}

			public void removeUpdate(DocumentEvent arg0) {
				probe.setName(nameTextField.getText());
			}
		});

		final JLabel lblName = new JLabel("Name:");
		lblName.setLabelFor(nameTextField);

		deviceTextField = new JTextField();
		deviceTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent arg0) {
				probe.setDevice(deviceTextField.getText());
			}

			public void insertUpdate(DocumentEvent arg0) {
				probe.setDevice(deviceTextField.getText());
			}

			public void removeUpdate(DocumentEvent arg0) {
				probe.setDevice(deviceTextField.getText());
			}
		});
		deviceTextField.setColumns(10);

		final JLabel lblDevice = new JLabel("Device:");
		lblDevice.setLabelFor(deviceTextField);

		final JCheckBox chckbxActive = new JCheckBox("active");
		chckbxActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final AbstractButton abstractButton = (AbstractButton)arg0.getSource();
				dirty = abstractButton.getModel().isSelected();
			}
		});

		actionButton = new JButton();
		setActionButton("Save", new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = CreateEditProbePanel.this.sessionFactory.openSession();

				try {
					session.save(probe);
					session.flush();
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

		final GroupLayout gl_panel = new GroupLayout(getTopPanel());
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblName)
								.addComponent(lblDevice))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(deviceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(chckbxActive)))
						.addComponent(actionButton))
					.addContainerGap(251, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDevice)
						.addComponent(deviceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addComponent(chckbxActive)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(actionButton)
					.addContainerGap())
		);
		getTopPanel().setLayout(gl_panel);

		getBottomPanel().setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Sensors", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getBottomPanel().setLayout(new BorderLayout());

		final JScrollPane scrollPane_1 = new JScrollPane();
		getBottomPanel().add(scrollPane_1);

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Description", "Address", "Parameter1", "Parameter2", "Parameter3", "Parameter4", "Parameter5", "Parameter6"
			}
		) {
			private static final long serialVersionUID = -1593462583060407344L;

			final Class<?>[] columnTypes = new Class[] {
				Object.class, Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.getColumnModel().getColumn(2).setPreferredWidth(90);
		table.getColumnModel().getColumn(3).setPreferredWidth(90);
		table.getColumnModel().getColumn(4).setPreferredWidth(90);
		table.getColumnModel().getColumn(5).setPreferredWidth(90);
		table.getColumnModel().getColumn(6).setPreferredWidth(90);
		table.getColumnModel().getColumn(7).setPreferredWidth(90);
		scrollPane_1.setViewportView(table);
	}

	void setProbe(Probe probe) {
		this.probe = probe;
		nameTextField.setText(probe.getName());
		deviceTextField.setText(probe.getDevice());
	}

	public boolean isDirty() {
		if (nameTextField.getText().length() != 0) {
			return true;
		}

		if (deviceTextField.getText().length() != 0) {
			return true;
		}

		return dirty;
	}

	protected void setActionButton(String text, ActionListener actionListener) {
		actionButton.setText(text);

		for (final ActionListener registeredActionListener: actionButton.getActionListeners()) {
			actionButton.removeActionListener(registeredActionListener);
		}

		actionButton.addActionListener(actionListener);
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected Probe getProbe() {
		return probe;
	}

	protected void updateSensors() {
	}
	}
}