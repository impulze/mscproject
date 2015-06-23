package de.hzg.editor;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.border.LineBorder;

import org.hibernate.SessionFactory;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ListPanel extends JPanel {
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
		setActionButton("Update list", getUpdateActionListener());

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

	protected Window getOwner() {
		return owner;
	}

	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected JPanel makeGridLabel(String string) {
		final JLabel gridLabel = new JLabel(string);
		final JPanel gridLabelPanel = new JPanel();

		gridLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		gridLabelPanel.add(gridLabel);
		return gridLabelPanel;
	}

	protected JPanel makeGridIcon(Icon icon) {
		final double scale = 0.5;
		final int newWidth = (int)(scale * icon.getIconWidth());
		final int newHeight= (int)(scale * icon.getIconWidth());
		final Image image = ((ImageIcon)icon).getImage();
		final Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		final Icon newIcon = new ImageIcon(newImage);
		final JLabel gridIconLabel = new JLabel(newIcon);
		final JPanel gridIconLabelPanel = new JPanel();

		gridIconLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		gridIconLabelPanel.add(gridIconLabel);

		return gridIconLabelPanel;
	}

	protected ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getList();
			}
		};
	}

	protected abstract void getList();
}