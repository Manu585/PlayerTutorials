package org.bendersdestiny.playertutorials.utils.memory.storage.format;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@Getter
public class MySQLStorage {
	private static MySQLStorage instance;
	protected final String username;
	protected final String password;
	protected final String port;
	protected final String host;
	protected final String database;

	private AtomicBoolean connected = new AtomicBoolean(false);
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

	/**
	 * Instance of the {@link MySQLStorage}
	 *
	 * @param username Database Username
	 * @param password Database Password
	 * @param port Database Port
	 * @param host Database host
	 * @param database Database name
	 *
	 * @return the instance
	 */
	public static MySQLStorage getInstance(String username, String password, String port, String host, String database) {
		if (instance == null) {
			instance = new MySQLStorage(username, password, port, host, database);
		}
		return instance;
	}

	/**
	 * The MySQL way of connecting to the Database
	 */
	public void connect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String connectionString = "jdbc:mysql://" + host + ":" + port + "/" + database;
					connection = DriverManager.getConnection(connectionString, username, password);
					PlayerTutorials.getInstance().getLogger().log(Level.INFO, "MySQL connection established.");
					connected.set(true);
				} catch (SQLException e) {
					PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "MySQL connection failed.", e);
				}
			}
		}.runTaskAsynchronously(PlayerTutorials.getInstance());
	}

	/**
	 * The MySQL way of disconnecting from the Database
	 */
	public void disconnect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (connection != null) {
					try {
						if (!connection.isClosed()) {
							connection.close();
							connected.set(false);
						}
					} catch (SQLException e) {
						PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Error while disconnecting from database!", e);
					}
				}
			}
		}.runTaskAsynchronously(PlayerTutorials.getInstance());
	}
}
