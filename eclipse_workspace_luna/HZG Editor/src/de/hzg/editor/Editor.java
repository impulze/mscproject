package de.hzg.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.Configuration;
import de.hzg.common.HibernateUtil;
import de.hzg.common.ObservedPropertyClassesConfiguration;
import de.hzg.measurement.ObservedPropertyDescription;
import de.hzg.measurement.Sensor;
import de.hzg.values.CalculatedData;
import de.hzg.values.RawData;

public class Editor {
	private JFrame frame;
	private SessionFactory sessionFactory;
	private Component currentComponent = null;
	private static ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Configuration configuration = new Configuration();
					observedPropertyClassesConfiguration = configuration.getObservedPropertyClassesConfiguration();
					final HibernateUtil hibernateUtil = new HibernateUtil(configuration);
					final Editor editor = new Editor();
					editor.sessionFactory = hibernateUtil.getSessionFactory();
					editor.initialize();
					editor.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 850, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar_1 = new JMenuBar();
		menuBar_1.setBorderPainted(false);
		frame.setJMenuBar(menuBar_1);

		JMenu mnFile = new JMenu("File");
		menuBar_1.add(mnFile);

		JSeparator separator_0 = new JSeparator();
		mnFile.add(separator_0);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar_1.add(mnEdit);

