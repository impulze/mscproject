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

import de.hzg.measurement.ObservedPropertyInstance;

public class ObservedPropertyInstanceComboBox extends JComboBox<ObservedPropertyInstance> {
	private static final long serialVersionUID = 831509272904128385L;

	public ObservedPropertyInstanceComboBox(Window owner, SessionFactory sessionFactory) {
		final List<ObservedPropertyInstance> observedPropertyInstances = MeasurementQueries.getObservedPropertyInstances(owner, sessionFactory);

		Collections.sort(observedPropertyInstances, new Comparator<ObservedPropertyInstance>() {
			@Override
			public int compare(ObservedPropertyInstance pi1, ObservedPropertyInstance pi2) {
				return pi1.getName().compareTo(pi2.getName());
			}
		});

		for (final ObservedPropertyInstance observedPropertyInstance: observedPropertyInstances) {
			addItem(observedPropertyInstance);
		}

		setRenderer(new Renderer());
	}

	static class Renderer implements ListCellRenderer<ObservedPropertyInstance>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		private Object getRenderingObject(ObservedPropertyInstance value) {
			final String displayText = value == null ? "" : value.getName();

			return displayText;
		}

		public Component getListCellRendererComponent(JList<? extends ObservedPropertyInstance> list, ObservedPropertyInstance value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((ObservedPropertyInstance)value);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((ObservedPropertyInstance)value);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}
