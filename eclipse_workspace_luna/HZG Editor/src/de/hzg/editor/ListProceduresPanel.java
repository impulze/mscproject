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

import de.hzg.measurement.ProcedureDescription;

public class ListProceduresPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = -2140811495389781461L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private AddListener addListener;
	private EditListener<ProcedureDescription> editListener;

	public ListProceduresPanel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List procedures");
		setBottomPanelTitle("Procedures");
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
		adder.addToPanel(addSensorPanel, "Add procedure", this);

		dataCreator.addPanel(addSensorPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove procedures.");
		dataCreator.addInformationMessage("Use double left click to change procedure values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "procedure");
		final String editString = String.format("Edit %s", "procedure");
		final String removeString = String.format("Remove %s", "procedure");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ProcedureDescriptionTableModel tableModel = (ProcedureDescriptionTableModel)table.getModel();
				final ProcedureDescription procedureDescription = tableModel.getProcedureDescriptions().get(row);

				onEdit(procedureDescription);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ProcedureDescriptionTableModel tableModel = (ProcedureDescriptionTableModel)table.getModel();
				removeProcedureDescription(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final ProcedureDescriptionTableModel tableModel = new ProcedureDescriptionTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<ProcedureDescription> result = (List<ProcedureDescription>)session
				.createQuery("FROM ProcedureDescription")
				.list();

			for (final ProcedureDescription procedureDescription: result) {
				procedureDescription.initProcedureDescription();
			}

			final ProcedureDescriptionTableModel tableModel = (ProcedureDescriptionTableModel)table.getModel();;

			tableModel.setProcedureDescriptions(result);
			tableModel.fireTableDataChanged();
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Procedures could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedures not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}

	void removeProcedureDescription(ProcedureDescriptionTableModel tableModel, int row) {
		final List<ProcedureDescription> procedureDescriptions = tableModel.getProcedureDescriptions();
		final ProcedureDescription procedureDescription = procedureDescriptions.get(row);
		final boolean deleted = CreateEditProcedurePanel.removeProcedureDescription(procedureDescription, owner, sessionFactory);

		if (deleted) {
			procedureDescriptions.remove(row);
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

	void setEditListener(EditListener<ProcedureDescription> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(ProcedureDescription procedureDescription) {
		if (editListener != null) {
			editListener.onEdit(procedureDescription);
		}
	}
}