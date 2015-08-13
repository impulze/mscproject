package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.hibernate.SessionFactory;

import de.hzg.common.ProcedureClassesConfiguration;

public class ListProcedureJavaClassesPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = -6614179124616055688L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private final ProcedureClassesConfiguration procedureClassesConfiguration;
	private AddListener addListener;
	private EditListener<ProcedureJavaClass> editListener;

	public ListProcedureJavaClassesPanel(Window owner, SessionFactory sessionFactory, ProcedureClassesConfiguration procedureClassesConfiguration) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.procedureClassesConfiguration = procedureClassesConfiguration;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List procedure classes");
		setBottomPanelTitle("Procedure classes");
		getBottomPanel().add(dataCreator.createPanel(table));
		createForm();

		setDataProvider(this);
		provide("Refresh list");
	}

	private void createForm() {
		final JButton actionButton = getActionButton("Refresh list");
		final JButton checkClassesButton = getActionButton("Check classes");
		final JButton compileClassesButton = getActionButton("Compile classes");

		final GroupLayout topPanelLayout = new GroupLayout(getTopPanel());

		final SequentialGroup horizontalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addComponent(actionButton)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(checkClassesButton)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(compileClassesButton)
			.addContainerGap(251, Short.MAX_VALUE);

		final ParallelGroup verticalButtonGroup = topPanelLayout.createParallelGroup(Alignment.BASELINE)
			.addComponent(actionButton)
			.addComponent(checkClassesButton)
			.addComponent(compileClassesButton);

		final SequentialGroup verticalLayoutWithGaps = topPanelLayout.createSequentialGroup()
			.addContainerGap()
			.addGroup(verticalButtonGroup)
			.addContainerGap();

		topPanelLayout.setHorizontalGroup(horizontalLayoutWithGaps);
		topPanelLayout.setVerticalGroup(verticalLayoutWithGaps);

		getTopPanel().setLayout(topPanelLayout);
	}

	private JTable createTable(DataCreator dataCreator) {
		final Adder adder = new Adder();
		final JPanel addProcedureClassPanel = new JPanel();

		addProcedureClassPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adder.addToPanel(addProcedureClassPanel, "Add procedure class", this);

		dataCreator.addPanel(addProcedureClassPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove procedure classes.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "procedure class");
		final String editString = String.format("Edit %s", "procedure class");
		final String removeString = String.format("Remove %s", "procedure class");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ProcedureJavaClassTableModel tableModel = (ProcedureJavaClassTableModel)table.getModel();
				final String name = tableModel.getProcedureClassNames().get(row);
				final ProcedureJavaClass procedureJavaClass = ProcedureJavaClass.loadByName(procedureClassesConfiguration, owner, name);

				onEdit(procedureJavaClass);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ProcedureJavaClassTableModel tableModel = (ProcedureJavaClassTableModel)table.getModel();
				removeProcedureJavaClass(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final ProcedureJavaClassTableModel tableModel = new ProcedureJavaClassTableModel();

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final ProcedureJavaClassTableModel tableModel = (ProcedureJavaClassTableModel)table.getModel();

		if (title.equals("Refresh list")) {
			final List<String> procedureClassNames = ProcedureJavaClass.listNames(procedureClassesConfiguration, owner);

			tableModel.setProcedureClassNames(procedureClassNames);
			tableModel.fireTableDataChanged();
			return true;
		} else if (title.equals("Compile classes")) {
			System.out.println("Compile classes");
		} else if (title.equals("Check classes")) {
			System.out.println("Check classes");
		}

		return false;
	}

	void removeProcedureJavaClass(ProcedureJavaClassTableModel tableModel, int row) {
		final List<String> procedureClassNames = tableModel.getProcedureClassNames();
		final String name = procedureClassNames.get(row);
		final ProcedureJavaClass procedureJavaClass = ProcedureJavaClass.loadByName(procedureClassesConfiguration, owner, name);
		final boolean deleted = CreateEditProcedureJavaClassPanel.removeProcedureJavaClass(procedureJavaClass, owner, sessionFactory);

		if (deleted) {
			procedureClassNames.remove(row);
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

	void setEditListener(EditListener<ProcedureJavaClass> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(ProcedureJavaClass procedureJavaClass) {
		if (editListener != null) {
			editListener.onEdit(procedureJavaClass);
		}
	}
}
