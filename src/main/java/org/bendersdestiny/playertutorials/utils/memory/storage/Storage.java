package org.bendersdestiny.playertutorials.utils.memory.storage;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.utils.memory.storage.format.MySQLStorage;
import org.bendersdestiny.playertutorials.utils.memory.storage.format.SQLiteStorage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@Getter
public class Storage {
	private String storageType;
	private SQLiteStorage sqliteStorage;
	private MySQLStorage mySQLStorage;

	public static AtomicBoolean tableCreationComplete = new AtomicBoolean(false);

	/**
	 * The {@link Storage} object represents the storage in the PlayerTutorials plugin.
	 * It manages the Storage type, tables and connections.
	 */
	public Storage() {
		this.storageType = Objects.requireNonNull(ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.type")).equalsIgnoreCase("mysql")
				? "mysql"
				: "sqlite";

		if (this.storageType.equalsIgnoreCase("sqlite")) {
			this.setupSQLiteStorage();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			this.setupMySQLStorage();
		} else { // Wrong config input case!
			PlayerTutorials.getInstance().getLogger().log(
					Level.WARNING,
					"Wrong option in config.yml 'playertutorials.storage.type'." +
					"Viable options: 'mysql' or 'sqlite'! Defaulting to 'sqlite'.");
			this.storageType = "sqlite";
			this.setupSQLiteStorage();
		}
		this.createAllTables();
	}

	/**
	 * Sets up the SQLite Storage functionality
	 */
	private void setupSQLiteStorage() {
		File sqliteFile = new File(PlayerTutorials.getInstance().getDataFolder(), "storage.db");
		try {
			if (sqliteFile.createNewFile()) {
				PlayerTutorials.getInstance().getLogger().log(Level.INFO, "SQLITE DB file successfully created!");
			}
		} catch (IOException ex) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Error while creating the .db file!" + ex);
		}
		this.sqliteStorage = SQLiteStorage.getInstance(sqliteFile);
	}

	/**
	 * Sets up the MYSQL Storage functionality
	 */
	private void setupMySQLStorage() {
		String username = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.username");
		String password = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.password");
		String database = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.database");
		String host = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.host");
		String port = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.port");

		this.mySQLStorage = MySQLStorage.getInstance(username, password, port, host, database);
	}

	/**
	 * Connect to the database depending on storage type
	 */
	public void connect() {
		if (this.storageType.equalsIgnoreCase("sqlite")) {
			this.sqliteStorage.connect();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			this.mySQLStorage.connect();
		}
	}

	/**
	 * Disconnect from the database depending on storage type
	 */
	public void disconnect() {
		if (this.storageType.equalsIgnoreCase("sqlite")) {
			this.sqliteStorage.disconnect();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			this.mySQLStorage.disconnect();
		}
	}

	/**
	 * Gets the {@link Connection} object depending on the storage type
	 *
	 * @return the {@link Connection} object depending on storage type
	 * @throws SQLException if there is any kind of error
	 */
	public Connection getConnection() throws SQLException {
		if (this.storageType.equalsIgnoreCase("sqlite")) {
			return this.sqliteStorage.getConnection();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			return this.mySQLStorage.getConnection();
		} else {
			throw new NullPointerException("Error while receiving the connection from the database!");
		}
	}

	/**
	 * Helper method to create all necessary tables with
	 */
	private void createAllTables() {
		this.createTutorialTable();
		this.createAreaTable();
		this.createStructuresTable();
		this.createTasksTable();
		this.createCommandTaskTable();
		this.createTeleportTaskTable();

		tableCreationComplete.set(true);
	}

	/**
	 * Creates the tutorial table for all created tutorials
	 */
	public void createTutorialTable() {
		this.connect();
		String query =
				"CREATE TABLE IF NOT EXISTS tutorials (" +
						"id INTEGER PRIMARY KEY," +
						"name VARCHAR(255) NOT NULL," +
						"icon VARCHAR(41) DEFAULT 'DIORITE'" +
						")";
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			this.disconnect();
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create tutorials table!", e);
		}
	}

	/**
	 * Creates the area table for all created tutorial areas
	 */
	public void createAreaTable() {
        this.connect();
        String query =
				"CREATE TABLE IF NOT EXISTS areas (" +
						"areaID INTEGER PRIMARY KEY," +
						"tutorialID INTEGER," +
						"structureID INTEGER," +
						"name VARCHAR(255)," +
						"spawnPoint TEXT," +
						"tasks TEXT," +
						"priority INTEGER," +
						"FOREIGN KEY(tutorialID) REFERENCES tutorials(id)" +
						")";
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
			this.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create areas table!", e);
        }
    }

	/**
	 * Creates the structure table
	 */
	public void createStructuresTable() {
		this.connect();
		String query =
				"CREATE TABLE IF NOT EXISTS structures (" +
						"structureID INTEGER PRIMARY KEY," +
						"areaID INTEGER," +
						"schematic TEXT," +
						"FOREIGN KEY(areaID) REFERENCES areas(areaID)" +
						")";
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create structures table!", e);
		}
	}

	/**
	 * Creates the tasks table
	 */
	public void createTasksTable() {
		this.connect();
		String query =
				"CREATE TABLE IF NOT EXISTS tasks (" +
						"taskID INTEGER PRIMARY KEY," +
						"areaID INTEGER NOT NULL," +
						"type VARCHAR(50)," + // 'CommandTask' or 'TeleportTask' or ...
						"priority INTEGER NOT NULL," +
						"FOREIGN KEY(areaID) REFERENCES areas(areaID)" +
						")";
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			this.disconnect();
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create tasks table!", e);
		}
	}

	/**
	 * Create the teleport tasks table
	 */
	public void createTeleportTaskTable() {
		this.connect();
		String query =
				"CREATE TABLE IF NOT EXISTS teleport_tasks (" +
						"taskID INTEGER PRIMARY KEY," +
						"fromLocation TEXT," +
						"toLocation TEXT," +
						"FOREIGN KEY(taskID) REFERENCES tasks(taskID)" +
						")";
		try (Connection connection = this.getConnection();
			Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			this.disconnect();
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create teleport tasks table!", e);
		}
	}

	/**
	 * Create the command tasks table
	 */
	public void createCommandTaskTable() {
		this.connect();
		String query =
				"CREATE TABLE IF NOT EXISTS command_tasks (" +
						"taskID INTEGER PRIMARY KEY," +
						"required_command TEXT," +
						"FOREIGN KEY(taskID) REFERENCES tasks(taskID)" +
						")";
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			this.disconnect();
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create command tasks table!", e);
		}
	}
}
