package de.hzg.editor;

import java.awt.Window;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import de.hzg.common.SensorClassesConfiguration;

public class SensorJavaClassComboBox extends JComboBox<String> {
	private static final long serialVersionUID = 3702996956310374279L;

	public SensorJavaClassComboBox(Window owner, SensorClassesConfiguration sensorClassesConfiguration) {
		final List<String> names = SensorJavaClass.listNames(sensorClassesConfiguration, owner);

		if (names != null) {
			Collections.sort(names);
			for (final String name: names) {
				addItem(name);
			}
		}
	}
}
