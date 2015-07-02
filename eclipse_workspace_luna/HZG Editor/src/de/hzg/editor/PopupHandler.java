package de.hzg.editor;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public interface PopupHandler {
	JPopupMenu getComponentPopupMenu(JComponent source);
	Point getPopupLocation(JComponent source, MouseEvent arg0);
}