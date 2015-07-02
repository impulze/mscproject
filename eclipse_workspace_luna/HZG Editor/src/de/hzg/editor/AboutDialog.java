package de.hzg.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1262143188636233073L;
	private final Window owner;

	public AboutDialog(Window owner) {
		super(owner, "About HZG Editor", Dialog.ModalityType.APPLICATION_MODAL);

		this.owner = owner;

		final String messageString = String.format(
			"<html><p>" +
			"This editor belongs to a software that was developed for " +
			"Helmholtz Zentrum Geesthacht (HZG) during the summer course at " +
			"Hochschule RheinMain by Daniel Mierswa.<br>" +
			"The project in which the software was written was supported by " +
			"Gisbert Breitbach at HZG.<br><br>" +
			"The purpose of this editor is to set data that is required for " +
			"the parts of the software that collects data from oceanic probes and " +
			"which is able to distribute them to HZG and " +
			"which can make the data available via SOS.<br><br>" +
			"If you need assistance please check the Handbook first.<br>"+
			"</p></html>");
		final JLabel messageLabel = new JLabel(messageString);

		final JLabel[][] addLabels = new JLabel[5][];

		for (int i = 0; i < addLabels.length; i++) {
			addLabels[i] = new JLabel[2];
		}

		addLabels[0][0] = new JLabel("Daniel Mierswa");
		addLabels[0][1] = new JLabel();
		addLabels[0][0].setLabelFor(addLabels[0][1]);
		sendMail(addLabels[0][1], "daniel.b.mierswa@student.hs-rm.de");

		addLabels[1][0] = new JLabel("Gisbert Breitbach");
		addLabels[1][1] = new JLabel();
		addLabels[1][0].setLabelFor(addLabels[1][1]);
		sendMail(addLabels[1][1], "Gisbert.Breitbach@hzg.de");

		addLabels[2][0] = new JLabel("Helmholtz Zentrum Geesthacht");
		addLabels[2][1] = new JLabel();
		addLabels[2][0].setLabelFor(addLabels[2][1]);
		goWebsite(addLabels[2][1], "www.hzg.de");

		addLabels[3][0] = new JLabel("Hochschule RheinMain");
		addLabels[3][1] = new JLabel();
		addLabels[3][0].setLabelFor(addLabels[3][1]);
		goWebsite(addLabels[3][1], "www.hs-rm.de");

		addLabels[4][0] = new JLabel("Version");
		addLabels[4][1] = new JLabel("0.3 (02.07.2015)");
		addLabels[4][0].setLabelFor(addLabels[4][1]);


		final JLabel infoLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));

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
		layout.setConstraints(infoLabel, constraints);
		getContentPane().add(infoLabel);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;

		layout.setConstraints(messageLabel, constraints);
		getContentPane().add(messageLabel);

		for (int i = 0; i < addLabels.length; i++) {
			for (int j = 0; j < addLabels[i].length; j++) {
				constraints.gridx = j;
				constraints.gridy = 1 + i;
				constraints.weightx = 0;
				constraints.weighty = 0;
				constraints.gridwidth = 1;
				constraints.gridheight = 1;

				layout.setConstraints(addLabels[i][j], constraints);
				getContentPane().add(addLabels[i][j]);
			}
		}

		constraints.gridx = 0;
		constraints.gridy = addLabels.length + 2;
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

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600, 500);
	}

	private void goWebsite(JLabel website, String url) {
		final String usedURL = url;
		website.setCursor(new Cursor(Cursor.HAND_CURSOR));
		website.setText(url);
		website.setForeground(Color.BLUE);
		website.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://" + usedURL));
				} catch (URISyntaxException | IOException exception) {
					final String[] messages = { "Unable to follow HTTP link.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(owner, "Unable to follow HTTP link", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				}
			}
		});
	}

	private void sendMail(JLabel contact, String email) {
		contact.setCursor(new Cursor(Cursor.HAND_CURSOR));
		contact.setText(email);
		contact.setForeground(Color.BLUE);
		contact.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().mail(new URI("mailto:YourEmailAddress@gmail.com?subject=TEST"));
				} catch (URISyntaxException | IOException exception) {
					final String[] messages = { "Unable to follow E-Mail link.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(owner, "Unable to follow E-Mail link", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				}
			}
		});
	}
}