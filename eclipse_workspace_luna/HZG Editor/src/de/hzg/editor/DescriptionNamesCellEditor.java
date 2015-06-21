package de.hzg.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class DescriptionNamesCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor {
	private static final long serialVersionUID = -1596419497648007649L;
	private final List<String> descriptionNames;
	private String descriptionName;

	public DescriptionNamesCellEditor(List<String> descriptionNames) {
		this.descriptionNames = descriptionNames;
	}

	public Object getCellEditorValue() {
		return descriptionName;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		@SuppressWarnings("unchecked")
		final JComboBox<String> box = (JComboBox<String>)arg0.getSource();
		descriptionName = (String)box.getSelectedItem();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
		assert(value instanceof String);

		descriptionName = (String)value;

		final JComboBox<String> box = new JComboBox<String>();

		for (String descriptionName: descriptionNames) {
			box.addItem(descriptionName);
		}

		box.setSelectedItem(descriptionName);
		box.addActionListener(this);

		return box;
	}
}