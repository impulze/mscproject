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

import de.hzg.common.ObservedPropertyClassesConfiguration;

public class ListObservedPropertyJavaClassesPanel extends SplitPanel implements DataProvider, AddListener {
	private static final long serialVersionUID = -6614179124616055688L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private final ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration;
	private AddListener addListener;
	private EditListener<ObservedPropertyJavaClass> editListener;

	public ListObservedPropertyJavaClassesPanel(Window owner, SessionFactory sessionFactory, ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.observedPropertyClassesConfiguration = observedPropertyClassesConfiguration;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List observed property classes");
		setBottomPanelTitle("Observed property classes");
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
		final JPanel addObservedPropertyClassPanel = new JPanel();

		addObservedPropertyClassPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		adder.addToPanel(addObservedPropertyClassPanel, "Add observed property class", this);

		dataCreator.addPanel(addObservedPropertyClassPanel);

		dataCreator.addInformationMessage("Use right click to add/edit/remove observed property classes.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "observed property class");
		final String editString = String.format("Edit %s", "observed property class");
		final String removeString = String.format("Remove %s", "observed property class");

		final TablePopupMenu.ActionListener addActionListener = new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				onAdd();
			}
		};

		noCellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(addString, addActionListener);
		cellPopupMenu.addItem(editString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ObservedPropertyJavaClassTableModel tableModel = (ObservedPropertyJavaClassTableModel)table.getModel();
				final String name = tableModel.getObservedPropertyClassNames().get(row);
				final ObservedPropertyJavaClass observedPropertyJavaClass = ObservedPropertyJavaClass.loadByName(observedPropertyClassesConfiguration, owner, name);

				onEdit(observedPropertyJavaClass);
			}
		});
		cellPopupMenu.addItem(removeString, new TablePopupMenu.ActionListener() {
			public void  actionPerformed(JTable table, int row, int column, ActionEvent event) {
				final ObservedPropertyJavaClassTableModel tableModel = (ObservedPropertyJavaClassTableModel)table.getModel();
				removeObservedPropertyJavaClass(tableModel, row);
			}
		});

		dataCreator.setCellPopupMenu(-1, cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final ObservedPropertyJavaClassTableModel tableModel = new ObservedPropertyJavaClassTableModel();

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final ObservedPropertyJavaClassTableModel tableModel = (ObservedPropertyJavaClassTableModel)table.getModel();

		if (title.equals("Refresh list")) {
			final List<String> observedPropertyClassNames = ObservedPropertyJavaClass.listNames(observedPropertyClassesConfiguration, owner);

			tableModel.setObservedPropertyClassNames(observedPropertyClassNames);
			tableModel.fireTableDataChanged();
			return true;
		} else if (title.equals("Compile classes")) {
			System.out.println("Compile classes");
		} else if (title.equals("Check classes")) {
			System.out.println("Check classes");
		}

		return false;
	}

	void removeObservedPropertyJavaClass(ObservedPropertyJavaClassTableModel tableModel, int row) {
		final List<String> observedPropertyClassNames = tableModel.getObservedPropertyClassNames();
		final String name = observedPropertyClassNames.get(row);
		final ObservedPropertyJavaClass observedPropertyJavaClass = ObservedPropertyJavaClass.loadByName(observedPropertyClassesConfiguration, owner, name);
		final boolean deleted = CreateEditObservedPropertyJavaClassPanel.removeObservedPropertyJavaClass(observedPropertyJavaClass, owner, sessionFactory);

		if (deleted) {
			observedPropertyClassNames.remove(row);
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

	void setEditListener(EditListener<ObservedPropertyJavaClass> editListener) {
		this.editListener = editListener;
	}

	public void onEdit(ObservedPropertyJavaClass observedPropertyJavaClass) {
		if (editListener != null) {
			editListener.onEdit(observedPropertyJavaClass);
		}
	}
}
