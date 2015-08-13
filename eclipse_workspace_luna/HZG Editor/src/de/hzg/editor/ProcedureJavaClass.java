package de.hzg.editor;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import de.hzg.common.ProcedureClassesConfiguration;

public class ProcedureJavaClass {
	private String name;
	private String nameLoaded;
	private String text;
	private final ProcedureClassesConfiguration procedureClassesConfiguration;
	private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private static final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

	ProcedureJavaClass(ProcedureClassesConfiguration procedureClassesConfiguration) {
		this.procedureClassesConfiguration = procedureClassesConfiguration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name)  throws InvalidIdentifierException {
		if (name == null || name.length() == 0) {
			throw new InvalidIdentifierException(name);
		}

		final char[] nameCharacters = name.toCharArray();

		if (!Character.isJavaIdentifierStart(nameCharacters[0])) {
			throw new InvalidIdentifierException(name);
		}

		for (int i = 1; i < nameCharacters.length; i++) {
			if (!Character.isJavaIdentifierStart(nameCharacters[i])) {
				throw new InvalidIdentifierException(name);
			}
		}

		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isLoaded() {
		return nameLoaded != null;
	}

	public void load() throws IOException {
		text = load(procedureClassesConfiguration, name);
		nameLoaded = name;
	}

	public void save() throws IOException {
		save(procedureClassesConfiguration, name, text);
		nameLoaded = name;
	}

	public void deleteLoadedInstance() throws IOException {
		if (nameLoaded != null) {
			delete(procedureClassesConfiguration, nameLoaded);
			nameLoaded = null;
		}
	}

	public static List<String> listNames(ProcedureClassesConfiguration procedureClassesConfiguration, Window owner) {
		final String sourceDirectory = procedureClassesConfiguration.getSourceDirectory();
		final String packageDirectory = ProcedureClassesConfiguration.CLASSES_PACKAGE.replace('.', File.separatorChar);
		final String walkDirectory = sourceDirectory + File.separatorChar + packageDirectory + File.separatorChar;
		final List<String> procedureClassNames = new ArrayList<String>();

		try {
			Files.walkFileTree(Paths.get(walkDirectory), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
					final String name = path.getFileName().toString();

					if (name.endsWith(Kind.SOURCE.extension)) {
						procedureClassNames.add(name.substring(0, name.length() - Kind.SOURCE.extension.length()));
					}

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException exception) {
			final String[] messages = { "Procedure classes cannot be determined.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure classes cannot be determined", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		}

		return procedureClassNames;
	}
	private static String load(ProcedureClassesConfiguration procedureClassesConfiguration, String name) throws IOException {
		final String inputPath = getInputPath(procedureClassesConfiguration, name);
		final byte[] rawText = Files.readAllBytes(Paths.get(inputPath));
		return new String(rawText, Charset.defaultCharset());
	}

	private static void save(ProcedureClassesConfiguration procedureClassesConfiguration, String name, String text) throws IOException {
		final String inputPath = getInputPath(procedureClassesConfiguration, name); 
		final byte[] rawText = text.getBytes();
		Files.write(Paths.get(inputPath), rawText);
	}

	private static void delete(ProcedureClassesConfiguration procedureClassesConfiguration, String name) throws IOException {
		final String inputPath = getInputPath(procedureClassesConfiguration, name); 
		Files.delete(Paths.get(inputPath));
	}

	public static boolean compileTasks(Window owner, ProcedureClassesConfiguration procedureClassesConfiguration, String... names) {
		final CompileOutputDialog dialog = new CompileOutputDialog(owner);
		final StringBuilder writerStringBuilder = new StringBuilder();
		final Writer writer = new Writer() {
			@Override
			public void close() throws IOException {
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void write(char[] charBuffer, int offset, int length) throws IOException {
				for (int i = offset; i < offset + length; i++) {
					final char c = charBuffer[i];

					if (c == '\n') {
						final String message = writerStringBuilder.toString();
						writerStringBuilder.setLength(0);

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								dialog.append(message);
							}
						});
					} else {
						writerStringBuilder.append(c);
					}
				}
			}
		};

		final String[] inputPaths = new String[names.length];

		for (int i = 0; i < names.length; i++) {
			inputPaths[i] = getInputPath(procedureClassesConfiguration, names[i]);
		}

		final CompilerSwingWorker worker = new CompilerSwingWorker(owner, dialog, writer, inputPaths, fileManager, compiler);

		worker.execute();

		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);

		return worker.result();
	}

	private static String getInputPath(ProcedureClassesConfiguration procedureClassesConfiguration, String name) {
		final String sourceDirectory = procedureClassesConfiguration.getSourceDirectory();
		final String packageDirectory = ProcedureClassesConfiguration.CLASSES_PACKAGE.replace('.', File.separatorChar);

		return sourceDirectory + File.separatorChar + packageDirectory + File.separatorChar + name + Kind.SOURCE.extension;
	}

	public static String getCompilerInvocation() {
		return "java -jar foo/bar/baz";
	}

	public static String getTemplate() {
		return "";
	}

	public static ProcedureJavaClass loadByName(ProcedureClassesConfiguration procedureClassesConfiguration, Window owner, String name) {

		final ProcedureJavaClass procedureJavaClass = new ProcedureJavaClass(procedureClassesConfiguration);

		try {
			procedureJavaClass.setName(name);
		} catch (InvalidIdentifierException exception) {
			final String[] messages = { "Procedure class has an invalid identifier.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure class has an invalid identifier", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			return null;
		}

		try {
			procedureJavaClass.load();
		} catch (IOException exception) {
			final String[] messages = { "Procedure class could not be loaded.", "An exception occured." };
			final JDialog dialog = new ExceptionDialog(owner, "Procedure class not loaded", messages, exception);
			dialog.pack();
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
			return null;
		}

		return procedureJavaClass;
	}
}