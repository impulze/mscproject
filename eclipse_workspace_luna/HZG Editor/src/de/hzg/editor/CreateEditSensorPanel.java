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
import org.hibernate.Transaction;

import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.ProcedureInstance;
import de.hzg.measurement.Sensor;

public class CreateEditSensorPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = 5644942285449679689L;
	private final JTextField nameTextField;
	private final JTextField deviceTextField;
	private final JCheckBox chckbxActive;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private final Sensor sensor;
	private RemoveListener removeListener;
	private SequentialGroup horizontalButtonGroup;
	private ParallelGroup verticalButtonGroup;
	private boolean editFunctionsShown = false;

	public CreateEditSensorPanel(Window owner, SessionFactory sessionFactory, Sensor sensor) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.sensor = sensor;

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
		sensorToFormAndProcedureInstances();
	}

	public static Sensor createNewSensor() {
		final Sensor sensor = new Sensor();

		sensor.setProcedureInstances(new ArrayList<ProcedureInstance>());

		return sensor;
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
		horizontalButtonGroup = topPanelLayout.createSequentialGroup()
			.addComponent(actionButton);
		final ParallelGroup labelsWithInputsAndButtonLayout = topPanelLayout.createParallelGroup(Alignment.LEADING)
			.addGroup(labelsWithInputsLayout)
			.addGroup(horizontalButtonGroup);
		final SequentialGroup horizontalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(labelsWithInputsAndButtonLayout)
			.addContainerGap(251, Short.MAX_VALUE);

		verticalButtonGroup = topPanelLayout.createParallelGroup(Alignment.BASELINE)
			.addComponent(actionButton);
		final SequentialGroup verticalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblName).addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addGroup(topPanelLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblDevice).addComponent(deviceTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(chckbxActive)
			.addGap(8)
			.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(verticalButtonGroup)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTable createTableArea(DataCreator dataCreator) {
		final Adder adder = new Adder();
		final JPanel addProcedureInstancePanel = new JPanel();

		addProcedureInstancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adder.addToPanel(addProcedureInstancePanel, "Add sensor instance", this);

		dataCreator.addPanel(addProcedureInstancePanel);

		dataCreator.addInformationMessage("Use right click to add/remove sensor instances.");
		dataCreator.addInformationMessage("Use double left click to change sensor instance values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "sensor instance");
		final String removeString = String.format("Remove %s", "sensor instance");
		final TablePopupMenu.ActionListener addListener = new TablePopupMenu.ActionListener() {
			public void actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addListener);
		cellPopupMenu.addItem(addString, addListener);
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ProcedureInstanceTableModel tableModel = (ProcedureInstanceTableModel)table.getModel();
				removeProcedureInstance(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable() {
		final ProcedureInstanceTableModel tableModel = new ProcedureInstanceTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(0).setCellRenderer(new ProcedureDescriptionComboBox.Renderer());
		table.getColumnModel().getColumn(1).setPreferredWidth(70);

		for (int i = 2;  i < 8; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(90);
			table.getColumnModel().getColumn(i).setCellRenderer(new ParameterTableCellRenderer());
		}

		final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tableModel);
		rowSorter.setComparator(0, new Comparator<ProcedureDescription>() {
			public int compare(ProcedureDescription procedureDescription1, ProcedureDescription procedureDescription2) {
				return procedureDescription1.getName().compareTo(procedureDescription2.getName());
			}
		});

		table.setRowSorter(rowSorter);
	}

	private void sensorToFormAndProcedureInstances() {
		nameTextField.setText(sensor.getName());
		deviceTextField.setText(sensor.getDevice());
		chckbxActive.setSelected(sensor.getActive());

		final ProcedureInstanceTableModel tableModel = (ProcedureInstanceTableModel)table.getModel();
		final TableColumn sensorDescriptionColumn = table.getColumnModel().getColumn(0);
		final ProcedureDescriptionComboBox comboBox = new ProcedureDescriptionComboBox(owner, sessionFactory);

		sensorDescriptionColumn.setCellEditor(new DefaultCellEditor(comboBox));
		tableModel.setProcedureInstances(sensor.getProcedureInstances());
	}

	private void formToSensor() {
		if (nameTextField.getText().length() == 0) {
			sensor.setName(null);
		} else {
			sensor.setName(nameTextField.getText());
		}

		if (deviceTextField.getText().length() == 0) {
			sensor.setDevice(null);
		} else {
			sensor.setDevice(deviceTextField.getText());
		}

		sensor.setActive(chckbxActive.isSelected());
	}

	public boolean isDirty() {
		{
			final String cmpString = sensor.getName() == null ? "" : sensor.getName();

			if (!nameTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		{
			final String cmpString = sensor.getDevice() == null ? "" : sensor.getDevice();

			if (!deviceTextField.getText().equals(cmpString)) {
				return true;
			}
		}

		if (chckbxActive.isSelected() != sensor.getActive()) {
			return true;
		}

		return super.isDirty();
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			formToSensor();

			if (!getSaved()) {
				session.save(sensor);
				session.flush();
			} else {
				session.update(sensor);
				session.flush();
			}

			JOptionPane.showMessageDialog(owner, "Sensor successfully saved.", "Sensor saved", JOptionPane.INFORMATION_MESSAGE);
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Sensor could not be saved.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor not saved", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	public void onAdd() {
		final AddProcedureInstanceDialog dialog = new AddProcedureInstanceDialog(owner, sessionFactory, sensor);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		final ProcedureInstance procedureInstance = dialog.getResult();

		if (procedureInstance != null) {
			final ProcedureInstanceTableModel tableModel = (ProcedureInstanceTableModel)table.getModel();

			tableModel.fireTableDataChanged();
		}
	}

	private void removeProcedureInstance(ProcedureInstanceTableModel tableModel, int row) {
		final List<ProcedureInstance> procedureInstances = tableModel.getProcedureInstances();
		final ProcedureInstance procedureInstance = procedureInstances.get(row);
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the sensor instance.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		final Session session = sessionFactory.openSession();
		try {
			session.delete(procedureInstance);
			session.flush();
			procedureInstances.remove(row);
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

	static boolean removeSensor(Sensor sensor, Window owner, SessionFactory sessionFactory) {
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the sensor and all sensor instances for this sensor.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return false;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			for (final ProcedureInstance probeInstance: sensor.getProcedureInstances()) {
				session.delete(probeInstance);
			}
			session.delete(sensor);
			session.flush();
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Sensor could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensor could not be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			return false;
		} finally {
			session.close();
		}

		return true;
	}

	void showEditFunctions() {
		if (editFunctionsShown) {
			return;
		}

		final JButton removeButton = new JButton("Remove sensor");

		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				removeSensor(sensor, owner, sessionFactory);

				if (removeListener != null) {
					removeListener.onRemove();
				}
			}
		});
		horizontalButtonGroup
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(removeButton);
		verticalButtonGroup.addComponent(removeButton);
		showBottom(true);
		editFunctionsShown = true;
	}

	void setRemoveListener(RemoveListener removeListener) {
		this.removeListener = removeListener;
	}
};