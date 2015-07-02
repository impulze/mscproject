package de.hzg.editor;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class TablePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 7960127374098285815L;

	public void addItem(String itemText) {
		final JMenuItem mntmThis = new JMenuItem(itemText);

		add(mntmThis);
	}
}