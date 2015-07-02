package de.hzg.editor;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;

public class DataCreator {
	private PopupHandler popupHandler;
	private final List<List<JPanel>> informationPanelLists = new ArrayList<List<JPanel>>();
	private int currentRowForPopup = -1;
	private JPopupMenu cellPopupMenu;
	private JPopupMenu noCellPopupMenu;

	public void addInformationMessage(String message) {
		final JPanel informationMessageLabelPanel = createInformationMessageLabelPanel(message);
		final JPanel informationMessageIconPanel = createInformationMessageIconPanel();
		final List<JPanel> informationMessagePanels = new ArrayList<JPanel>();

		informationMessagePanels.add(informationMessageIconPanel);
		informationMessagePanels.add(informationMessageLabelPanel);
		informationPanelLists.add(informationMessagePanels);

		// default popup handler
		popupHandler = new PopupHandler() {
			@Override
			public JPopupMenu getComponentPopupMenu(JComponent source) {
				if (currentRowForPopup >= 0) {
					return cellPopupMenu;
				}

				return noCellPopupMenu;
			}

			@Override
			public Point getPopupLocation(JComponent source, MouseEvent arg0) {
				final JTable sourceTable = (JTable)source;
				final int row = sourceTable.rowAtPoint(arg0.getPoint());

				currentRowForPopup = row;

				return null;
			}
		};
	}

	public void setPopupHandler(PopupHandler popupHandler) {
		this.popupHandler = popupHandler;
	}

	private JPanel createInformationMessageLabelPanel(String message) {
		final JLabel messageLabel = new JLabel(message);
		final JPanel messageLabelPanel = new JPanel();

		messageLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		messageLabelPanel.add(messageLabel);

		return messageLabelPanel;
	}

	private JPanel createInformationMessageIconPanel() {
		final Icon informationIcon = UIManager.getIcon("OptionPane.informationIcon");
		final double scale = 0.5;
		final int newWidth = (int)(scale * informationIcon.getIconWidth());
		final int newHeight= (int)(scale * informationIcon.getIconWidth());
		final Image image = ((ImageIcon)informationIcon).getImage();
		final Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		final Icon newIcon = new ImageIcon(newImage);
		final JLabel iconLabel = new JLabel(newIcon);
		final JPanel iconLabelPanel = new JPanel();

		iconLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		iconLabelPanel.add(iconLabel);

		return iconLabelPanel;
	}

	public final JPanel getMessageInformationPanel() {
		final JPanel resultPanel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();

		resultPanel.setLayout(gridBagLayout);

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0;

		for (int i = 0; i < informationPanelLists.size(); i++) {
			final List<JPanel> informationPanelList = informationPanelLists.get(i);

			if (i + 1 == informationPanelLists.size()) {
				gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
			} else {
				gridBagConstraints.gridheight = 1;
			}

			gridBagConstraints.gridy = i;

			for (int j = 0; j < informationPanelList.size(); j++) {
				final JPanel informationPanel = informationPanelList.get(j);

				if (j + 1 == informationPanelList.size()) {
					gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
				} else {
					gridBagConstraints.gridwidth = 1;
				}

				if (j == 0) {
					gridBagConstraints.weightx = 0;
				} else {
					gridBagConstraints.weightx = 1;
				}

				gridBagConstraints.gridx = j;

				gridBagLayout.setConstraints(informationPanel, gridBagConstraints);
				resultPanel.add(informationPanel);
			}
		}

		return resultPanel;
	}


	void setCellPopupMenu(JPopupMenu cellPopupMenu) {
		this.cellPopupMenu = cellPopupMenu;
	}

	void setNoCellPopupMenu(JPopupMenu noCellPopupMenu) {
		this.noCellPopupMenu = noCellPopupMenu;
	}

	public JTable create() {
		final JTable table = new JTable() {
			private static final long serialVersionUID = 2285091377444632688L;

			@Override
			public JPopupMenu getComponentPopupMenu() {
				if (popupHandler != null) {
					final JPopupMenu result = popupHandler.getComponentPopupMenu(this);

					if (result != null) {
						return result;
					}
				}

				return super.getComponentPopupMenu();
			}

			@Override
			public Point getPopupLocation(MouseEvent arg0) {
				if (popupHandler != null) {
					final Point result =  popupHandler.getPopupLocation(this, arg0);

					if (result != null) {
						return result;
					}
				}

				return super.getPopupLocation(arg0);
			}
		};

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(25);
		table.setAutoCreateRowSorter(true);

		return table;
	}

	JPanel createPanel(JComponent viewableComponent) {
		final JPanel tablePanel = new JPanel();
		final JScrollPane scrollPane = new JScrollPane();

		scrollPane.setViewportView(viewableComponent);

		final JPanel messageInformationPanel = getMessageInformationPanel();

		final GridBagLayout panelLayout = new GridBagLayout();

		tablePanel.setLayout(panelLayout);

		final GridBagConstraints gridBagConstraints = new GridBagConstraints();

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.weightx = 1;

		gridBagConstraints.gridy = 0;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridheight = 1;

		panelLayout.setConstraints(messageInformationPanel, gridBagConstraints);
		tablePanel.add(messageInformationPanel);

		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;

		panelLayout.setConstraints(scrollPane, gridBagConstraints);
		tablePanel.add(scrollPane);

		return tablePanel;
	}
}