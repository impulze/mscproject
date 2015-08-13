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
import de.hzg.common.ProcedureClassesConfiguration;
import de.hzg.measurement.ProcedureDescription;
import de.hzg.measurement.Sensor;
import de.hzg.values.CalculatedData;
import de.hzg.values.RawData;

public class Editor {
	private JFrame frame;
	private SessionFactory sessionFactory;
	private Component currentComponent = null;
	private static ProcedureClassesConfiguration procedureClassesConfiguration;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Configuration configuration = new Configuration();
					procedureClassesConfiguration = configuration.getProcedureClassesConfiguration();
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

		JMenuItem mntmCreateProcedure = new JMenuItem("Create procedure");
		mntmCreateProcedure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateProcedurePanel();
				}
			}
		});
		mnEdit.add(mntmCreateProcedure);

		JMenuItem mntmEditProcedure = new JMenuItem("Edit procedure");
		mntmEditProcedure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditProcedureDialog dialog = new EditProcedureDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final ProcedureDescription procedureDescription = dialog.getResult();

					if (procedureDescription != null) {
						final CreateEditProcedurePanel procedurePanel = new CreateEditProcedurePanel(frame, sessionFactory, procedureClassesConfiguration, procedureDescription);
						setupEditProcedurePanel(procedurePanel);
					}
				}
			}
		});
		mnEdit.add(mntmEditProcedure);

		JMenuItem mntmListProcedures = new JMenuItem("List procedures");
		mntmListProcedures.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListProceduresPanel();
				}
			}
		});
		mnEdit.add(mntmListProcedures);

		JSeparator separator_2 = new JSeparator();
		mnEdit.add(separator_2);

		JMenuItem mntmCreateProcedureClass = new JMenuItem("Create procedure class");
		mntmCreateProcedureClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateProcedureJavaClassPanel();
				}
			}
		});
		mnEdit.add(mntmCreateProcedureClass);

		JMenuItem mntmEditProcedureClass = new JMenuItem("Edit procedure class");
		mntmEditProcedureClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditProcedureJavaClassDialog dialog = new EditProcedureJavaClassDialog(frame, procedureClassesConfiguration);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final ProcedureJavaClass procedureJavaClass = dialog.getResult();

					if (procedureJavaClass != null) {
						final CreateEditProcedureJavaClassPanel procedureJavaClassPanel = new CreateEditProcedureJavaClassPanel(frame, sessionFactory, procedureClassesConfiguration, procedureJavaClass);
						setupEditProcedureJavaClassPanel(procedureJavaClassPanel);
					}
				}
			}
		});
		mnEdit.add(mntmEditProcedureClass);

		JMenuItem mntmListProcedureClasses = new JMenuItem("List procedure classes");
		mntmListProcedureClasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListProcedureJavaClassesPanel();
				}
			}
		});
		mnEdit.add(mntmListProcedureClasses);

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

		/* TODO: for development */
		final Session session = sessionFactory.openSession();
		try {
			@SuppressWarnings("unchecked")
			final List<ProcedureDescription> result = (List<ProcedureDescription>)session
				.createQuery("FROM  ProcedureDescription WHERE name = :name")
				.setParameter("name", "Vbatt")
				.list();
			final ProcedureDescription procedureDescription = result.get(0);
			final CreateEditProcedurePanel procedurePanel = new CreateEditProcedurePanel(frame, sessionFactory, procedureClassesConfiguration, procedureDescription);
			setupEditProcedurePanel(procedurePanel);
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
		} else if (currentComponent instanceof CreateEditProcedurePanel) {
			return ((CreateEditProcedurePanel)currentComponent).isDirty();
		} else if (currentComponent instanceof CreateEditProcedureJavaClassPanel) {
			return ((CreateEditProcedureJavaClassPanel)currentComponent).isDirty();
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

	private void setupCreateProcedurePanel() {
		final ProcedureDescription newProcedureDescription= CreateEditProcedurePanel.createNewProcedureDescription();
		final CreateEditProcedurePanel procedurePanel = new CreateEditProcedurePanel(frame, sessionFactory, procedureClassesConfiguration, newProcedureDescription);

		procedurePanel.setTitle("Create procedure");
		switchPanel("Create procedure", procedurePanel);

		procedurePanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditProcedurePanel(procedurePanel);
			}
		});
	}

	private void setupEditProcedurePanel(CreateEditProcedurePanel procedurePanel) {
		procedurePanel.setSaved(true);
		procedurePanel.setTitle("Edit procedure");
		procedurePanel.showEditFunctions();
		procedurePanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListProceduresPanel();
			}
		});
		switchPanel("Edit procedure", procedurePanel);
	}

	private void setupListProceduresPanel() {
		final ListProceduresPanel listProceduresPanel = new ListProceduresPanel(frame, sessionFactory);
		listProceduresPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateProcedurePanel();
			}
		});
		listProceduresPanel.setEditListener(new EditListener<ProcedureDescription>() {
			public void onEdit(ProcedureDescription procedureDescription) {
				final CreateEditProcedurePanel procedurePanel = new CreateEditProcedurePanel(frame, sessionFactory, procedureClassesConfiguration, procedureDescription);
				setupEditProcedurePanel(procedurePanel);
			}
		});

		switchPanel("List procedures", listProceduresPanel);
	}

	private void setupCreateProcedureJavaClassPanel() {
		final ProcedureJavaClass newProcedureJavaClass = CreateEditProcedureJavaClassPanel.createNewProcedureJavaClass(procedureClassesConfiguration);
		final CreateEditProcedureJavaClassPanel procedureJavaClassPanel = new CreateEditProcedureJavaClassPanel(frame, sessionFactory, procedureClassesConfiguration, newProcedureJavaClass);

		procedureJavaClassPanel.setTitle("Create procedure class");
		switchPanel("Create procedure class", procedureJavaClassPanel);

		procedureJavaClassPanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditProcedureJavaClassPanel(procedureJavaClassPanel);
			}
		});
	}

	private void setupEditProcedureJavaClassPanel(CreateEditProcedureJavaClassPanel procedureJavaClassPanel) {
		// In terms of procedureJavaClassPanel being saved basically means it's on the filesystem
		procedureJavaClassPanel.setSaved(true);
		procedureJavaClassPanel.setTitle("Edit procedure class");
		procedureJavaClassPanel.showEditFunctions();
		procedureJavaClassPanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListProcedureJavaClassesPanel();
			}
		});
		switchPanel("Edit procedure class", procedureJavaClassPanel);
	}

	private void setupListProcedureJavaClassesPanel() {
		final ListProcedureJavaClassesPanel listProcedureClassesPanel = new ListProcedureJavaClassesPanel(frame, sessionFactory, procedureClassesConfiguration);
		listProcedureClassesPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateProcedureJavaClassPanel();
			}
		});
		listProcedureClassesPanel.setEditListener(new EditListener<ProcedureJavaClass>() {
			public void onEdit(ProcedureJavaClass procedureJavaClass) {
				final CreateEditProcedureJavaClassPanel procedureJavaClassPanel = new CreateEditProcedureJavaClassPanel(frame, sessionFactory, procedureClassesConfiguration, procedureJavaClass);
				setupEditProcedureJavaClassPanel(procedureJavaClassPanel);
			}
		});

		switchPanel("List procedure classes", listProcedureClassesPanel);
	}
}