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
	private final List<List<JPanel>> informationMessagePanelsLists = new ArrayList<List<JPanel>>();
	private final List<JComponent> panelList = new ArrayList<JComponent>();
	private int currentRowForPopup = -1;
	private int currentColumnForPopup = -1;
	private TablePopupMenu cellPopupMenu;
	private TablePopupMenu noCellPopupMenu;
	private static double ICON_WEIGHT = 0.0;
	private static double LABEL_WEIGHT = 1.0;

	public void addInformationMessage(String message) {
		final JPanel informationMessageLabelPanel = createInformationMessageLabelPanel(message);
		final JPanel informationMessageIconPanel = createInformationMessageIconPanel();
		final List<JPanel> informationMessagePanels = new ArrayList<JPanel>();

		informationMessagePanels.add(informationMessageIconPanel);
		informationMessagePanels.add(informationMessageLabelPanel);
		informationMessagePanelsLists.add(informationMessagePanels);
	}

	public void setDefaultPopupHandler() {
		// default popup handler
		popupHandler = new PopupHandler() {
			@Override
			public JPopupMenu getComponentPopupMenu(JComponent source) {
				final TablePopupMenu popupMenu;

				if (currentRowForPopup >= 0 && currentColumnForPopup >= 0) {
					popupMenu = cellPopupMenu;
				} else {
					popupMenu = noCellPopupMenu;
				}

				if (popupMenu != null) {
					popupMenu.setCurrentRow(currentRowForPopup);
					popupMenu.setCurrentColumn(currentColumnForPopup);
					popupMenu.setCurrentTable((JTable)source);
				}

				return popupMenu;
			}

			@Override
			public Point getPopupLocation(JComponent source, MouseEvent arg0) {
				final JTable sourceTable = (JTable)source;
				final int row = sourceTable.rowAtPoint(arg0.getPoint());
				final int column = sourceTable.columnAtPoint(arg0.getPoint());

				System.out.println("setting row to " + row);
				currentRowForPopup = row;
				currentColumnForPopup = column;

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

	void addPanel(JComponent panel) {
		panelList.add(panel);
	}

	public final JPanel getResultPanel() {
		final JPanel resultPanel = new JPanel();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();

		resultPanel.setLayout(gridBagLayout);

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 0;

		for (int i = 0; i < informationMessagePanelsLists.size(); i++) {
			final List<JPanel> informationPanelList = informationMessagePanelsLists.get(i);
			final boolean isLastVertically;

			if (i + 1 == informationMessagePanelsLists.size()) {
				isLastVertically = panelList.size() == 0;
			} else {
				isLastVertically = false;
			}

			if (isLastVertically) {
				gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
			} else {
				gridBagConstraints.gridheight = 1;
			}

			gridBagConstraints.gridy = i;

			for (int j = 0; j < informationPanelList.size(); j++) {
				final JPanel informationPanel = informationPanelList.get(j);
				final boolean isLastHorizontally;

				if (j + 1 == informationPanelList.size()) {
					isLastHorizontally = true;
				} else {
					isLastHorizontally = false;
				}

				if (isLastHorizontally) {
					gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
				} else {
					gridBagConstraints.gridwidth = 1;
				}

				if (j == 0) {
					gridBagConstraints.weightx = ICON_WEIGHT;
				} else {
					gridBagConstraints.weightx = LABEL_WEIGHT;
				}

				gridBagConstraints.gridx = j;

				gridBagLayout.setConstraints(informationPanel, gridBagConstraints);
				resultPanel.add(informationPanel);
			}
		}

		for (int i = 0; i < panelList.size(); i++) {
			final boolean isLastVertically;

			if (i + 1 == panelList.size()) {
				isLastVertically = true;
			} else {
				isLastVertically = false;
			}

			if (isLastVertically) {
				gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
			} else {
				gridBagConstraints.gridheight = 1;
			}

			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = informationMessagePanelsLists.size() + i;
			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints.weightx = 1.0;

			gridBagLayout.setConstraints(panelList.get(i), gridBagConstraints);
			resultPanel.add(panelList.get(i));
		}

		return resultPanel;
	}

	void setCellPopupMenu(TablePopupMenu cellPopupMenu) {
		this.cellPopupMenu = cellPopupMenu;
	}

	void setNoCellPopupMenu(TablePopupMenu noCellPopupMenu) {
		this.noCellPopupMenu = noCellPopupMenu;
	}

	public JTable create() {
		if (popupHandler == null) {
			setDefaultPopupHandler();
		}

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
				System.out.println("getpou: " + popupHandler);
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

		final JPanel resultPanel = getResultPanel();

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

		panelLayout.setConstraints(resultPanel, gridBagConstraints);
		tablePanel.add(resultPanel);

		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;

		panelLayout.setConstraints(scrollPane, gridBagConstraints);
		tablePanel.add(scrollPane);

		return tablePanel;
	}
}