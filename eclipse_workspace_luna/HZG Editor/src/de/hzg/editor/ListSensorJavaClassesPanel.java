package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.hibernate.SessionFactory;

import de.hzg.common.SensorClassesConfiguration;

public class ListSensorJavaClassesPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = -6614179124616055688L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private final SensorClassesConfiguration sensorClassesConfiguration;
	private AddListener addListener;
	private EditListener<SensorJavaClass> editListener;

	public ListSensorJavaClassesPanel(Window owner, SessionFactory sessionFactory, SensorClassesConfiguration sensorClassesConfiguration) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.sensorClassesConfiguration = sensorClassesConfiguration;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List sensor classes");
		setBottomPanelTitle("Sensor classes");
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
		final JPanel addSensorClassPanel = new JPanel();

		addSensorClassPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adder.addToPanel(addSensorClassPanel, "Add sensor class", this);

		dataCreator.addPanel(addSensorClassPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove sensor classes.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "sensor class");
		final String editString = String.format("Edit %s", "sensor class");
		final String removeString = String.format("Remove %s", "sensor class");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final SensorJavaClassTableModel tableModel = (SensorJavaClassTableModel)table.getModel();
				final String name = tableModel.getSensorClassNames().get(row);
				final SensorJavaClass sensorJavaClass = SensorJavaClass.loadByName(sensorClassesConfiguration, owner, name);

				onEdit(sensorJavaClass);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final SensorJavaClassTableModel tableModel = (SensorJavaClassTableModel)table.getModel();
				removeSensorJavaClass(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final SensorJavaClassTableModel tableModel = new SensorJavaClassTableModel();

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final SensorJavaClassTableModel tableModel = (SensorJavaClassTableModel)table.getModel();

		if (title.equals("Refresh list")) {
			final List<String> sensorClassNames = SensorJavaClass.listNames(sensorClassesConfiguration, owner);

			tableModel.setSensorClassNames(sensorClassNames);
			tableModel.fireTableDataChanged();
			return true;
		}

		return false;
	}

	void removeSensorJavaClass(SensorJavaClassTableModel tableModel, int row) {
		final List<String> sensorClassNames = tableModel.getSensorClassNames();
		final String name = sensorClassNames.get(row);
		final SensorJavaClass sensorJavaClass = SensorJavaClass.loadByName(sensorClassesConfiguration, owner, name);
		final boolean deleted = CreateEditSensorJavaClassPanel.removeSensorJavaClass(sensorJavaClass, owner, sessionFactory);

		if (deleted) {
			sensorClassNames.remove(row);
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

	void setEditListener(EditListener<SensorJavaClass> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(SensorJavaClass sensorJavaClass) {
		if (editListener != null) {
			editListener.onEdit(sensorJavaClass);
		}
	}
}