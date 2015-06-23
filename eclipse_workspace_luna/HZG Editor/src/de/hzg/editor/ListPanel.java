package de.hzg.editor;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.border.LineBorder;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListPanel extends JPanel {
	private static final long serialVersionUID = -6652800387557722375L;
	private final JPanel topPanel;
	private final JPanel bottomPanel;
	private final JButton actionButton;
	private final Window owner;
	private final SessionFactory sessionFactory;

	public ListPanel(String borderTitle, Window owner, SessionFactory sessionFactory) {
		setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), borderTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));

		this.owner = owner;
		this.sessionFactory = sessionFactory;

		actionButton = new JButton();

		final JPanel wholePanel = new JPanel();
		final GridBagLayout wholePanelLayout = new GridBagLayout();
		wholePanelLayout.rowWeights = new double[]{0.0, 1.0};
		wholePanel.setLayout(wholePanelLayout);

		topPanel = new JPanel();
		final GridBagConstraints topPanelLayout = new GridBagConstraints();
		topPanelLayout.weightx = 1.0;
		topPanelLayout.fill = GridBagConstraints.BOTH;
		topPanelLayout.gridx = 0;
		topPanelLayout.gridy = 0;
		wholePanel.add(topPanel, topPanelLayout);

		bottomPanel = new JPanel();
		final GridBagConstraints bottomPanelLayout = new GridBagConstraints();
		bottomPanelLayout.weightx = 1.0;
		bottomPanelLayout.weighty = 1.0;
		bottomPanelLayout.fill = GridBagConstraints.BOTH;
		bottomPanelLayout.gridx = 0;
		bottomPanelLayout.gridy = 1;
		wholePanel.add(bottomPanel, bottomPanelLayout);

		setLayout(new BorderLayout(0, 0));
		add(wholePanel);

		// TODO: split to separate class
		final GroupLayout gl_panel = new GroupLayout(getTopPanel());
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup())
						.addComponent(actionButton))
					.addContainerGap(251, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(actionButton)
					.addContainerGap())
		);
		getTopPanel().setLayout(gl_panel);

		getBottomPanel().setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "List data", TitledBorder.LEADING, TitledBorder.TOP, null, null));

	}

	protected JPanel getTopPanel() {
		return topPanel;
	}

	protected JPanel getBottomPanel() {
		return bottomPanel;
	}


	public void setActionButton(String text, ActionListener actionListener) {
		actionButton.setText(text);

		for (final ActionListener registeredActionListener: actionButton.getActionListeners()) {
			actionButton.removeActionListener(registeredActionListener);
		}

		actionButton.addActionListener(actionListener);
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();
			}
		};
	}
}