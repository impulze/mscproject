package de.hzg.editor;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
		table = createTable(dataCreator);
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

	private JTable createTable(DataCreator dataCreator) {
		dataCreator.addInformationMessage("Use right click to add/edit/remove sensor instances.");
		dataCreator.addInformationMessage("Use double left click to change sensor instance values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "sensor instance");
		final String editString = String.format("Edit %s", "sensor instance");
		final String removeString = String.format("Remove %s", "sensor instance");

		noCellPopupMenu.addItem(addString);
		cellPopupMenu.addItem(addString);
		cellPopupMenu.addItem(editString);
		cellPopupMenu.addItem(removeString);

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable() {
		final SensorInstanceTableModel tableModel = new SensorInstanceTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(0).setCellRenderer(new SensorDescriptionCellRenderer());
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

		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<SensorDescription> result = (List<SensorDescription>)session
				.createQuery("FROM SensorDescription")
				.list();
			sensorDescriptionColumn.setCellEditor(new SensorDescriptionCellEditor(result));
		} catch (Exception exception) {
			final String[] messages = { "Sensor descriptions could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor descriptions not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

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
}