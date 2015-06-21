package de.hzg.sensors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class ProbeDatabase {
	private final DataSource dataSource;
	private static final Logger logger = Logger.getLogger(ProbeDatabase.class.getName());

	public ProbeDatabase(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// create an active probe by its resultset from a query (where active = true)
	private void addActiveProbe(List<Probe> activeProbes, ResultSet resultSet) {
		//final int id;
		final String name;
		final String device;

		try {
			//id = resultSet.getInt("id");
			name = resultSet.getString("name");
			device = resultSet.getString("device");
		} catch (SQLException exception) {
			logger.severe("Error while trying to fetch necessary fields to create probe instance.");
			logger.severe(exception.toString());
			return;
		}

		final Probe activeProbe = new Probe(name, device);
		activeProbes.add(activeProbe);
	}

	public List<Probe> getActiveProbes() {
		final List<Probe> activeProbes = new LinkedList<Probe>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		final String query = "SELECT * FROM probes WHERE active = true";

		try {
			connection = dataSource.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			connection.close();

			while (resultSet.next()) {
				addActiveProbe(activeProbes, resultSet);
			}
		} catch (SQLException exception) {
			logger.severe("Unable to use database to determine active probes.");
			logger.severe(exception.toString());
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connection != null) {
					connection.close();
				}
			} catch (SQLException exception) {
				logger.severe("Unable to close database connection while determining active probes.");
				logger.severe(exception.toString());
			}
		}

		return activeProbes;
	}
}