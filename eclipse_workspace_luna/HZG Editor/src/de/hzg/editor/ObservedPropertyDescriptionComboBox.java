package de.hzg.editor;

import java.awt.Component;
import java.awt.Window;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.hibernate.SessionFactory;

import de.hzg.measurement.ObservedPropertyDescription;

public class ObservedPropertyDescriptionComboBox extends JComboBox<ObservedPropertyDescription> {
	private static final long serialVersionUID = 8250871093133166653L;

	public ObservedPropertyDescriptionComboBox(Window owner, SessionFactory sessionFactory) {
		final List<ObservedPropertyDescription> observedPropertyDescriptions = MeasurementQueries.getObservedPropertyDescriptions(owner, sessionFactory);

		Collections.sort(observedPropertyDescriptions, new Comparator<ObservedPropertyDescription>() {
			@Override
			public int compare(ObservedPropertyDescription pd1, ObservedPropertyDescription pd2) {
				return pd1.getName().compareTo(pd2.getName());
			}
		});

		for (final ObservedPropertyDescription observedPropertyDescription: observedPropertyDescriptions) {
			addItem(observedPropertyDescription);
		}

		setRenderer(new Renderer());
	}

	static class Renderer implements ListCellRenderer<ObservedPropertyDescription>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		private Object getRenderingObject(ObservedPropertyDescription value) {
			final String displayText = value == null ? "" : value.getName();

			return displayText;
		}

		public Component getListCellRendererComponent(JList<? extends ObservedPropertyDescription> list, ObservedPropertyDescription value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((ObservedPropertyDescription)value);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((ObservedPropertyDescription)value);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}
