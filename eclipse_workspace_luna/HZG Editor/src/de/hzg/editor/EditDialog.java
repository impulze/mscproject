package de.hzg.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import javax.swing.JLabel;


public abstract class EditDialog<T> extends JDialog {
	private static final long serialVersionUID = 8506257554432775780L;
	private final JTextField nameTextField;
	private final JButton okButton;

	public EditDialog(Window owner, String title, String label) {
		super(owner, title);
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().setLayout(new BorderLayout());
		final JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			final JLabel lblEditName = new JLabel(label + ":");
			contentPanel.add(lblEditName);
		}
		{
			nameTextField = new JTextField();
			contentPanel.add(nameTextField);
			nameTextField.setColumns(10);
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void setOKListener(ActionListener okListener) {
		for (final ActionListener actionListener: okButton.getActionListeners()) {
			okButton.removeActionListener(actionListener);
		}

		okButton.addActionListener(okListener);
	}

	protected JTextField getNameTextField() {
		return nameTextField;
	}

	protected abstract T getResult();
}