package de.hzg.editor;

import java.awt.Window;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import de.hzg.common.ObservedPropertyClassesConfiguration;

public class ObservedPropertyJavaClassComboBox extends JComboBox<String> {
	private static final long serialVersionUID = 3702996956310374279L;

	public ObservedPropertyJavaClassComboBox(Window owner, ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration) {
		final List<String> names = ObservedPropertyJavaClass.listNames(observedPropertyClassesConfiguration, owner);

		if (names != null) {
			Collections.sort(names);
			for (final String name: names) {
				addItem(name);
			}
		}
	}
}
