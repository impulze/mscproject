package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jdesktop.swingx.table.DatePickerCellEditor;

import de.hzg.measurement.CalibrationSet;

public class ListCalibrationSetsPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = -2019106068966461378L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private AddListener addListener;
	private EditListener<CalibrationSet> editListener;

	public ListCalibrationSetsPanel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List calibration sets");
		setBottomPanelTitle("Calibration sets");
		getBottomPanel().add(dataCreator.createPanel(table));
		createForm();

		setDataProvider(this);
		provide("Refresh list");
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Refresh list");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final SequentialGroup horizontalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(actionButton)
			.addContainerGap(251, Short.MAX_VALUE);

		final SequentialGroup verticalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(actionButton)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTable createTable(DataCreator dataCreator) {
		final Adder adder = new Adder();
		final JPanel addSensorPanel = new JPanel();

		addSensorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adder.addToPanel(addSensorPanel, "Add calibration set", this);

		dataCreator.addPanel(addSensorPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove calibration sets.");
		dataCreator.addInformationMessage("Use double left click to change calibration set values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "calibration set");
		final String editString = String.format("Edit %s", "calibration set");
		final String removeString = String.format("Remove %s", "calibration set");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final CalibrationSetTableModel tableModel = (CalibrationSetTableModel)table.getModel();
				final CalibrationSet calibrationSet = tableModel.getCalibrationSets().get(row);

				onEdit(calibrationSet);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final CalibrationSetTableModel tableModel = (CalibrationSetTableModel)table.getModel();
				removeCalibrationSet(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(-1, cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final CalibrationSetTableModel tableModel = new CalibrationSetTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(0).setCellEditor(new DatePickerCellEditor());
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setCellEditor(new DatePickerCellEditor());
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setCellRenderer(new ObservedPropertyInstanceComboBox.Renderer());

		for (int i = 0; i < 6; i++) {
			table.getColumnModel().getColumn(3 + i).setPreferredWidth(90);
			table.getColumnModel().getColumn(3 + i).setCellRenderer(new ParameterTableCellRenderer());
		}
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<CalibrationSet> result = (List<CalibrationSet>)session
				.createQuery("FROM CalibrationSet")
				.list();

			for (final CalibrationSet calibrationSet: result) {
				calibrationSet.initCalibrationSet();
			}

			final CalibrationSetTableModel tableModel = (CalibrationSetTableModel)table.getModel();;

			final ObservedPropertyInstanceComboBox observedPropertyInstanceComboBox = new ObservedPropertyInstanceComboBox(owner, sessionFactory);
			table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(observedPropertyInstanceComboBox));

			tableModel.setCalibrationSets(result);
			tableModel.fireTableDataChanged();
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Calibration sets could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Calibration sets not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	void removeCalibrationSet(CalibrationSetTableModel tableModel, int row) {
		final List<CalibrationSet> calibrationSets = tableModel.getCalibrationSets();
		final CalibrationSet calibrationSet = calibrationSets.get(row);
		final boolean deleted = CreateEditCalibrationSetPanel.removeCalibrationSet(calibrationSet, owner, sessionFactory);

		if (deleted) {
			final ObservedPropertyInstanceComboBox observedPropertyInstanceComboBox = new ObservedPropertyInstanceComboBox(owner, sessionFactory);
			table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(observedPropertyInstanceComboBox));

			calibrationSets.remove(row);
			tableModel.fireTableDataChanged();
		}
	}

	void setAddListener(AddListener addListener) {
		this.addListener = addListener;
	}

	public void onAdd() {
		if (addListener != null) {
			addListener.onAdd();
		}
	}

	void setEditListener(EditListener<CalibrationSet> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(CalibrationSet calibrationSet) {
		if (editListener != null) {
			editListener.onEdit(calibrationSet);
		}
	}
}