package de.hzg.editor;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ParameterTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 6916898139635778985L;
	
	public ParameterTableCellRenderer() {
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}