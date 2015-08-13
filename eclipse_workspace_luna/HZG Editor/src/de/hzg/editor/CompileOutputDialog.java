package de.hzg.editor;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class CompileOutputDialog extends JDialog {
	private static final long serialVersionUID = -4358152972730995911L;
	private final GridBagLayout layout;
	private JLabel currentLabel;
	private JPanel buttonPane;
	private int labelLine;
	private int currentLine;
	private JTextArea currentTextArea;
	private String currentPath;
	private String currentFileName;

	public CompileOutputDialog(Window owner) {
		super(owner, "Output during compilation of procedure class(es)", Dialog.ModalityType.APPLICATION_MODAL);

		final JLabel topLabel = new JLabel("The following output was created during compilation of the files:\n");
		final JPanel topPanel = new JPanel();
		topPanel.add(topLabel);

		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				setVisible(false);
			}
		});

		buttonPane.add(okButton);

		layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		getContentPane().setLayout(layout);

		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		layout.setConstraints(topPanel, constraints);
		getContentPane().add(topPanel);

		getRootPane().setDefaultButton(okButton);

		setResizable(true);

		currentLine = 1;
	}

	private void setLabel(Icon icon) {
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;

		if (currentLabel == null) {
			currentLabel = new JLabel(icon);
			labelLine = currentLine;
		} else {
			layout.removeLayoutComponent(currentLabel);
			getContentPane().remove(currentLabel);
			currentLabel = new JLabel(icon);
		}

		constraints.gridx = 0;
		constraints.gridy = labelLine;
		constraints.gridheight = currentLine + 1 - labelLine;

		layout.setConstraints(currentLabel,  constraints);
		getContentPane().add(currentLabel);
	}

	public void newTask(String name) {
		final JLabel nameLabel = new JLabel(name + ":");
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = currentLine;
		constraints.gridwidth = 2;

		layout.setConstraints(nameLabel, constraints);
		getContentPane().add(nameLabel);

		currentLine++;
		currentLabel = null;
		setLabel(UIManager.getIcon("OptionPane.informationIcon"));

		constraints.gridx = 1;
		constraints.gridy = currentLine;
		constraints.gridwidth = 1;

		currentTextArea = new JTextArea();
		currentTextArea.setEditable(false);
		final JScrollPane scrollPane = new JScrollPane(currentTextArea);
		layout.setConstraints(scrollPane, constraints);
		getContentPane().add(scrollPane);
		currentLine++;

		currentPath = name;
		currentFileName = Paths.get(name).getFileName().toString();

		getContentPane().revalidate();
		pack();
	}

	public void append(String message) {
		if (message.length() > currentPath.length() + 1) {
			final String messageWithoutPath = message.substring(currentPath.length());
			currentTextArea.append(currentFileName + messageWithoutPath + "\n");
		} else {
			currentTextArea.append(message + "\n");
		}

		getContentPane().revalidate();
		pack();
	}

	public void finishedTask(boolean result) {
		if (!result) {
			setLabel(UIManager.getIcon("OptionPane.errorIcon"));
		}

		getContentPane().revalidate();
		pack();
	}

	public void finishedAll() {
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = currentLine + 1;
		constraints.gridwidth = 2;

		layout.setConstraints(buttonPane, constraints);
		getContentPane().add(buttonPane);

		getContentPane().revalidate();
		pack();
	}
}
