package de.hzg.editor;

import java.awt.Window;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import de.hzg.common.ProcedureClassesConfiguration;

public class ProcedureJavaClassComboBox extends JComboBox<String> {
	private static final long serialVersionUID = 3702996956310374279L;

	public ProcedureJavaClassComboBox(Window owner, ProcedureClassesConfiguration procedureClassesConfiguration) {
		final List<String> names = ProcedureJavaClass.listNames(procedureClassesConfiguration, owner);

		if (names != null) {
			Collections.sort(names);
			for (final String name: names) {
				addItem(name);
			}
		}

		// TODO: fix this once all classes are implemeneted
		setEditable(true);
	}
}
