package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.Probe;
import de.hzg.measurement.SensorDescription;
import de.hzg.measurement.SensorInstance;

public class CreateEditProbePanel extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = 5644942285449679689L;
	private final JTextField nameTextField;
	private final JTextField deviceTextField;
	private final JCheckBox chckbxActive;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private final Probe probe;

	public CreateEditProbePanel(Window owner, SessionFactory sessionFactory, Probe probe) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.probe = probe;

		nameTextField = new JTextField();
		nameTextField.setColumns(20);

		deviceTextField = new JTextField();
		deviceTextField.setColumns(10);

		chckbxActive = new JCheckBox("active");

		final DataCreator dataCreator = new DataCreator();
		table = createTableArea(dataCreator);
		setupTable();

		setBottomPanelTitle("Sensors");
		getBottomPanel().add(dataCreator.createPanel(table));
		createForm();

		setDataProvider(this);
		probeToFormAndSensorInstances();
	}

	public static Probe createNewProbe() {
		final Probe probe = new Probe();

		probe.setSensorInstances(new ArrayList<SensorInstance>());

		return probe;
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Save information");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final JLabel lblName = new JLabel("Name:");
		lblName.setLabelFor(nameTextField);

		final JLabel lblDevice = new JLabel("Device:");
		lblDevice.setLabelFor(deviceTextField);

		final ParallelGroup labelsLayout = topPanelLayout.createParallelGroup(Alignment.TRAILING)
			.addComponent(lblName)
			.addComponent(lblDevice);
		final ParallelGroup inputsLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addComponent(deviceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
			.addComponent(chckbxActive);
		final SequentialGroup labelsWithInputsLayout = topPanelLayout.createSequentialGroup()
			.addGroup(labelsLayout)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(inputsLayout);
		final ParallelGroup labelsWithInputsAndButtonLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(labelsWithInputsLayout)
			.addComponent(actionButton);
		final SequentialGroup horizontalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(labelsWithInputsAndButtonLayout)
			.addContainerGap(251, Short.MAX_VALUE);

		final SequentialGroup verticalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblName).addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDevice).addComponent(deviceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(chckbxActive)
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(actionButton)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTable createTableArea(DataCreator dataCreator) {
		final JPanel addSensorInstancePanel = new JPanel();
		final JButton addSensorInstanceButton = new JButton("Add sensor instance");

		addSensorInstanceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addSensorInstance();
			}
		});

		addSensorInstancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		addSensorInstancePanel.add(addSensorInstanceButton);

		dataCreator.addPanel(addSensorInstancePanel);

		dataCreator.addInformationMessage("Use right click to add/remove sensor instances.");
		dataCreator.addInformationMessage("Use double left click to change sensor instance values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "sensor instance");
		final String removeString = String.format("Remove %s", "sensor instance");
		final TablePopupMenu.ActionListener addListener = new TablePopupMenu.ActionListener() {
			public void actionPerformed(JTable table, int row, int column, ActionEvent event) {
				addSensorInstance();
			}
		};

		noCellPopupMenu.addItem(addString, addListener);
		cellPopupMenu.addItem(addString, addListener);
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final SensorInstanceTableModel tableModel = (SensorInstanceTableModel) table.getModel();
				removeSensorInstance(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable() {
		final SensorInstanceTableModel tableModel = new SensorInstanceTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(0).setCellRenderer(new SensorDescriptionComboBox.Renderer());
		table.getColumnModel().getColumn(1).setPreferredWidth(70);

		for (int i = 2;  i < 8; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(90);
			table.getColumnModel().getColumn(i).setCellRenderer(new ParameterTableCellRenderer());
		}

		final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tableModel);
		rowSorter.setComparator(0, new Comparator<SensorDescription>() {
			public int compare(SensorDescription sensorDescription1, SensorDescription sensorDescription2) {
				return sensorDescription1.getName().compareTo(sensorDescription2.getName());
			}
		});

		table.setRowSorter(rowSorter);
	}

	private void probeToFormAndSensorInstances() {
		nameTextField.setText(probe.getName());
		deviceTextField.setText(probe.getDevice());
		chckbxActive.setSelected(probe.getActive());

		final SensorInstanceTableModel tableModel = (SensorInstanceTableModel)table.getModel();
		final TableColumn sensorDescriptionColumn = table.getColumnModel().getColumn(0);
		final SensorDescriptionComboBox comboBox = new SensorDescriptionComboBox(owner, sessionFactory);

		sensorDescriptionColumn.setCellEditor(new DefaultCellEditor(comboBox));
		tableModel.setSensorInstances(probe.getSensorInstances());
	}

	private void formToProbe() {
		if (nameTextField.getText().length() == 0) {
			probe.setName(null);
		} else {
			probe.setName(nameTextField.getText());
		}

		if (deviceTextField.getText().length() == 0) {
			probe.setDevice(null);
		} else {
			probe.setDevice(deviceTextField.getText());
		}

		probe.setActive(chckbxActive.isSelected());
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

		return super.isDirty();
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			formToProbe();

			if (!getSaved()) {
				session.save(probe);
				session.flush();
			} else {
				session.update(probe);
				session.flush();
			}

			JOptionPane.showMessageDialog(owner, "Probe successfully saved.", "Probe saved", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Probe could not be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Probe not saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	private void addSensorInstance() {
		final AddSensorInstanceDialog dialog = new AddSensorInstanceDialog(owner, sessionFactory, probe);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		final SensorInstance sensorInstance = dialog.getResult();

		if (sensorInstance != null) {
			((SensorInstanceTableModel)table.getModel()).fireTableDataChanged();
		}
	}

	private void removeSensorInstance(SensorInstanceTableModel tableModel, int row) {
		final List<SensorInstance> sensorInstances = tableModel.getSensorInstances();
		final SensorInstance sensorInstance = sensorInstances.get(row);
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the sensor instance.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		final Session session = sessionFactory.openSession();
		try {
			session.delete(sensorInstance);
			session.flush();
			sensorInstances.remove(row);
			tableModel.fireTableDataChanged();
		} catch (Exception exception) {
			final String[] messages = { "Sensor instance could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor instance could not be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}
};