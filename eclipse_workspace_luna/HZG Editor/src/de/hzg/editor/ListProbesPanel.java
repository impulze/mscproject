package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.measurement.Probe;

public class ListProbesPanel extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = 3643030501235973505L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;

	public ListProbesPanel(Window owner, SessionFactory sessionFactory) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setTitle("List probes");
		setBottomPanelTitle("Probes");
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
		dataCreator.addInformationMessage("Use right click to add/edit/remove probes.");
		dataCreator.addInformationMessage("Use double left click to change probe values.");
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		final TablePopupMenu noCellPopupMenu = new TablePopupMenu();
		final TablePopupMenu cellPopupMenu = new TablePopupMenu();
		final String addString = String.format("Add %s", "probe");
		final String editString = String.format("Edit %s", "probe");
		final String removeString = String.format("Remove %s", "probe");

		noCellPopupMenu.addItem(addString, null);
		cellPopupMenu.addItem(addString, null);
		cellPopupMenu.addItem(editString, null);
		cellPopupMenu.addItem(removeString, null);

		dataCreator.setCellPopupMenu(cellPopupMenu);
		dataCreator.setNoCellPopupMenu(noCellPopupMenu);

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final ProbeTableModel tableModel = new ProbeTableModel(owner, sessionFactory);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<Probe> result = (List<Probe>)session
				.createQuery("FROM Probe")
				.list();

			for (final Probe probe: result) {
				Probe.initProbe(probe);
			}

			final ProbeTableModel tableModel = (ProbeTableModel)table.getModel();;

			tableModel.setProbes(result);
			tableModel.fireTableDataChanged();
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Probes could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Probes not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}
}