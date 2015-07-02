package de.hzg.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class SplitPanel extends JPanel {
	private static final long serialVersionUID = -6652800387557722375L;
	private final JPanel topPanel;
	private final JPanel bottomPanel;
	private final JPanel bottomOverlayPanel;
	private final CardLayout bottomOverlayLayout;
	private DataProvider dataProvider;
	private boolean saved = false;
	private SavedHandler savedHandler;
	private UpdateHandler updateHandler;
	private boolean dirty = false;

	public SplitPanel() {
		final GridBagConstraints layoutConstraints = new GridBagConstraints();
		layoutConstraints.weightx = 1.0;
		layoutConstraints.fill = GridBagConstraints.BOTH;
		layoutConstraints.gridx = 0;

		layoutConstraints.gridy = 0;
		layoutConstraints.weighty = 0.0;

		final GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		// default layout only for bottom, top will get grouplayout
		topPanel = new JPanel();
		bottomPanel = new JPanel();
		bottomOverlayPanel = new JPanel();
		bottomOverlayLayout = new CardLayout();

		bottomPanel.setLayout(new BorderLayout(0, 0));

		bottomOverlayPanel.setLayout(bottomOverlayLayout);
		bottomOverlayPanel.add(bottomPanel, "visible");
		bottomOverlayPanel.add(new JPanel(), "invisible");

		layoutConstraints.weighty = 0.0;
		layoutConstraints.gridy = 0;
		layout.setConstraints(topPanel, layoutConstraints);
		add(topPanel);

		layoutConstraints.weighty = 1.0;
		layoutConstraints.gridy = 1;
		layout.setConstraints(bottomOverlayPanel, layoutConstraints);
		add(bottomOverlayPanel);
	}

	protected void setPanelTitle(JPanel panel, String title) {
		panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}

	public void setTitle(String title) {
		setPanelTitle(this, title);
	}

	public void setTopPanelTitle(String title) {
		setPanelTitle(getTopPanel(), title);
	}

	protected JPanel getTopPanel() {
		return topPanel;
	}

	public void setBottomPanelTitle(String title) {
		setPanelTitle(getBottomPanel(), title);
	}

	public JPanel getBottomPanel() {
		return bottomPanel;
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

	protected void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	protected JButton getActionButton(String title) {
		final JButton actionButton = new JButton(title);
		final String usedTitle = title;

		actionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dirty = true;
				// TODO: at this point the masks are considered dirty until saved, even if resetted

				if (dataProvider.provide(usedTitle)) {
					dirty = false;

					if (!saved) {
						if (savedHandler != null) {
							savedHandler.onSave();
						}
					} else {
						if (updateHandler != null) {
							updateHandler.onUpdate();
						}
					}
				}
			}
		});

		return actionButton;
	}

	protected boolean isDirty() {
		return dirty;
	}

	public void showBottom(boolean show) {
		if (show) {
			bottomOverlayLayout.show(bottomOverlayPanel, "visible");
		} else {
			bottomOverlayLayout.show(bottomOverlayPanel, "invisible");
		}
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	protected boolean getSaved() {
		return saved;
	}

	public void setSavedHandler(SavedHandler savedHandler) {
		this.savedHandler = savedHandler;
	}

	public void setUpdateHandler(UpdateHandler updateHandler) {
		this.updateHandler = updateHandler;
	}
}