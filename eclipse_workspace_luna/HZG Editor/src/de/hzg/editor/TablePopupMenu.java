package de.hzg.editor;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TablePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 7960127374098285815L;
	private int currentRow = -1;
	private int currentColumn = -1;
	private JTable table;

	public void addItem(String itemText, final ActionListener actionListener) {
		final JMenuItem mntmThis = new JMenuItem(itemText);

		mntmThis.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (actionListener != null) {
					actionListener.actionPerformed(table, currentRow, currentColumn, event);
				}
			}
		});

		add(mntmThis);
	}

	public void setCurrentRow(int currentRow) {
		this.currentRow = currentRow;
	}

	public void setCurrentColumn(int currentColumn) {
		this.currentColumn = currentColumn;
	}

	public void setCurrentTable(JTable table) {
		this.table = table;
	}

	interface ActionListener {
		void actionPerformed(JTable table, int row, int column, ActionEvent event);
	}
}