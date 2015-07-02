package de.hzg.editor;

import java.awt.Window;
import java.io.Writer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

public class CompilerSwingWorker extends SwingWorker<Boolean, Boolean> {
	private final Window owner;
	private final CompileOutputDialog dialog;
	private final Writer writer;
	private final String[] inputPaths;
	private final StandardJavaFileManager fileManager;
	private final JavaCompiler compiler;
	private final AtomicBoolean workerResult = new AtomicBoolean(false);
	private final Lock lock = new ReentrantLock();
	private final Condition canStart = lock.newCondition();

	CompilerSwingWorker(Window owner, CompileOutputDialog dialog, Writer writer, String[] inputPaths, StandardJavaFileManager fileManager, JavaCompiler compiler) {
		this.owner = owner;
		this.dialog = dialog;
		this.writer = writer;
		this.inputPaths = inputPaths;
		this.fileManager = fileManager;
		this.compiler = compiler;
	}

	@Override
	public Boolean doInBackground() throws Exception {
		final AtomicBoolean tasksResult = new AtomicBoolean(true);

		for (final String inputPath: inputPaths) {
			final Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjects(inputPath);

			lock.lock();
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							lock.lock();
							dialog.newTask(inputPath);
							canStart.signal();
						} finally {
							lock.unlock();
						}
					}
				});
				canStart.await();
			} finally {
				lock.unlock();
			}

			final Boolean taskResult = compiler.getTask(writer, fileManager, null, null, null, units).call();

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dialog.finishedTask(taskResult.booleanValue());
				}
			});

			if (taskResult.booleanValue()) {
				tasksResult.set(false);
			}
		}

		return tasksResult.get();
	}

	@Override
	public void done() {
		try {
			workerResult.set(get().booleanValue());
		} catch (InterruptedException | ExecutionException exception) {
			final String[] messages = { "Class(es) compilation task aborted.", "An exception occured." };
			final JDialog exceptionDialog = new ExceptionDialog(owner, "Class(es) compilation task aborted", messages, exception);
			exceptionDialog.pack();
			exceptionDialog.setLocationRelativeTo(owner);
			exceptionDialog.setVisible(true);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.finishedAll();
			}
		});
	}

	boolean result() {
		return workerResult.get();
	}
};