package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.Sensor;

public class ListSensorsPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = 3643030501235973505L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private AddListener addListener;
	private EditListener<Sensor> editListener;

	public ListSensorsPanel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List sensors");
		setBottomPanelTitle("Sensors");
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
		adder.addToPanel(addSensorPanel, "Add sensor", this);

		dataCreator.addPanel(addSensorPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove sensors.");
		dataCreator.addInformationMessage("Use double left click to change sensor values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "sensor");
		final String editString = String.format("Edit %s", "sensor");
		final String removeString = String.format("Remove %s", "sensor");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final SensorTableModel tableModel = (SensorTableModel)table.getModel();
				final Sensor sensor = tableModel.getSensors().get(row);

				onEdit(sensor);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final SensorTableModel tableModel = (SensorTableModel)table.getModel();
				removeSensor(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final SensorTableModel tableModel = new SensorTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<Sensor> result = (List<Sensor>)session
				.createQuery("FROM Sensor")
				.list();

			for (final Sensor sensor: result) {
				sensor.initSensor();
			}

			final SensorTableModel tableModel = (SensorTableModel)table.getModel();;

			tableModel.setSensors(result);
			tableModel.fireTableDataChanged();
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Sensors could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Sensors not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	void removeSensor(SensorTableModel tableModel, int row) {
		final List<Sensor> sensors = tableModel.getSensors();
		final Sensor sensor = sensors.get(row);
		final boolean deleted = CreateEditSensorPanel.removeSensor(sensor, owner, sessionFactory);

		if (deleted) {
			sensors.remove(row);
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

	void setEditListener(EditListener<Sensor> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(Sensor sensor) {
		if (editListener != null) {
			editListener.onEdit(sensor);
		}
	}
}