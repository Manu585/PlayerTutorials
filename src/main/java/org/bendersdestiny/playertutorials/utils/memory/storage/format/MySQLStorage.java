package org.bendersdestiny.playertutorials.utils.memory.storage.format;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class MySQLStorage {
	private static MySQLStorage instance;
	protected final String username;
	protected final String password;
	protected final String port;
	protected final String host;
	protected final String database;

	private Connection connection;

	/**
	 * The MySQL Storage type object
	 *
	 * @param username Database Username
	 * @param password Database Password
	 * @param port Database Port
	 * @param host Database Host
	 * @param database Database Name
	 */
	private MySQLStorage(String username, String password, String port, String host, String database) {
		this.username = username;
		this.password = password;
		this.port = port;
		this.host = host;
		this.database = database;
	}

	public static MySQLStorage getInstance(String username, String password, String port, String host, String database) {
		if (instance == null) {
			instance = new MySQLStorage(username, password, port, host, database);
		}
		return instance;
	}

	public void connect() {
		try {
			String connectionString = "jdbc:mysql://" + host + ":" + port + "/" + database;
			connection = DriverManager.getConnection(connectionString, username, password);
			PlayerTutorials.getInstance().getLogger().log(Level.INFO, "MySQL connection established.");
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "MySQL connection failed.", e);
		}
	}

	public void disconnect() {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Error while disconnecting from database!", e);
			}
		}
	}
}
