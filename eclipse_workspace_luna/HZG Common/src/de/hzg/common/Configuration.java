package de.hzg.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configuration {
	public static String DEFAULT_PATH = buildDefaultPath();
	private Map<String, Object> map = new HashMap<String, Object>();
	private static Logger logger = Logger.getLogger(Configuration.class.getName());
	private static Configuration instance;

	public Configuration() throws ConfigurationSetupException, Exception {
		this(DEFAULT_PATH);
	}

	public Configuration(String filename) throws ConfigurationSetupException, Exception {
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

		final NodeList observedPropertyClassesList = root.getElementsByTagName("observed_property_classes");

		if (observedPropertyClassesList != null) {
			parseObservedPropertyClasses(observedPropertyClassesList);
		}

		final NodeList httpSenderList = root.getElementsByTagName("http_sender");

		if (httpSenderList != null) {
			parseHTTPSender(httpSenderList);
		}

		final NodeList sosList = root.getElementsByTagName("sos");

		if (sosList != null) {
			parseSOSList(sosList);
		}

		if (instance == null) {
			instance = this;
		}
	}

	public static Configuration getInstance() {
		return instance;
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
			final Node node = nodeList.item(i);

			if (!(node instanceof Element)) {
				continue;
			}

			final Element element = (Element)node;
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

	public ObservedPropertyClassesConfiguration getObservedPropertyClassesConfiguration() throws ConfigurationNotFound {
		@SuppressWarnings("unchecked")
		final List<ObservedPropertyClassesConfiguration> observedPropertyClassesConfigurations = (List<ObservedPropertyClassesConfiguration>)map.get("observed_property_classes");

		if (observedPropertyClassesConfigurations != null) {
			return observedPropertyClassesConfigurations.get(0);
		}

		throw new ConfigurationNotFound(String.format("Observed property classes configuration not found."));
	}

	private void parseObservedPropertyClasses(NodeList nodeList) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);

			if (!(node instanceof Element)) {
				continue;
			}

			final Element element = (Element)node;
			final ObservedPropertyClassesConfiguration observedPropertyClassesConfiguration = new ObservedPropertyClassesConfiguration();

			final String homeDirectory = System.getProperty("user.home");
			final String sourceDirectory = getTextValue(element, "source_directory");
			observedPropertyClassesConfiguration.setSourceDirectory(sourceDirectory.replace("~", homeDirectory));

			addObject("observed_property_classes", observedPropertyClassesConfiguration);
		}
	}

	public HTTPSenderConfiguration getHTTPSenderConfiguration() throws ConfigurationNotFound {
		@SuppressWarnings("unchecked")
		final List<HTTPSenderConfiguration> httpSenderConfigurations = (List<HTTPSenderConfiguration>)map.get("http_sender");

		if (httpSenderConfigurations != null) {
			return httpSenderConfigurations.get(0);
		}

		throw new ConfigurationNotFound(String.format("HTTP Sender configuration not found."));
	}

	private void parseHTTPSender(NodeList nodeList) throws MalformedURLException {
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);

			if (!(node instanceof Element)) {
				continue;
			}

			final Element element = (Element)node;
			final HTTPSenderConfiguration httpSenderConfiguration = new HTTPSenderConfiguration();

			final String basicAuth = getTextValue(element, "basic_auth");
			final String urlString = getTextValue(element, "url");
			final URL url = new URL(urlString);
			final String query = getTextValue(element, "query");

			httpSenderConfiguration.setBasicAuth(basicAuth);
			httpSenderConfiguration.setURL(url);
			httpSenderConfiguration.setQuery(query);

			addObject("http_sender", httpSenderConfiguration);
		}
	}

	public SOSConfiguration getSOSConfiguration() throws ConfigurationNotFound {
		@SuppressWarnings("unchecked")
		final List<SOSConfiguration> sosConfigurations = (List<SOSConfiguration>)map.get("sos");

		if (sosConfigurations != null) {
			return sosConfigurations.get(0);
		}

		throw new ConfigurationNotFound(String.format("SOS configuration not found."));
	}

	private void parseSOSList(NodeList nodeList) {
		Logger LOG = Logger.getLogger("Foom");

		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node node = nodeList.item(i);
			LOG.info("parsing sos: " + node);

			if (!(node instanceof Element)) {
				continue;
			}

			final Element element = (Element)node;
			final SOSConfiguration sosConfiguration = new SOSConfiguration();

			String identifierPrefix = getTextValue(element, "identifier_prefix");

			if (identifierPrefix == null) {
				identifierPrefix = new String();
			} else {
				identifierPrefix += "/";
			}

			sosConfiguration.setProcedureIdentifierPrefix(identifierPrefix);
			sosConfiguration.setOfferingIdentifierPrefix(identifierPrefix);
			sosConfiguration.setFeatureOfInterestIdentifierPrefix(identifierPrefix);
			sosConfiguration.setObservablePropertyIdentifierPrefix(identifierPrefix);

			final NodeList children = element.getChildNodes();

			LOG.info("logging children: " + children);

			if (children == null) {
				continue;
			}

			for (int j = 0; j < children.getLength(); j++) {
				final Node childNode = children.item(j);

				if (!(childNode instanceof Element)) {
					continue;
				}

				final Element childElement = (Element)childNode;

				LOG.info("logging child: " + childElement);
				LOG.info("logging child tag: " + childElement.getTagName());
				if (childElement.getTagName().equals("procedures")) {
					LOG.info("setting procedure identifier");
					final String procedureIdentifierPrefix = getTextValue(childElement, "identifier_prefix");

					if (procedureIdentifierPrefix != null) {
						LOG.info("setting proc id to: " + procedureIdentifierPrefix);
						sosConfiguration.setProcedureIdentifierPrefix(sosConfiguration.getProcedureIdentifierPrefix() + procedureIdentifierPrefix + "/");
					}
				}

				if (childElement.getTagName().equals("offering")) {
					final String offeringIdentifierPrefix = getTextValue(childElement, "identifier_prefix");

					if (offeringIdentifierPrefix != null) {
						sosConfiguration.setOfferingIdentifierPrefix(sosConfiguration.getOfferingIdentifierPrefix() + offeringIdentifierPrefix + "/");
					}

					final String offeringName = getTextValue(childElement, "name");
					sosConfiguration.setOfferingName(offeringName);
				}

				if (childElement.getTagName().equals("feature_of_interest")) {
					final String featureOfInterestIdentifierPrefix = getTextValue(childElement, "identifier_prefix");

					if (featureOfInterestIdentifierPrefix != null) {
						sosConfiguration.setFeatureOfInterestIdentifierPrefix(sosConfiguration.getFeatureOfInterestIdentifierPrefix() + featureOfInterestIdentifierPrefix + "/");
					}

					final String featureOfInterestName = getTextValue(childElement, "name");
					sosConfiguration.setFeatureOfInterestName(featureOfInterestName);
				}

				if (childElement.getTagName().equals("observable_properties")) {
					final String observablePropertyIdentifierPrefix = getTextValue(childElement, "identifier_prefix");

					if (observablePropertyIdentifierPrefix != null) {
						sosConfiguration.setObservablePropertyIdentifierPrefix(sosConfiguration.getObservablePropertyIdentifierPrefix() + observablePropertyIdentifierPrefix + "/");
					}
				}
			}

			if (sosConfiguration.getProcedureIdentifierPrefix().isEmpty() ||
			    sosConfiguration.getOfferingIdentifierPrefix().isEmpty() ||
			    sosConfiguration.getFeatureOfInterestIdentifierPrefix().isEmpty() ||
			    sosConfiguration.getObservablePropertyIdentifierPrefix().isEmpty()) {
				throw new RuntimeException("One of the identifier prefixes is empty.");
			}

			addObject("sos", sosConfiguration);
		}
	}

	static private String getTextValue(Element element, String tagName) {
		if (element == null) {
			return null;
		}

		final NodeList nodeList = element.getElementsByTagName(tagName);

		if (nodeList != null && nodeList.getLength() > 0) {
			final Node subNode = nodeList.item(0);

			if (subNode instanceof Element) {
				final Element subElement = (Element)subNode;
				return subElement.getFirstChild().getNodeValue();
			}
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
