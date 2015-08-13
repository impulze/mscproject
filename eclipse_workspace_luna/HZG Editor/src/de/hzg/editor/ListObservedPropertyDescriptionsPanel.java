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

import de.hzg.measurement.ObservedPropertyDescription;

public class ListObservedPropertyDescriptionsPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = -2140811495389781461L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private AddListener addListener;
	private EditListener<ObservedPropertyDescription> editListener;

	public ListObservedPropertyDescriptionsPanel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List observed property descriptions");
		setBottomPanelTitle("Observed property descriptions");
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
		adder.addToPanel(addSensorPanel, "Add observed property description", this);

		dataCreator.addPanel(addSensorPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove observed property descriptions.");
		dataCreator.addInformationMessage("Use double left click to change observed property description values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "observed property description");
		final String editString = String.format("Edit %s", "observed property description");
		final String removeString = String.format("Remove %s", "observed property description");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ObservedPropertyDescriptionTableModel tableModel = (ObservedPropertyDescriptionTableModel)table.getModel();
				final ObservedPropertyDescription observedPropertyDescription = tableModel.getObservedPropertyDescriptions().get(row);

				onEdit(observedPropertyDescription);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ObservedPropertyDescriptionTableModel tableModel = (ObservedPropertyDescriptionTableModel)table.getModel();
				removeObservedPropertyDescription(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(-1, cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final ObservedPropertyDescriptionTableModel tableModel = new ObservedPropertyDescriptionTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<ObservedPropertyDescription> result = (List<ObservedPropertyDescription>)session
				.createQuery("FROM ObservedPropertyDescription")
				.list();

			for (final ObservedPropertyDescription observedPropertyDescription: result) {
				observedPropertyDescription.initObservedPropertyDescription();
			}

			final ObservedPropertyDescriptionTableModel tableModel = (ObservedPropertyDescriptionTableModel)table.getModel();;

			tableModel.setObservedPropertyDescriptions(result);
			tableModel.fireTableDataChanged();
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Observed property descriptions could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Observed property descriptions not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	void removeObservedPropertyDescription(ObservedPropertyDescriptionTableModel tableModel, int row) {
		final List<ObservedPropertyDescription> observedPropertyDescriptions = tableModel.getObservedPropertyDescriptions();
		final ObservedPropertyDescription observedPropertyDescription = observedPropertyDescriptions.get(row);
		final boolean deleted = CreateEditObservedPropertyDescriptionPanel.removeObservedPropertyDescription(observedPropertyDescription, owner, sessionFactory);

		if (deleted) {
			observedPropertyDescriptions.remove(row);
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

	void setEditListener(EditListener<ObservedPropertyDescription> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(ObservedPropertyDescription observedPropertyDescription) {
		if (editListener != null) {
			editListener.onEdit(observedPropertyDescription);
		}
	}
}