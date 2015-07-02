package de.hzg.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Adder {
	public void addToPanel(JPanel panel, String buttonName, final AddListener addListener) {
		final JButton addButton = new JButton(buttonName);

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				addListener.onAdd();
			}
		});

		panel.add(addButton);
	}
}