		JMenuItem mntmCreateSensor = new JMenuItem("Create sensor");
		mntmCreateSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateSensorPanel();
				}
			}
		});
		mnEdit.add(mntmCreateSensor);

		JMenuItem mntmEditSensor = new JMenuItem("Edit sensor");
		mntmEditSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditSensorDialog dialog = new EditSensorDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final Sensor sensor = dialog.getResult();

					if (sensor != null) {
						final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensor);
						setupEditSensorPanel(sensorPanel);
					}
				}
			}
		});
		mnEdit.add(mntmEditSensor);

		JMenuItem mntmListSensors = new JMenuItem("List sensors");
		mntmListSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListSensorsPanel();
				}
			}
		});
		mnEdit.add(mntmListSensors);

		JSeparator separator_1 = new JSeparator();
		mnEdit.add(separator_1);

		JMenuItem mntmCreateObservedProperty = new JMenuItem("Create observed property description");
		mntmCreateObservedProperty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateObservedPropertyDescriptionPanel();
				}
			}
		});
		mnEdit.add(mntmCreateObservedProperty);

		JMenuItem mntmEditObservedProperty = new JMenuItem("Edit observed property description");
		mntmEditObservedProperty.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditObservedPropertyDialog dialog = new EditObservedPropertyDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final ObservedPropertyDescription observedPropertyDescription = dialog.getResult();

					if (observedPropertyDescription != null) {
						final CreateEditObservedPropertyDescriptionPanel observedPropertyDescriptionPanel = new CreateEditObservedPropertyDescriptionPanel(frame, sessionFactory, observedPropertyClassesConfiguration, observedPropertyDescription);
						setupEditObservedPropertyDescriptionPanel(observedPropertyDescriptionPanel);
					}
				}
			}
		});
		mnEdit.add(mntmEditObservedProperty);

		JMenuItem mntmListObservedPropertyDescriptions = new JMenuItem("List observed property descriptions");
		mntmListObservedPropertyDescriptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListObservedPropertyDescriptionsPanel();
				}
			}
		});
		mnEdit.add(mntmListObservedPropertyDescriptions);

		JSeparator separator_2 = new JSeparator();
		mnEdit.add(separator_2);

		JMenuItem mntmCreateObservedPropertyClass = new JMenuItem("Create observed property class");
		mntmCreateObservedPropertyClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateObservedPropertyJavaClassPanel();
				}
			}
		});
		mnEdit.add(mntmCreateObservedPropertyClass);

		JMenuItem mntmEditObservedPropertyClass = new JMenuItem("Edit observed property class");
		mntmEditObservedPropertyClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditObservedPropertyJavaClassDialog dialog = new EditObservedPropertyJavaClassDialog(frame, observedPropertyClassesConfiguration);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final ObservedPropertyJavaClass observedPropertyJavaClass = dialog.getResult();

					if (observedPropertyJavaClass != null) {
						final CreateEditObservedPropertyJavaClassPanel observedPropertyJavaClassPanel = new CreateEditObservedPropertyJavaClassPanel(frame, sessionFactory, observedPropertyClassesConfiguration, observedPropertyJavaClass);
						setupEditObservedPropertyJavaClassPanel(observedPropertyJavaClassPanel);
					}
				}
			}
		});
		mnEdit.add(mntmEditObservedPropertyClass);

		JMenuItem mntmListObservedPropertyClasses = new JMenuItem("List observed property classes");
		mntmListObservedPropertyClasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListObservedPropertyJavaClassesPanel();
				}
			}
		});
		mnEdit.add(mntmListObservedPropertyClasses);

		JSeparator separator_3 = new JSeparator();
		mnEdit.add(separator_3);

		JMenuItem mntmListRawValues = new JMenuItem("List raw values");
		mntmListRawValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (isDirtyCheck()) {
					final ListValuesPanel<RawData, Integer> listValuesPanel = new ListValuesPanel<RawData, Integer>(frame, sessionFactory, Integer.class, "RawData");
					listValuesPanel.setTitle("List raw values");
					switchPanel("List raw values", listValuesPanel);
				}
			}
		});
		mnEdit.add(mntmListRawValues);

		JMenuItem mntmListCalculatedValues = new JMenuItem("List calculated values");
		mntmListCalculatedValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (isDirtyCheck()) {
					final ListValuesPanel<CalculatedData, Double> listValuesPanel = new ListValuesPanel<CalculatedData, Double>(frame, sessionFactory, Double.class, "CalculatedData");
					listValuesPanel.setTitle("List calculated values");
					switchPanel("List calculated values", listValuesPanel);
				}
			}
		});
		mnEdit.add(mntmListCalculatedValues);

		JMenu mnHelp = new JMenu("Help");
		menuBar_1.add(mnHelp);

		JMenuItem mntmHandbook = new JMenuItem("Handbook");
		mnHelp.add(mntmHandbook);

		JSeparator separator_4 = new JSeparator();
		mnHelp.add(separator_4);

		JMenuItem mntmAboutHZGEditor = new JMenuItem("About HZG Editor");
		mntmAboutHZGEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final AboutDialog aboutDialog = new AboutDialog(frame);

				aboutDialog.pack();
				aboutDialog.setLocationRelativeTo(frame);
				aboutDialog.setVisible(true);
			}
		});
		mnHelp.add(mntmAboutHZGEditor);

		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		/* TODO: for development*/ 
		final Session session = sessionFactory.openSession();
		try {
			@SuppressWarnings("unchecked")
			//final List<ObservedPropertyDescription> result = (List<ObservedPropertyDescription>)session
			final List<Sensor> result = (List<Sensor>)session
				//.createQuery("FROM  ObservedPropertyDescription WHERE name = :name")
				//.setParameter("name", "Vbatt")
				.createQuery("FROM Sensor WHERE name = :name")
				.setParameter("name", "FakeCTDHelgoland")
				.list();
			//final ObservedPropertyDescription observedPropertyDescription = result.get(0);
			//final CreateEditObservedPropertyDescriptionPanel observedPropertyDescriptionPanel = new CreateEditObservedPropertyDescriptionPanel(frame, sessionFactory, observedPropertyClassesConfiguration, observedPropertyDescription);
			final Sensor sensor = result.get(0);
			sensor.initSensor();
			final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensor);
			//setupEditObservedPropertyDescriptionPanel(observedPropertyDescriptionPanel);
			setupEditSensorPanel(sensorPanel);
		} finally {
			session.close();
		}
	}

	SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private void switchPanel(String title, Component component) {
		frame.setTitle(title);
		frame.getContentPane().removeAll();
		frame.getContentPane().add(component);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		currentComponent = component;
	}

	private boolean isDirty() {
		if (currentComponent instanceof CreateEditSensorPanel) {
			return ((CreateEditSensorPanel)currentComponent).isDirty();
		} else if (currentComponent instanceof CreateEditObservedPropertyDescriptionPanel) {
			return ((CreateEditObservedPropertyDescriptionPanel)currentComponent).isDirty();
		} else if (currentComponent instanceof CreateEditObservedPropertyJavaClassPanel) {
			return ((CreateEditObservedPropertyJavaClassPanel)currentComponent).isDirty();
		}

		return false;
	}

	private boolean isDirtyCheck() {
		boolean clear = true;

		if (isDirty()) {
			final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Discard changes?", JOptionPane.YES_NO_OPTION);
			clear = confirm == JOptionPane.YES_OPTION;
		}

		return clear;
	}

	private void setupCreateSensorPanel() {
		final Sensor newSensor = CreateEditSensorPanel.createNewSensor();
		final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, newSensor);

		sensorPanel.setTitle("Create sensor");
		switchPanel("Create sensor", sensorPanel);

		sensorPanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditSensorPanel(sensorPanel);
			}
		});
	}

	private void setupEditSensorPanel(CreateEditSensorPanel sensorPanel) {
		sensorPanel.setSaved(true);
		sensorPanel.setTitle("Edit sensor");
		sensorPanel.showEditFunctions();
		sensorPanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListSensorsPanel();
			}
		});
		switchPanel("Edit sensor", sensorPanel);
	}

	private void setupListSensorsPanel() {
		final ListSensorsPanel listSensorsPanel = new ListSensorsPanel(frame, sessionFactory);
		listSensorsPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateSensorPanel();
			}
		});
		listSensorsPanel.setEditListener(new EditListener<Sensor>() {
			public void onEdit(Sensor sensor) {
				final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensor);
				setupEditSensorPanel(sensorPanel);
			}
		});

		switchPanel("List sensors", listSensorsPanel);
	}

	private void setupCreateObservedPropertyDescriptionPanel() {
		final ObservedPropertyDescription newObservedPropertyDescription= CreateEditObservedPropertyDescriptionPanel.createNewObservedPropertyDescription();
		final CreateEditObservedPropertyDescriptionPanel observedPropertyDescriptionPanel = new CreateEditObservedPropertyDescriptionPanel(frame, sessionFactory, observedPropertyClassesConfiguration, newObservedPropertyDescription);

		observedPropertyDescriptionPanel.setTitle("Create observed property description");
		switchPanel("Create observed property description", observedPropertyDescriptionPanel);

		observedPropertyDescriptionPanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditObservedPropertyDescriptionPanel(observedPropertyDescriptionPanel);
			}
		});
	}

	private void setupEditObservedPropertyDescriptionPanel(CreateEditObservedPropertyDescriptionPanel observedPropertyDescriptionPanel) {
		observedPropertyDescriptionPanel.setSaved(true);
		observedPropertyDescriptionPanel.setTitle("Edit observed property description");
		observedPropertyDescriptionPanel.showEditFunctions();
		observedPropertyDescriptionPanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListObservedPropertyDescriptionsPanel();
			}
		});
		switchPanel("Edit observed property description", observedPropertyDescriptionPanel);
	}

	private void setupListObservedPropertyDescriptionsPanel() {
		final ListObservedPropertyDescriptionsPanel listObservedPropertyDescriptionsPanel = new ListObservedPropertyDescriptionsPanel(frame, sessionFactory);
		listObservedPropertyDescriptionsPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateObservedPropertyDescriptionPanel();
			}
		});
		listObservedPropertyDescriptionsPanel.setEditListener(new EditListener<ObservedPropertyDescription>() {
			public void onEdit(ObservedPropertyDescription observedPropertyDescription) {
				final CreateEditObservedPropertyDescriptionPanel observedPropertyDescriptionPanel = new CreateEditObservedPropertyDescriptionPanel(frame, sessionFactory, observedPropertyClassesConfiguration, observedPropertyDescription);
				setupEditObservedPropertyDescriptionPanel(observedPropertyDescriptionPanel);
			}
		});

		switchPanel("List observed property descriptions", listObservedPropertyDescriptionsPanel);
	}

	private void setupCreateObservedPropertyJavaClassPanel() {
		final ObservedPropertyJavaClass newObservedPropertyJavaClass = CreateEditObservedPropertyJavaClassPanel.createNewObservedPropertyJavaClass(observedPropertyClassesConfiguration);
		final CreateEditObservedPropertyJavaClassPanel observedPropertyJavaClassPanel = new CreateEditObservedPropertyJavaClassPanel(frame, sessionFactory, observedPropertyClassesConfiguration, newObservedPropertyJavaClass);

		observedPropertyJavaClassPanel.setTitle("Create observed property class");
		switchPanel("Create observed property class", observedPropertyJavaClassPanel);

		observedPropertyJavaClassPanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditObservedPropertyJavaClassPanel(observedPropertyJavaClassPanel);
			}
		});
	}

	private void setupEditObservedPropertyJavaClassPanel(CreateEditObservedPropertyJavaClassPanel observedPropertyJavaClassPanel) {
		// In terms of observedPropertyJavaClassPanel being saved basically means it's on the filesystem
		observedPropertyJavaClassPanel.setSaved(true);
		observedPropertyJavaClassPanel.setTitle("Edit observed property class");
		observedPropertyJavaClassPanel.showEditFunctions();
		observedPropertyJavaClassPanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListObservedPropertyJavaClassesPanel();
			}
		});
		switchPanel("Edit observed property class", observedPropertyJavaClassPanel);
	}

	private void setupListObservedPropertyJavaClassesPanel() {
		final ListObservedPropertyJavaClassesPanel listObservedPropertyClassesPanel = new ListObservedPropertyJavaClassesPanel(frame, sessionFactory, observedPropertyClassesConfiguration);
		listObservedPropertyClassesPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateObservedPropertyJavaClassPanel();
			}
		});
		listObservedPropertyClassesPanel.setEditListener(new EditListener<ObservedPropertyJavaClass>() {
			public void onEdit(ObservedPropertyJavaClass observedPropertyJavaClass) {
				final CreateEditObservedPropertyJavaClassPanel observedPropertyJavaClassPanel = new CreateEditObservedPropertyJavaClassPanel(frame, sessionFactory, observedPropertyClassesConfiguration, observedPropertyJavaClass);
				setupEditObservedPropertyJavaClassPanel(observedPropertyJavaClassPanel);
			}
		});

		switchPanel("List observed property classes", listObservedPropertyClassesPanel);
	}
}