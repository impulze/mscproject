package de.hzg.editor;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import de.hzg.sensors.Probe;
import de.hzg.sensors.SensorDescription;
import de.hzg.sensors.SensorInstance;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;

import javax.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JButton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class CreateEditProbePanel extends CreateEditPanel {
	private static final long serialVersionUID = 5644942285449679689L;
	private final JTextField nameTextField;
	private final JTextField deviceTextField;
	private final JCheckBox chckbxActive;
	private final JButton actionButton;
	private final JTable table;
	private final SessionFactory sessionFactory;
	private Probe probe;
	private boolean dirty = false;
	private final Window owner;

	public CreateEditProbePanel(Window owner, SessionFactory sessionFactory) {
		super("Probe information");

		this.sessionFactory = sessionFactory;
		this.owner = owner;

		nameTextField = new JTextField();
		nameTextField.setColumns(20);

		final JLabel lblName = new JLabel("Name:");
		lblName.setLabelFor(nameTextField);

		deviceTextField = new JTextField();
		deviceTextField.setColumns(10);

		final JLabel lblDevice = new JLabel("Device:");
		lblDevice.setLabelFor(deviceTextField);

		chckbxActive = new JCheckBox("active");

		actionButton = new JButton();

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

		table = createTable(getBottomPanel());

		probe = new Probe();
		probe.setSensorInstances(new ArrayList<SensorInstance>());
		setProbe(probe);
	}

	void setProbe(Probe probe) {
		this.probe = probe;
		nameTextField.setText(probe.getName());
		deviceTextField.setText(probe.getDevice());
		chckbxActive.setSelected(probe.getActive());
		updateSensors(probe);
	}

	public boolean isDirty() {
		{
			final String cmpString = probe.getName() == null ? "" : probe.getName();

			if (!nameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = probe.getDevice() == null ? "" : probe.getDevice();

			if (!deviceTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		if (chckbxActive.isSelected() != probe.getActive()) {
			return true;
		}

		return dirty;
	}

	public void setActionButton(String text, ActionListener actionListener) {
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

	protected void updateSensors(Probe probe) {
		assert(probe != null);

		final SensorInstanceTableModel tableModel = (SensorInstanceTableModel)table.getModel();
		final TableColumn sensorDescriptionColumn = table.getColumnModel().getColumn(0);

		sensorDescriptionColumn.setCellEditor(new DescriptionCellEditor(probe));

		tableModel.setSensorInstances(probe.getSensorInstances());
	}

	private JTable createTable(JPanel panelToAddTo) {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		final JPanel gridLabel1 = makeGridLabel("Use right click to add/remove senors.");
		final JPanel gridLabel2 = makeGridLabel("Use left click(s) to change sensor values.");
		final JPanel gridLabel3 = makeGridLabel("Click column header to sort ascending/descending.");
		final JPanel gridLabelIcon1 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));
		final JPanel gridLabelIcon2 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));
		final JPanel gridLabelIcon3 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));

		final JScrollPane scrollPane = new JScrollPane();
		final SensorInstanceTableModel tableModel = new SensorInstanceTableModel(owner, sessionFactory);
		final JTable table = new JTable();

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setModel(tableModel);
		table.setRowHeight(25);
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(0).setCellRenderer(new DescriptionCellRenderer());
		table.getColumnModel().getColumn(1).setPreferredWidth(70);

		for (int i = 2;  i < 8; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(90);
			table.getColumnModel().getColumn(i).setCellRenderer(new ParameterTableCellRenderer());
		}

		table.setAutoCreateRowSorter(true);
		final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tableModel);
		table.setRowSorter(rowSorter);

		rowSorter.setComparator(0, new Comparator<SensorDescription>() {
			public int compare(SensorDescription sensorDescription1, SensorDescription sensorDescription2) {
				return sensorDescription1.getName().compareTo(sensorDescription2.getName());
			}
		});

		scrollPane.setViewportView(table);

		panelToAddTo.setLayout(layout);

		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon1, constraints);
		panelToAddTo.add(gridLabelIcon1);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel1, constraints);
		panelToAddTo.add(gridLabel1);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon2, constraints);
		panelToAddTo.add(gridLabelIcon2);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel2, constraints);
		panelToAddTo.add(gridLabel2);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon3, constraints);
		panelToAddTo.add(gridLabelIcon3);

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel3, constraints);
		panelToAddTo.add(gridLabel3);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		layout.setConstraints(scrollPane, constraints);
		panelToAddTo.add(scrollPane);

		return table;
	}

	private JPanel makeGridLabel(String string) {
		final JLabel gridLabel = new JLabel(string);
		final JPanel gridLabelPanel = new JPanel();

		gridLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		gridLabelPanel.add(gridLabel);
		return gridLabelPanel;
	}

	private JPanel makeGridIcon(Icon icon) {
		final double scale = 0.5;
		final int newWidth = (int)(scale * icon.getIconWidth());
		final int newHeight= (int)(scale * icon.getIconWidth());
		final Image image = ((ImageIcon)icon).getImage();
		final Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		final Icon newIcon = new ImageIcon(newImage);
		final JLabel gridIconLabel = new JLabel(newIcon);
		final JPanel gridIconLabelPanel = new JPanel();

		gridIconLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		gridIconLabelPanel.add(gridIconLabel);

		return gridIconLabelPanel;
	}

	protected ActionListener getSaveActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();

				try {
					updateProbeInformation();
					session.save(getProbe());
					session.flush();
					dirty = false;
					JOptionPane.showMessageDialog(owner, "Probe successfully saved.", "Probe saved", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					dirty = true;
					final String[] messages = { "Probe could not be saved.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(owner, "Probe not saved", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				} finally {
					session.close();
				}
			}
		};
	}

	protected ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();
				try {
					updateProbeInformation();
					session.update(getProbe());
					session.flush();
					dirty = false;
					JOptionPane.showMessageDialog(owner, "Probe successfully updated.", "Probe updated", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					dirty = true;
					final String[] messages = { "Probe could not be updated.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(owner, "Probe not updated", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				} finally {
					session.close();
				}
			}
		};
	}

	private void updateProbeInformation() {
		if (nameTextField.getText().length() != 0) {
			getProbe().setName(nameTextField.getText());
		}

		if (deviceTextField.getText().length() != 0) {
			getProbe().setDevice(deviceTextField.getText());
		}

		getProbe().setActive(chckbxActive.isSelected());
		dirty = true;
	}
}
