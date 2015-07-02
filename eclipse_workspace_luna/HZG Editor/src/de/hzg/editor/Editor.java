package de.hzg.editor;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import java.awt.event.ActionListener;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import de.hzg.common.Configuration;
import de.hzg.common.ConfigurationNotFound;
import de.hzg.common.HibernateUtil;
import de.hzg.sensors.Probe;
import de.hzg.sensors.SensorDescription;

public class Editor {
	private JFrame frame;
	private SessionFactory sessionFactory;
	private Component currentComponent = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final Configuration configuration = new Configuration();
					final HibernateUtil hibernateUtil = new HibernateUtil(configuration);
					final Editor editor = new Editor();
					editor.sessionFactory = hibernateUtil.getSessionFactory();
					editor.initialize(configuration);
					editor.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initialize(Configuration configuration) {
		final Configuration usedConfiguration = configuration;

		frame = new JFrame();
		frame.setBounds(100, 100, 850, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar_1 = new JMenuBar();
		menuBar_1.setBorderPainted(false);
		frame.setJMenuBar(menuBar_1);

		JMenu mnDatabase = new JMenu("File");
		menuBar_1.add(mnDatabase);

		JMenuItem mntmSwitchDB = new JMenuItem("Switch database");
		mnDatabase.add(mntmSwitchDB);

		JSeparator separator_0 = new JSeparator();
		mnDatabase.add(separator_0);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		mnDatabase.add(mntmExit);

		JMenu mnProbe = new JMenu("Edit");
		menuBar_1.add(mnProbe);

		JMenuItem mntmCreateProbe = new JMenuItem("Create probe");
		mntmCreateProbe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean clear = true;

				if (isDirty()) {
					final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Create new form", JOptionPane.YES_NO_OPTION);
					clear = confirm == JOptionPane.YES_OPTION;
				}

				if (clear) {
					final CreateEditProbePanel probePanel = new CreateProbePanel(frame, sessionFactory);
					switchPanel("Create probe", probePanel);
				}
			}
		});
		mnProbe.add(mntmCreateProbe);

		JMenuItem mntmEditProbe = new JMenuItem("Edit probe");
		mntmEditProbe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean clear = true;

				if (isDirty()) {
					final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Create new form", JOptionPane.YES_NO_OPTION);
					clear = confirm == JOptionPane.YES_OPTION;
				}

				if (clear) {
					final EditProbeDialog dialog = new EditProbeDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final Probe probe = dialog.getResult();

					if (probe != null) {
						final CreateEditProbePanel probePanel = new EditProbePanel(frame, sessionFactory, probe);
						switchPanel("Edit probe", probePanel);
					}
				}
			}
		});
		mnProbe.add(mntmEditProbe);

		JMenuItem mntmListProbes = new JMenuItem("List probes");
		mntmListProbes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean clear = true;

				if (isDirty()) {
					final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Create new form", JOptionPane.YES_NO_OPTION);
					clear = confirm == JOptionPane.YES_OPTION;
				}

				if (clear) {
					final ListProbesPanel listProbesPanel = new ListProbesPanel(frame, sessionFactory);
					switchPanel("List probes", listProbesPanel);
				}
			}
		});
		mnProbe.add(mntmListProbes);

		JSeparator separator_1 = new JSeparator();
		mnProbe.add(separator_1);

		JMenuItem mntmCreateSensor = new JMenuItem("Create sensor");
		mntmCreateSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean clear = true;

				if (isDirty()) {
					final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Create new form", JOptionPane.YES_NO_OPTION);
					clear = confirm == JOptionPane.YES_OPTION;
				}

				if (clear) {
					final CreateEditSensorPanel sensorPanel = new CreateSensorPanel(frame, sessionFactory);
					switchPanel("Create sensor", sensorPanel);
				}
			}
		});
		mnProbe.add(mntmCreateSensor);

		JMenuItem mntmEditSensor = new JMenuItem("Edit sensor");
		mntmEditSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean clear = true;

				if (isDirty()) {
					final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Create new form", JOptionPane.YES_NO_OPTION);
					clear = confirm == JOptionPane.YES_OPTION;
				}

				if (clear) {
					final EditSensorDialog dialog = new EditSensorDialog(frame, sessionFactory);
					dialog.pack();
					dialog.setLocationRelativeTo(frame);
					dialog.setVisible(true);
					final SensorDescription sensorDescription = dialog.getResult();

					if (sensorDescription != null) {
						final CreateEditSensorPanel sensorPanel = new EditSensorPanel(frame, sessionFactory, sensorDescription);
						switchPanel("Edit sensor", sensorPanel);
					}
				}
			}
		});
		mnProbe.add(mntmEditSensor);

		JMenuItem mntmListSensors = new JMenuItem("List sensors");
		mntmListSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean clear = true;

				if (isDirty()) {
					final int confirm = JOptionPane.showConfirmDialog(frame, "This will erase all of your input so far.", "Create new form", JOptionPane.YES_NO_OPTION);
					clear = confirm == JOptionPane.YES_OPTION;
				}

				if (clear) {
					final ListSensorsPanel listSensorsPanel = new ListSensorsPanel(frame, sessionFactory);
					switchPanel("List sensors", listSensorsPanel);
				}
			}
		});
		mnProbe.add(mntmListSensors);

		JSeparator separator_2 = new JSeparator();
		mnProbe.add(separator_2);

		JMenuItem mntmListRawValues = new JMenuItem("List raw values");
		mnProbe.add(mntmListRawValues);

		JMenuItem mntmListCalculatedValues = new JMenuItem("List calculated values");
		mnProbe.add(mntmListCalculatedValues);

		JMenu mnHelp = new JMenu("Help");
		menuBar_1.add(mnHelp);

		JMenuItem mntmHandbook = new JMenuItem("Handbook");
		mnHelp.add(mntmHandbook);

		JSeparator separator_3 = new JSeparator();
		mnHelp.add(separator_3);

		JMenuItem mntmAboutHZGEditor = new JMenuItem("About HZG Editor");
		mnHelp.add(mntmAboutHZGEditor);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		/* TODO: for development */
		final Session session = sessionFactory.openSession();
		/*
		try {
			@SuppressWarnings("unchecked")
			final List<Probe> result = (List<Probe>)session
				.createQuery("FROM Probe WHERE name = :name")
				.setParameter("name", "MyActiveProbe999")
				.list();
			final Probe probe = result.get(0);
			Probe.initProbe(probe);
			final CreateEditProbePanel probePanel = new EditProbePanel(frame, sessionFactory, probe);
			switchPanel("Edit probe", probePanel);
		} finally {
			session.close();
		}
		*/
		try {
			@SuppressWarnings("unchecked")
			final List<SensorDescription> result = (List<SensorDescription>)session
				.createQuery("FROM  SensorDescription WHERE name = :name")
				.setParameter("name", "Vbatt")
				.list();
			final SensorDescription sensorDescription = result.get(0);
			final CreateEditSensorPanel sensorPanel = new EditSensorPanel(frame, sessionFactory, sensorDescription);
			switchPanel("Edit sensor", sensorPanel);
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
		}

		return false;
	}
}