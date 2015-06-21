package de.hzg.editor;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.hzg.common.ExceptionUtil;

public class ExceptionDialog extends JDialog {
	private static final long serialVersionUID = 6692728605150725313L;

	public ExceptionDialog(Window owner, String title, String[] messages, Exception exception) {
		super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);

		final String stackTrace = ExceptionUtil.stackTraceToString(exception);
		final JTextArea textArea = new JTextArea(6, 80);
		textArea.setText(stackTrace);
		textArea.setEditable(false);

		final JScrollPane scrollPane = new JScrollPane(textArea);
		final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				verticalScrollBar.setValue(0);
			}
		});

		final JLabel errorLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
		final JPanel errorPanel = new JPanel();
		errorPanel.add(errorLabel);

		final JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		//messagePanel.setLayout(new GridLayout(0, 1));

		for (final String currentMessage: messages) {
			final JLabel messageLabel = new JLabel(currentMessage);
			messagePanel.add(messageLabel);
		}

		final JPanel buttonPane = new JPanel();
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

		final GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		getContentPane().setLayout(layout);

		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(errorPanel, constraints);
		getContentPane().add(errorPanel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;

		layout.setConstraints(messagePanel, constraints);
		getContentPane().add(messagePanel);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(scrollPane, constraints);
		getContentPane().add(scrollPane);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		constraints.weightx = 1.0;
		constraints.weighty = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		layout.setConstraints(buttonPane, constraints);
		getContentPane().add(buttonPane);

		getRootPane().setDefaultButton(okButton);

		setResizable(true);
	}
}