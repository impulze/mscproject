package de.hzg.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.hibernate.SessionFactory;

import de.hzg.measurement.CalibrationSet;

public class CalibrationSetComboBox extends JComboBox<CalibrationSet> {
	private static final long serialVersionUID = -5097795582557172860L;
	private boolean layingOut = false;

	public CalibrationSetComboBox(Window owner, SessionFactory sessionFactory) {
		final List<CalibrationSet> calibrationSets = MeasurementQueries.getCalibrationSets(owner, sessionFactory);

		for (final CalibrationSet calibrationSet: calibrationSets) {
			addItem(calibrationSet);
		}

		setRenderer(new Renderer(true));
	}

	@Override
	public void doLayout() {
		try {
			layingOut = true;
			super.doLayout();
		} finally {
			layingOut = false;
		}
	}

	@Override
	public Dimension getSize() {
		final Dimension dimension = super.getSize();

		if (!layingOut) {
			dimension.width = Math.max(dimension.width, 600);
		}

		return dimension;
	}

	static class Renderer implements ListCellRenderer<CalibrationSet>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		private final SimpleDateFormat dateFormatter;
		private final boolean showFullDates;

		Renderer(boolean showFullDates) {
			this.showFullDates = showFullDates;

			if (showFullDates) {
				dateFormatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			} else {
				dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
			}
		}

		private Object getRenderingObject(CalibrationSet value, boolean isSelected) {
			if (value == null) {
				return "";
			}

			final String observedPropertyInstanceName = value.getObservedPropertyInstance().getName();
			final String displayText;

			if (!showFullDates) {
				final String startDate = dateFormatter.format(value.getValidStart());
				displayText = startDate + " (" + observedPropertyInstanceName + ")";
			} else {
				final String startDate = dateFormatter.format(value.getValidStart());
				final String endDate = dateFormatter.format(value.getValidEnd());

				displayText = startDate + " - "  + endDate + " (" + observedPropertyInstanceName + ")";
			}

			return displayText;
		}

		@Override	
		public Component getListCellRendererComponent(JList<? extends CalibrationSet> list, CalibrationSet value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((CalibrationSet)value, isSelected);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((CalibrationSet)value, isSelected);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}