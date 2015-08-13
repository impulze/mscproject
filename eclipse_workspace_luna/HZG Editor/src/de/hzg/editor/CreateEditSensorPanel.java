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

import de.hzg.measurement.CalibrationSet;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.ObservedPropertyInstance;
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
		sensorToFormAndObjects();
	}

	public static Sensor createNewSensor() {
		final Sensor sensor = new Sensor();

		sensor.setObservedPropertyInstances(new ArrayList<ObservedPropertyInstance>());

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
		final JPanel addObservedPropertyInstancePanel = new JPanel();

		addObservedPropertyInstancePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adder.addToPanel(addObservedPropertyInstancePanel, "Add observed property instance", this);

		dataCreator.addPanel(addObservedPropertyInstancePanel);

		dataCreator.addInformationMessage("Use right click to add/remove observed property instances or edit calibration sets.");
		dataCreator.addInformationMessage("Use double left click to change observed property instance and calibration set values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu observedPropertyInstanceCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu calibrationSetCellPopupMenu = new TablePopupMenu();
		final String addObservedPropertyInstanceString = String.format("Add %s", "observed property instance");
		final String removeObservedPropertyInstanceString = String.format("Remove %s", "observed property instance");
		final String editCalibrationSetString = String.format("Edit %s", "calibration set");
		final TablePopupMenu.ActionListener addObservedPropertyInstanceListener = new TablePopupMenu.ActionListener() {
			public void actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAddObservedPropertyInstance();
			}
		};
		final TablePopupMenu.ActionListener removeObservedPropertyInstanceListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ObservedPropertyInstanceTableModel tableModel = (ObservedPropertyInstanceTableModel)table.getModel();
				removeObservedPropertyInstance(tableModel, row);
			}
		};

		noCellPopupMenu.addItem(addObservedPropertyInstanceString, addObservedPropertyInstanceListener);
		observedPropertyInstanceCellPopupMenu.addItem(addObservedPropertyInstanceString, addObservedPropertyInstanceListener);
		observedPropertyInstanceCellPopupMenu.addItem(removeObservedPropertyInstanceString, removeObservedPropertyInstanceListener);
		calibrationSetCellPopupMenu.addItem(addObservedPropertyInstanceString,  addObservedPropertyInstanceListener);
		calibrationSetCellPopupMenu.addItem(removeObservedPropertyInstanceString,  removeObservedPropertyInstanceListener);
		calibrationSetCellPopupMenu.addItem(editCalibrationSetString, null);

		dataCreator.setCellPopupMenu(4, observedPropertyInstanceCellPopupMenu);
		dataCreator.setCellPopupMenu(5, calibrationSetCellPopupMenu);
		dataCreator.setCellPopupMenu(-1, observedPropertyInstanceCellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable() {
		final ObservedPropertyInstanceTableModel tableModel = new ObservedPropertyInstanceTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(85);
		table.getColumnModel().getColumn(0).setCellRenderer(new ObservedPropertyDescriptionComboBox.Renderer());
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(70);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setPreferredWidth(200);
		table.getColumnModel().getColumn(5).setCellRenderer(new CalibrationSetComboBox.Renderer(false));

		for (int i = 6;  i < 12; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(90);
			table.getColumnModel().getColumn(i).setCellRenderer(new ParameterTableCellRenderer());
		}

		final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tableModel);

		rowSorter.setComparator(0, new Comparator<ObservedPropertyDescription>() {
			public int compare(ObservedPropertyDescription observedPropertyDescription1, ObservedPropertyDescription observedPropertyDescription2) {
				return observedPropertyDescription1.getName().compareTo(observedPropertyDescription2.getName());
			}
		});

		rowSorter.setComparator(5, new Comparator<CalibrationSet>() {
			public int compare(CalibrationSet calibrationSet1, CalibrationSet calibrationSet2) {
				return calibrationSet1.getValidStart().compareTo(calibrationSet2.getValidStart());
			}
		});

		table.setRowSorter(rowSorter);
	}

	private void sensorToFormAndObjects() {
		nameTextField.setText(sensor.getName());
		deviceTextField.setText(sensor.getDevice());
		chckbxActive.setSelected(sensor.getActive());

		final ObservedPropertyInstanceTableModel tableModel = (ObservedPropertyInstanceTableModel)table.getModel();
		final TableColumn observedPropertyDescriptionColumn = table.getColumnModel().getColumn(0);
		final TableColumn calibrationSetColumn = table.getColumnModel().getColumn(5);
		final ObservedPropertyDescriptionComboBox observedPropertyDescriptionComboBox = new ObservedPropertyDescriptionComboBox(owner, sessionFactory);
		final CalibrationSetComboBox calibrationSetComboBox = new CalibrationSetComboBox(owner, sessionFactory);

		observedPropertyDescriptionColumn.setCellEditor(new DefaultCellEditor(observedPropertyDescriptionComboBox));
		calibrationSetColumn.setCellEditor(new DefaultCellEditor(calibrationSetComboBox));

		tableModel.setObservedPropertyInstances(sensor.getObservedPropertyInstances());
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
		// called from a button
		onAddObservedPropertyInstance();
	}

	public void onAddObservedPropertyInstance() {
		// called from button or popup menu
		final AddObservedPropertyInstanceDialog dialog = new AddObservedPropertyInstanceDialog(owner, sessionFactory, sensor);
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);

		final ObservedPropertyInstance observedPropertyInstance = dialog.getResult();
		final CalibrationSetComboBox calibrationSetComboBox = new CalibrationSetComboBox(owner, sessionFactory);
		final TableColumn calibrationSetColumn = table.getColumnModel().getColumn(5);

		calibrationSetColumn.setCellEditor(new DefaultCellEditor(calibrationSetComboBox));

		if (observedPropertyInstance != null) {
			final ObservedPropertyInstanceTableModel tableModel = (ObservedPropertyInstanceTableModel)table.getModel();

			tableModel.fireTableDataChanged();
		}
	}

	private void removeObservedPropertyInstance(ObservedPropertyInstanceTableModel tableModel, int row) {
		final List<ObservedPropertyInstance> observedPropertyInstances = tableModel.getObservedPropertyInstances();
		final ObservedPropertyInstance observedPropertyInstance = observedPropertyInstances.get(row);
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the observed property instance and all calibration sets belonging to this instance.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			for (final CalibrationSet calibrationSet: observedPropertyInstance.getCalibrationSets()) {
				session.delete(calibrationSet);
			}

			session.delete(observedPropertyInstance);
			transaction.commit();
			observedPropertyInstances.remove(row);
			tableModel.fireTableDataChanged();
		} catch (Exception exception) {
			if (transaction != null) {
				transaction.rollback();
			}

			final String[] messages = { "Observed property instance could not be deleted.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property instance could not be deleted", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		final CalibrationSetComboBox calibrationSetComboBox = new CalibrationSetComboBox(owner, sessionFactory);
		final TableColumn calibrationSetColumn = table.getColumnModel().getColumn(5);

		calibrationSetColumn.setCellEditor(new DefaultCellEditor(calibrationSetComboBox));
	}

	static boolean removeSensor(Sensor sensor, Window owner, SessionFactory sessionFactory) {
		final int confirm = JOptionPane.showConfirmDialog(owner, "This will remove the sensor, all observed property instances for this sensor and all calibration sets for those observed property instances.", "Are you sure?", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION) {
			return false;
		}

		final Session session = sessionFactory.openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();

			for (final ObservedPropertyInstance observedPropertyInstance: sensor.getObservedPropertyInstances()) {
				for (final CalibrationSet calibrationSet: observedPropertyInstance.getCalibrationSets()) {
					session.delete(calibrationSet);
				}

				session.delete(observedPropertyInstance);
			}

			session.delete(sensor);
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