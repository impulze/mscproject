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

import de.hzg.measurement.Sensor;

public class SensorComboBox extends JComboBox<Sensor> {
	private static final long serialVersionUID = 723731398929179486L;

	public SensorComboBox(Window owner, SessionFactory sessionFactory) {
		final List<Sensor> sensors = MeasurementQueries.getSensors(owner, sessionFactory);

		for (final Sensor sensor: sensors) {
			addItem(sensor);
		}

		setRenderer(new Renderer());
	}

	static class Renderer implements ListCellRenderer<Sensor>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		private Object getRenderingObject(Sensor value) {
			final String displayText = value == null ? "" : value.getName();

			return displayText;
		}

		public Component getListCellRendererComponent(JList<? extends Sensor> list, Sensor value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((Sensor)value);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((Sensor)value);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}
