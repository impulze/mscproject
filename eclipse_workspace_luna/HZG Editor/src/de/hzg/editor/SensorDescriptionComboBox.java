package de.hzg.editor;

import java.awt.Component;
import java.awt.Window;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.hibernate.SessionFactory;

import de.hzg.measurement.SensorDescription;

public class SensorDescriptionComboBox extends JComboBox<SensorDescription> {
	private static final long serialVersionUID = 8250871093133166653L;

	public SensorDescriptionComboBox(Window owner, SessionFactory sessionFactory) {
		final List<SensorDescription> sensorDescriptions = MeasurementQueries.getSensorDescriptions(owner, sessionFactory);

		for (final SensorDescription sensorDescription: sensorDescriptions) {
			addItem(sensorDescription);
		}

		setRenderer(new Renderer());
	}

	static class Renderer implements ListCellRenderer<SensorDescription>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		private Object getRenderingObject(SensorDescription value) {
			final String displayText = value == null ? "" : value.getName();

			return displayText;
		}

		public Component getListCellRendererComponent(JList<? extends SensorDescription> list, SensorDescription value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((SensorDescription)value);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((SensorDescription)value);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}
