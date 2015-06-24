package de.hzg.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.sensors.Probe;

public class ListProbesPanel extends ListPanel {
	private static final long serialVersionUID = 3643030501235973505L;
	private final JTable table;
	private final JPopupMenu popupMenu = createPopupMenu();
	private final JPopupMenu popupMenuFull = createPopupMenuFull();
	private int currentRowForPopup = -1;

	public ListProbesPanel(Window owner, SessionFactory sessionFactory) {
		super("List of probes", owner, sessionFactory);

		table = createTable(getBottomPanel());
		getList();
	}

	protected JTable createTable(JPanel panelToAddTo) {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		final JPanel gridLabel1 = makeGridLabel("Use right click to add/edit/remove probes.");
		// TODO: allow this?!
		//final JPanel gridLabel2 = makeGridLabel("Use left click(s) to change probe values.");
		final JPanel gridLabel3 = makeGridLabel("Click column header to sort ascending/descending.");
		final JPanel gridLabelIcon1 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));
		//final JPanel gridLabelIcon2 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));
		final JPanel gridLabelIcon3 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));

		final JScrollPane scrollPane = new JScrollPane();
		final ProbeTableModel tableModel = new ProbeTableModel(getOwner(), getSessionFactory());
		final JTable table = new JTable() {
			private static final long serialVersionUID = -9099397447709448974L;

			@Override
			public JPopupMenu getComponentPopupMenu() {
				if (currentRowForPopup >= 0) {
					return popupMenuFull;
				} else {
					return popupMenu;
				}
			}

			@Override
			public Point getPopupLocation(MouseEvent arg0) {
				final JTable sourceTable = (JTable)arg0.getSource();
				final int row = sourceTable.rowAtPoint(arg0.getPoint());

				currentRowForPopup = row;

				return super.getPopupLocation(arg0);
			}
		};

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setModel(tableModel);
		table.setRowHeight(25);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);

		table.setAutoCreateRowSorter(true);

		scrollPane.setViewportView(table);

		panelToAddTo.setLayout(layout);

		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon1, constraints);
		panelToAddTo.add(gridLabelIcon1);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel1, constraints);
		panelToAddTo.add(gridLabel1);

		/*
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon2, constraints);
		panelToAddTo.add(gridLabelIcon2);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel2, constraints);
		panelToAddTo.add(gridLabel2);
		*/

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon3, constraints);
		panelToAddTo.add(gridLabelIcon3);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel3, constraints);
		panelToAddTo.add(gridLabel3);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		layout.setConstraints(scrollPane, constraints);
		panelToAddTo.add(scrollPane);

		return table;
	}

	protected void getList() {
		final Session session = getSessionFactory().openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<Probe> result = (List<Probe>)session
				.createQuery("FROM Probe")
				.list();

			for (final Probe probe: result) {
				Probe.initProbe(probe);
			}

			final ProbeTableModel tableModel = (ProbeTableModel)table.getModel();
			tableModel.setProbes(result);
			tableModel.fireTableDataChanged();
		} catch (Exception exception) {
			final String[] messages = { "Probes could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(getOwner(), "Probes not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(getOwner());
			dialog.setVisible(true);
		} finally {
			session.close();
		}
	}

	private static JPopupMenu createPopupMenu() {
		final JPopupMenu popupMenu = new JPopupMenu();
		final JMenuItem mntmAddSensor = new JMenuItem("Add probe");

		popupMenu.add(mntmAddSensor);

		return popupMenu;
	}

	private static JPopupMenu createPopupMenuFull() {
		final JPopupMenu popupMenu = createPopupMenu();
		final JMenuItem mntmRemoveSensor = new JMenuItem("Remove probe");
		final JMenuItem mntmEditSensor = new JMenuItem("Edit probe");

		popupMenu.add(mntmRemoveSensor);
		popupMenu.add(mntmEditSensor);

		return popupMenu;
	}
}