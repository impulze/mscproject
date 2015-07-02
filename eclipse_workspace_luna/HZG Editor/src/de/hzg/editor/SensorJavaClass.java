package de.hzg.editor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.ToolProvider;

import de.hzg.common.SensorClassesConfiguration;

public class SensorJavaClass {
	private String name;
	private String nameLoaded;
	private String text;
	private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private static final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	private static final JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
	private final SensorClassesConfiguration sensorClassesConfiguration;

	SensorJavaClass(SensorClassesConfiguration sensorClassesConfiguration) {
		this.sensorClassesConfiguration = sensorClassesConfiguration;
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
		text = load(sensorClassesConfiguration, name);
		nameLoaded = name;
	}

	public void save() throws IOException {
		save(sensorClassesConfiguration, name, text);
		nameLoaded = name;
	}

	public void deleteLoadedInstance() throws IOException {
		if (nameLoaded != null) {
			delete(sensorClassesConfiguration, nameLoaded);
			nameLoaded = null;
		}
	}

	private static String load(SensorClassesConfiguration sensorClassesConfiguration, String name) throws IOException {
		final String inputPath = getInputPath(sensorClassesConfiguration, name);
		final byte[] rawText = Files.readAllBytes(Paths.get(inputPath));
		return new String(rawText, Charset.defaultCharset());
	}

	private static void save(SensorClassesConfiguration sensorClassesConfiguration, String name, String text) throws IOException {
		final String inputPath = getInputPath(sensorClassesConfiguration, name); 
		final byte[] rawText = text.getBytes();
		Files.write(Paths.get(inputPath), rawText);
	}

	private static void delete(SensorClassesConfiguration sensorClassesConfiguration, String name) throws IOException {
		final String inputPath = getInputPath(sensorClassesConfiguration, name); 
		Files.delete(Paths.get(inputPath));
	}

	public void compile() {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	}

	private static String getInputPath(SensorClassesConfiguration sensorClassesConfiguration, String name) {
		final String sourceDirectory = sensorClassesConfiguration.getSourceDirectory();
		final String packageDirectory = sensorClassesConfiguration.getPackage().replace('.', File.separatorChar);

		return sourceDirectory + File.separatorChar + packageDirectory + File.separatorChar + name + Kind.SOURCE.extension;
	}

	public static String getCompilerInvocation() {
		return "java -jar foo/bar/baz";
	}

	public static String getTemplate() {
		return "";
	}
}