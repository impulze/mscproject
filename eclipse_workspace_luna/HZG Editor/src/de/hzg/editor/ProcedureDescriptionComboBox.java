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

import de.hzg.measurement.ProcedureDescription;

public class ProcedureDescriptionComboBox extends JComboBox<ProcedureDescription> {
	private static final long serialVersionUID = 8250871093133166653L;

	public ProcedureDescriptionComboBox(Window owner, SessionFactory sessionFactory) {
		final List<ProcedureDescription> procedureDescriptions = MeasurementQueries.getProcedureDescriptions(owner, sessionFactory);

		for (final ProcedureDescription procedureDescription: procedureDescriptions) {
			addItem(procedureDescription);
		}

		setRenderer(new Renderer());
	}

	static class Renderer implements ListCellRenderer<ProcedureDescription>, TableCellRenderer {
		private static final BasicComboBoxRenderer defaultRenderer = new BasicComboBoxRenderer();
		private static final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

		private Object getRenderingObject(ProcedureDescription value) {
			final String displayText = value == null ? "" : value.getName();

			return displayText;
		}

		public Component getListCellRendererComponent(JList<? extends ProcedureDescription> list, ProcedureDescription value, int index, boolean isSelected, boolean cellHasFocus) {
			final Object renderingObject = getRenderingObject((ProcedureDescription)value);

			return defaultRenderer.getListCellRendererComponent(list, renderingObject, index, isSelected, cellHasFocus);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			final Object renderingObject = getRenderingObject((ProcedureDescription)value);

			return defaultTableCellRenderer.getTableCellRendererComponent(table, renderingObject, isSelected, hasFocus, row, column);
		}
	}
}
