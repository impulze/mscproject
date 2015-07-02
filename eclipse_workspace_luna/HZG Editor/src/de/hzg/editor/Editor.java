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
import de.hzg.common.SensorClassesConfiguration;
import de.hzg.measurement.Probe;
import de.hzg.measurement.SensorDescription;

public class Editor {
	private JFrame frame;
	private SessionFactory sessionFactory;
	private Component currentComponent = null;
	private static SensorClassesConfiguration sensorClassesConfiguration;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Configuration configuration = new Configuration();
					sensorClassesConfiguration = configuration.getSensorClassesConfiguration();
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

		JMenu mnProbe = new JMenu("Edit");
		menuBar_1.add(mnProbe);

		JMenuItem mntmCreateProbe = new JMenuItem("Create probe");
		mntmCreateProbe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateProbePanel();
				}
			}
		});
		mnProbe.add(mntmCreateProbe);

		JMenuItem mntmEditProbe = new JMenuItem("Edit probe");
		mntmEditProbe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditProbeDialog dialog = new EditProbeDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final Probe probe = dialog.getResult();

					if (probe != null) {
						final CreateEditProbePanel probePanel = new CreateEditProbePanel(frame, sessionFactory, probe);
						setupEditProbePanel(probePanel);
					}
				}
			}
		});
		mnProbe.add(mntmEditProbe);

		JMenuItem mntmListProbes = new JMenuItem("List probes");
		mntmListProbes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListProbesPanel();
				}
			}
		});
		mnProbe.add(mntmListProbes);

		JSeparator separator_1 = new JSeparator();
		mnProbe.add(separator_1);

		JMenuItem mntmCreateSensor = new JMenuItem("Create sensor");
		mntmCreateSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateSensorPanel();
				}
			}
		});
		mnProbe.add(mntmCreateSensor);

		JMenuItem mntmEditSensor = new JMenuItem("Edit sensor");
		mntmEditSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditSensorDialog dialog = new EditSensorDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final SensorDescription sensorDescription = dialog.getResult();

					if (sensorDescription != null) {
						final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensorClassesConfiguration, sensorDescription);
						setupEditSensorPanel(sensorPanel);
					}
				}
			}
		});
		mnProbe.add(mntmEditSensor);

		JMenuItem mntmListSensors = new JMenuItem("List sensors");
		mntmListSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListSensorsPanel();
				}
			}
		});
		mnProbe.add(mntmListSensors);

		JSeparator separator_2 = new JSeparator();
		mnProbe.add(separator_2);

		JMenuItem mntmCreateSensorClass = new JMenuItem("Create sensor class");
		mntmCreateSensorClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupCreateSensorJavaClassPanel();
				}
			}
		});
		mnProbe.add(mntmCreateSensorClass);

		JMenuItem mntmEditSensorClass = new JMenuItem("Edit sensor class");
		mntmEditSensorClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					final EditSensorJavaClassDialog dialog = new EditSensorJavaClassDialog(frame, sensorClassesConfiguration);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final SensorJavaClass sensorJavaClass = dialog.getResult();

					if (sensorJavaClass != null) {
						final CreateEditSensorJavaClassPanel sensorJavaClassPanel = new CreateEditSensorJavaClassPanel(frame, sessionFactory, sensorClassesConfiguration, sensorJavaClass);
						setupEditSensorJavaClassPanel(sensorJavaClassPanel);
					}
				}
			}
		});
		mnProbe.add(mntmEditSensorClass);

		JMenuItem mntmListSensorClasses = new JMenuItem("List sensor classes");
		mntmListSensorClasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (isDirtyCheck()) {
					setupListSensorJavaClassesPanel();
				}
			}
		});
		mnProbe.add(mntmListSensorClasses);

		JSeparator separator_3 = new JSeparator();
		mnProbe.add(separator_3);

		JMenuItem mntmListRawValues = new JMenuItem("List raw values");
		mnProbe.add(mntmListRawValues);

		JMenuItem mntmListCalculatedValues = new JMenuItem("List calculated values");
		mnProbe.add(mntmListCalculatedValues);

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
			final List<SensorDescription> result = (List<SensorDescription>)session
				.createQuery("FROM  SensorDescription WHERE name = :name")
				.setParameter("name", "Vbatt")
				.list();
			final SensorDescription sensorDescription = result.get(0);
			final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensorClassesConfiguration, sensorDescription);
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
		if (currentComponent instanceof CreateEditProbePanel) {
			return ((CreateEditProbePanel)currentComponent).isDirty();
		} else if (currentComponent instanceof CreateEditSensorPanel) {
			return ((CreateEditSensorPanel)currentComponent).isDirty();
		} else if (currentComponent instanceof CreateEditSensorJavaClassPanel) {
			return ((CreateEditSensorJavaClassPanel)currentComponent).isDirty();
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

	private void setupCreateProbePanel() {
		final Probe newProbe = CreateEditProbePanel.createNewProbe();
		final CreateEditProbePanel probePanel = new CreateEditProbePanel(frame, sessionFactory, newProbe);

		probePanel.setTitle("Create probe");
		switchPanel("Create probe", probePanel);

		probePanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditProbePanel(probePanel);
			}
		});
	}

	private void setupEditProbePanel(CreateEditProbePanel probePanel) {
		probePanel.setSaved(true);
		probePanel.setTitle("Edit probe");
		probePanel.showEditFunctions();
		probePanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListProbesPanel();
			}
		});
		switchPanel("Edit probe", probePanel);
	}

	private void setupListProbesPanel() {
		final ListProbesPanel listProbesPanel = new ListProbesPanel(frame, sessionFactory);
		listProbesPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateProbePanel();
			}
		});
		listProbesPanel.setEditListener(new EditListener<Probe>() {
			public void onEdit(Probe probe) {
				final CreateEditProbePanel probePanel = new CreateEditProbePanel(frame, sessionFactory, probe);
				setupEditProbePanel(probePanel);
			}
		});

		switchPanel("List probes", listProbesPanel);
	}

	private void setupCreateSensorPanel() {
		final SensorDescription newSensorDescription= CreateEditSensorPanel.createNewSensorDescription();
		final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensorClassesConfiguration, newSensorDescription);

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
		listSensorsPanel.setEditListener(new EditListener<SensorDescription>() {
			public void onEdit(SensorDescription sensorDescription) {
				final CreateEditSensorPanel sensorPanel = new CreateEditSensorPanel(frame, sessionFactory, sensorClassesConfiguration, sensorDescription);
				setupEditSensorPanel(sensorPanel);
			}
		});

		switchPanel("List sensors", listSensorsPanel);
	}

	private void setupCreateSensorJavaClassPanel() {
		final SensorJavaClass newSensorJavaClass = CreateEditSensorJavaClassPanel.createNewSensorJavaClass(sensorClassesConfiguration);
		final CreateEditSensorJavaClassPanel sensorJavaClassPanel = new CreateEditSensorJavaClassPanel(frame, sessionFactory, sensorClassesConfiguration, newSensorJavaClass);

		sensorJavaClassPanel.setTitle("Create sensor class");
		switchPanel("Create sensor class", sensorJavaClassPanel);

		sensorJavaClassPanel.setSavedHandler(new SavedHandler() {
			@Override
			public void onSave() {
				setupEditSensorJavaClassPanel(sensorJavaClassPanel);
			}
		});
	}

	private void setupEditSensorJavaClassPanel(CreateEditSensorJavaClassPanel sensorJavaClassPanel) {
		// In terms of sensorJavaClassPanel being saved basically means it's on the filesystem
		sensorJavaClassPanel.setSaved(true);
		sensorJavaClassPanel.setTitle("Edit sensor class");
		sensorJavaClassPanel.showEditFunctions();
		sensorJavaClassPanel.setRemoveListener(new RemoveListener() {
			public void onRemove() {
				setupListSensorJavaClassesPanel();
			}
		});
		switchPanel("Edit sensor class", sensorJavaClassPanel);
	}

	private void setupListSensorJavaClassesPanel() {
		final ListSensorJavaClassesPanel listSensorClassesPanel = new ListSensorJavaClassesPanel(frame, sessionFactory, sensorClassesConfiguration);
		listSensorClassesPanel.setAddListener(new AddListener() {
			public void onAdd() {
				setupCreateSensorJavaClassPanel();
			}
		});
		listSensorClassesPanel.setEditListener(new EditListener<SensorJavaClass>() {
			public void onEdit(SensorJavaClass sensorJavaClass) {
				final CreateEditSensorJavaClassPanel sensorJavaClassPanel = new CreateEditSensorJavaClassPanel(frame, sessionFactory, sensorClassesConfiguration, sensorJavaClass);
				setupEditSensorJavaClassPanel(sensorJavaClassPanel);
			}
		});

		switchPanel("List sensor classes", listSensorClassesPanel);
	}
}