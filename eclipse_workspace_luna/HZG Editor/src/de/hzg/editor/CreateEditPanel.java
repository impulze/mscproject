package de.hzg.editor;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.border.LineBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.BorderLayout;

public class CreateEditPanel extends JPanel {
	private static final long serialVersionUID = 2353375536272798353L;
	private final JPanel topPanel;
	private final JPanel bottomPanel;

	public CreateEditPanel(String borderTitle) {
		setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), borderTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));

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
	}

	protected JPanel getTopPanel() {
		return topPanel;
	}

	protected JPanel getBottomPanel() {
		return bottomPanel;
	}
}