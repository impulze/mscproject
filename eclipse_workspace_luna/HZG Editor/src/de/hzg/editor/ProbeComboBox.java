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

import de.hzg.measurement.Probe;

public class ProbeComboBox extends JComboBox<Probe> {
	private static final long serialVersionUID = 723731398929179486L;

	public ProbeComboBox(Window owner, SessionFactory sessionFactory) {
		final List<Probe> probes = MeasurementQueries.getProbes(owner, sessionFactory);

		for (final Probe probe: probes) {
			addItem(probe);
		}

		setRenderer(new Renderer());
	}

	static class Renderer implements ListCellRenderer<Probe>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		private Object getRenderingObject(Probe value) {
			final String displayText = value == null ? "" : value.getName();

			return displayText;
		}

		public Component getListCellRendererComponent(JList<? extends Probe> list, Probe value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((Probe)value);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((Probe)value);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}
