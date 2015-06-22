package de.hzg.editor;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Window;

import javax.swing.border.LineBorder;

import de.hzg.sensors.SensorDescription;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class CreateEditSensorPanel extends CreateEditPanel {
	private static final long serialVersionUID = 4302743064914251776L;
	private final JTextField nameTextField;
	private final JTextField classNameTextField;
	private final JTextField unitTextField;
	private final JButton actionButton;
	private final JTextArea metadataTextArea;
	private final SessionFactory sessionFactory;
	private SensorDescription sensorDescription;
	private boolean dirty = false;
	private final Window owner;

	public CreateEditSensorPanel(Window owner, SessionFactory sessionFactory) {
		super("Sensor metadata");

		this.sessionFactory = sessionFactory;
		this.owner = owner;

		nameTextField = new JTextField();
		nameTextField.setColumns(20);

		final JLabel lblName = new JLabel("Name:");
		lblName.setLabelFor(nameTextField);

		classNameTextField = new JTextField();
		classNameTextField.setColumns(30);

		final JLabel lblClassName = new JLabel("Name of Java Class:");
		lblName.setLabelFor(classNameTextField);

		unitTextField = new JTextField();
		unitTextField.setColumns(10);

		final JLabel lblUnit = new JLabel("Unit:");
		lblName.setLabelFor(unitTextField);

		actionButton = new JButton();

		final GroupLayout gl_panel = new GroupLayout(getTopPanel());
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblName)
								.addComponent(lblClassName)
								.addComponent(lblUnit))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(unitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(actionButton))
					.addContainerGap(251, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblName)
						.addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblClassName)
						.addComponent(classNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUnit)
						.addComponent(unitTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(actionButton)
					.addContainerGap())
		);
		getTopPanel().setLayout(gl_panel);

		getBottomPanel().setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Metadata", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		metadataTextArea = createTextArea(getBottomPanel());

		sensorDescription = new SensorDescription();
		setSensorDescription(sensorDescription);
	}

	void setSensorDescription(SensorDescription sensorDescription) {
		this.sensorDescription = sensorDescription;
		nameTextField.setText(sensorDescription.getName());
		classNameTextField.setText(sensorDescription.getClassName());
		unitTextField.setText(sensorDescription.getUnit());
		updateMetadata(sensorDescription);
	}

	public boolean isDirty() {
		if (!nameTextField.getText().equals(sensorDescription.getName())) {
			return true;
		}

		if (!classNameTextField.getText().equals(sensorDescription.getClassName())) {
			return true;
		}

		if (!unitTextField.getText().equals(sensorDescription.getUnit())) {
			return true;
		}

		return dirty;
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

	protected SensorDescription getSensorDescription() {
		return sensorDescription;
	}

	protected void updateMetadata(SensorDescription sensorDescription) {
		assert(sensorDescription != null);

		metadataTextArea.setText("Some metadata to edit here.");
	}

	private JTextArea createTextArea(JPanel panelToAddTo) {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		final JPanel gridLabel1 = makeGridLabel("Use right click to add/remove senors.");
		final JPanel gridLabel2 = makeGridLabel("Use left click(s) to change sensor values.");
		final JPanel gridLabel3 = makeGridLabel("Click column header to sort ascending/descending");
		final JPanel gridLabelIcon1 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));
		final JPanel gridLabelIcon2 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));
		final JPanel gridLabelIcon3 = makeGridIcon(UIManager.getIcon("OptionPane.informationIcon"));

		final JScrollPane scrollPane = new JScrollPane();
		final JTextArea textArea = new JTextArea();

		scrollPane.setViewportView(textArea);

		panelToAddTo.setLayout(layout);

		constraints.fill = GridBagConstraints.BOTH;

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon1, constraints);
		panelToAddTo.add(gridLabelIcon1);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel1, constraints);
		panelToAddTo.add(gridLabel1);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon2, constraints);
		panelToAddTo.add(gridLabelIcon2);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel2, constraints);
		panelToAddTo.add(gridLabel2);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabelIcon3, constraints);
		panelToAddTo.add(gridLabelIcon3);

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = 1;
		layout.setConstraints(gridLabel3, constraints);
		panelToAddTo.add(gridLabel3);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.gridheight = GridBagConstraints.REMAINDER;
		layout.setConstraints(scrollPane, constraints);
		panelToAddTo.add(scrollPane);

		return textArea;
	}

	private JPanel makeGridLabel(String string) {
		final JLabel gridLabel = new JLabel(string);
		final JPanel gridLabelPanel = new JPanel();

		gridLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		gridLabelPanel.add(gridLabel);
		return gridLabelPanel;
	}

	private JPanel makeGridIcon(Icon icon) {
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

	protected ActionListener getSaveActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();

				try {
					updateSensorDescriptionInformation();
					session.save(getSensorDescription());
					session.flush();
					dirty = false;
					JOptionPane.showMessageDialog(owner, "Sensor description successfully saved.", "Probe saved", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					dirty = true;
					final String[] messages = { "Sensor description could not be saved.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(owner, "Sensor description not saved", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				} finally {
					session.close();
				}
			}
		};
	}

	protected ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final Session session = getSessionFactory().openSession();
				try {
					updateSensorDescriptionInformation();
					session.update(getSensorDescription());
					session.flush();
					dirty = false;
					JOptionPane.showMessageDialog(owner, "Sensor description successfully edited.", "Probe edited", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					dirty = true;
					final String[] messages = { "Sensor description could not be updated.", "An exception occured." };
					final JDialog dialog = new ExceptionDialog(owner, "Sensor description not updated", messages, exception);
					dialog.pack();
					dialog.setLocationRelativeTo(owner);
					dialog.setVisible(true);
				} finally {
					session.close();
				}
			}
		};
	}

	private void updateSensorDescriptionInformation() {
		getSensorDescription().setName(nameTextField.getText());
		getSensorDescription().setClassName(classNameTextField.getText());
		getSensorDescription().setUnit(unitTextField.getText());
		dirty = true;
	}
}
