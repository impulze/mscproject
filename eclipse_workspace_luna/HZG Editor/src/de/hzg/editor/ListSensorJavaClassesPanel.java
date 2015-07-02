package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JTable;

import de.hzg.common.SensorClassesConfiguration;

public class ListSensorJavaClassesPanel extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = -6614179124616055688L;
	private final JTable table;
	private final Window owner;
	private final SensorClassesConfiguration sensorClassesConfiguration;

	public ListSensorJavaClassesPanel(Window owner,SensorClassesConfiguration sensorClassesConfiguration) {
		this.owner = owner;
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
		dataCreator.addInformationMessage("Use right click to add/edit/remove sensor classes.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "sensor class");
		final String editString = String.format("Edit %s", "sensor class");
		final String removeString = String.format("Remove %s", "sensor class");

		noCellPopupMenu.addItem(addString);
		cellPopupMenu.addItem(addString);
		cellPopupMenu.addItem(editString);
		cellPopupMenu.addItem(removeString);

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final SensorClassTableModel tableModel = new SensorClassTableModel();

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final SensorClassTableModel tableModel = (SensorClassTableModel)table.getModel();

		if (title.equals("Refresh list")) {
			final List<String> sensorClassNames = SensorJavaClass.listNames(sensorClassesConfiguration, owner);

			tableModel.setSensorClassNames(sensorClassNames);
			tableModel.fireTableDataChanged();
			return true;
		}

		return false;
	}
}