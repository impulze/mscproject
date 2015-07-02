package de.hzg.editor;

import java.awt.Window;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.values.ValueData;

public class ListValuesPanel<T extends ValueData<E>, E> extends SplitPanel implements DataProvider {
	private static final long serialVersionUID = -6459975552256961051L;
	private final JTable table;
	private final Window owner;
	private final SessionFactory sessionFactory;
	private final Class<E> classType;
	private final String tableName;

	public ListValuesPanel(Window owner, SessionFactory sessionFactory, Class<E> classType, String tableName) {
		this.owner = owner;
		this.sessionFactory = sessionFactory;
		this.classType = classType;
		this.tableName = tableName;

		final DataCreator dataCreator = new DataCreator();
		table = createTable(dataCreator);
		setupTable(table);

		setBottomPanelTitle("Values");
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
		dataCreator.addInformationMessage("Click column header to sort ascending/descending.");

		return dataCreator.create();
	}

	void setupTable(JTable table) {
		final ValueTableModel<T, E> tableModel = new ValueTableModel<T, E>(owner, sessionFactory, classType);

		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
		table.getColumnModel().getColumn(2).setPreferredWidth(140);
	}

	public boolean provide(String title) {
		final Session session = sessionFactory.openSession();

		try {
			@SuppressWarnings("unchecked")
			final List<T> result = (List<T>)session
				.createQuery("FROM " + tableName)
				.list();

			@SuppressWarnings("unchecked")
			final ValueTableModel<T, E> tableModel = (ValueTableModel<T, E>)table.getModel();;

			for (final T value: result) {
				Hibernate.initialize(value.getSensorInstance());
				Hibernate.initialize(value.getSensorInstance().getSensorDescription());
				Hibernate.initialize(value.getSensorInstance().getProbe());
			}

			tableModel.setValues(result);
			tableModel.fireTableDataChanged();
			return true;
		} catch (Exception exception) {
			final String[] messages = { "Values could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Values not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} finally {
			session.close();
		}

		return false;
	}
}