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
	private final String username;
	private final String password;
	private final String port;
	private final String host;
	private final String database;

	private Connection connection;

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
