package de.hzg.editor;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ParameterTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 6916898139635778985L;
	private static NumberFormat formatter = new DecimalFormat("0.00000E0");

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final double doubleValue = (Double)value;
		final int intValue = (int)doubleValue;
		final String convertedValue;

		if (intValue == doubleValue) {
			convertedValue = Integer.toString(intValue);
		} else {
			convertedValue = formatter.format(doubleValue);
		}

		return super.getTableCellRendererComponent(table, convertedValue, isSelected, hasFocus, row, column);
	}
}