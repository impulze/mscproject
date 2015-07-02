package de.hzg.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configuration {
	public static String DEFAULT_PATH = buildDefaultPath();
	private Map<String, Object> map = new HashMap<String, Object>();
	private static Logger logger = Logger.getLogger(Configuration.class.getName());

	public Configuration() throws ConfigurationSetupException {
		this(DEFAULT_PATH);
	}

	public Configuration(String filename) throws ConfigurationSetupException {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder;
		final Document document;

		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException exception) {
			logger.severe("Unable to create document builder for XML files.");
			throw new ConfigurationSetupException();
		}

		try {
			document = builder.parse(filename);
		} catch (IOException|SAXException exception) {
			final String error = String.format("Unable to read XML file '%s' to create configuration.", filename); 
			logger.severe(error);
			throw new ConfigurationSetupException();
		}

		final Element root = document.getDocumentElement();

		final NodeList databaseList = root.getElementsByTagName("database");

		if (databaseList != null) {
			parseDatabases(databaseList);
		}

		final NodeList sensorClassesList = root.getElementsByTagName("sensor_classes");

		if (sensorClassesList != null) {
			parseSensorClasses(sensorClassesList);
		}
	}

	public DatabaseConfiguration getDatabaseConfiguration(String id) throws ConfigurationNotFound {
		@SuppressWarnings("unchecked")
		final List<DatabaseConfiguration> databaseConfigurations = (List<DatabaseConfiguration>)map.get("database");

		if (databaseConfigurations != null) {
			for (final DatabaseConfiguration databaseConfiguration: databaseConfigurations) {
				if (databaseConfiguration.getId().equals(id)) {
					return databaseConfiguration;
				}
			}
		}

		throw new ConfigurationNotFound(String.format("Database configuration with id '%s' not found.", id));
	}

	private void parseDatabases(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Element element = (Element)nodeList.item(i);
			final String id = element.getAttribute("id");
			final DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(id);

			databaseConfiguration.setDriver(getTextValue(element, "driver"));
			databaseConfiguration.setHost(getTextValue(element, "host"));
			databaseConfiguration.setPort(getIntValue(element, "port"));
			databaseConfiguration.setName(getTextValue(element, "name"));
			databaseConfiguration.setUsername(getTextValue(element, "username"));
			databaseConfiguration.setPassword(getTextValue(element, "password"));

			addObject("database", databaseConfiguration);
		}
	}

	public SensorClassesConfiguration getSensorClassesConfiguration() throws ConfigurationNotFound {
		@SuppressWarnings("unchecked")
		final List<SensorClassesConfiguration> sensorClassesConfigurations = (List<SensorClassesConfiguration>)map.get("sensor_classes");

		if (sensorClassesConfigurations != null) {
			return sensorClassesConfigurations.get(0);
		}

		throw new ConfigurationNotFound(String.format("Sensor classes configuration not found."));
	}

	private void parseSensorClasses(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Element element = (Element)nodeList.item(i);
			final SensorClassesConfiguration sensorClassesConfiguration = new SensorClassesConfiguration();

			final String homeDirectory = System.getProperty("user.home");
			final String sourceDirectory = getTextValue(element, "source_directory");
			sensorClassesConfiguration.setSourceDirectory(sourceDirectory.replace("~", homeDirectory));
			sensorClassesConfiguration.setPackage(getTextValue(element, "package"));

			addObject("sensor_classes", sensorClassesConfiguration);
		}
	}

	static private String getTextValue(Element element, String tagName) {
		final NodeList nodeList = element.getElementsByTagName(tagName);

		if (nodeList != null) {
			final Element subElement = (Element)nodeList.item(0);
			return subElement.getFirstChild().getNodeValue();
		}

		return null;
	}

	private static int getIntValue(Element element, String tagName) {
		return Integer.parseInt(getTextValue(element, tagName));
	}

	private void addObject(String name, Object object) {
		@SuppressWarnings("unchecked")
		List<Object> currentEntries = (List<Object>)map.get(name);

		if (currentEntries == null) {
			currentEntries = new ArrayList<Object>(); 
			map.put(name, currentEntries);
		}

		currentEntries.add(object);
	}

	private static String buildDefaultPath() {
		return String.format("%s%shzg%sconfiguration.xml",
			System.getProperty("user.home"),
			File.separator,
			File.separator);
	}
}