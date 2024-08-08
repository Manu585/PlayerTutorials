package org.bendersdestiny.playertutorials.utils.memory.storage;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.utils.memory.storage.format.MySQLStorage;
import org.bendersdestiny.playertutorials.utils.memory.storage.format.SQLiteStorage;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;

@Getter
public class Storage {
	private String storageType;
	private SQLiteStorage sqliteStorage;
	private MySQLStorage mySQLStorage;

	/**
	 * The {@link Storage} object represents the storage in the PlayerTutorials plugin.
	 * It manages the Storage type, tables and connections.
	 */
	@SuppressWarnings("all") // For the 'else if' part.
	public Storage() {
		this.storageType = Objects.requireNonNull(ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.type")).equalsIgnoreCase("mysql")
				? "mysql"
				: "sqlite";

		if (storageType.equalsIgnoreCase("sqlite")) {
			this.setupSQLiteStorage();
		} else if (storageType.equalsIgnoreCase("mysql")) {
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
		sqliteStorage = SQLiteStorage.getInstance(sqliteFile);
	}

	/**
	 * Sets up the MYSQL Storage functionality
	 */
	private void setupMySQLStorage() {
		String username = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.username");
		String password = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.password");
		String database = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.database");
		String host 	= ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.host");
		String port 	= ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.port");

		mySQLStorage = MySQLStorage.getInstance(username, password, port, host, database);
	}

	/**
	 * Connect to the database depending on storage type
	 */
	public void connect() {
		if (storageType.equalsIgnoreCase("sqlite")) {
			sqliteStorage.connect();
		} else if (storageType.equalsIgnoreCase("mysql")) {
			mySQLStorage.connect();
		}
	}

	/**
	 * Disconnect from the database depending on storage type
	 */
	public void disconnect() {
		if (storageType.equalsIgnoreCase("sqlite")) {
			sqliteStorage.disconnect();
		} else if (storageType.equalsIgnoreCase("mysql")) {
			mySQLStorage.disconnect();
		}
	}

	/**
	 * Gets the {@link Connection} object depending on the storage type
	 *
	 * @return the {@link Connection} object depending on storage type
	 * @throws SQLException if there is any kind of error
	 */
	public Connection getConnection() throws SQLException {
		if (storageType.equalsIgnoreCase("sqlite")) {
			return sqliteStorage.getConnection();
		} else if (storageType.equalsIgnoreCase("mysql")) {
			return mySQLStorage.getConnection();
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
		this.createTeleportTaskTable();
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
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create tutorials table!", e);
		} finally {
			this.disconnect();
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
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create areas table!", e);
        } finally {
			this.disconnect();
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
						"FOREIGN KEY(areaID) REFERENCES areas(areaID" +
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
						"areaID INTEGER," +
						"type VARCHAR(50)," + // 'CommandTask' or 'TeleportTask' or ...
						"priority INTEGER," +
						"FOREIGN KEY(areaID) REFERENCES areas(areaID)" +
						")";
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create tasks table!", e);
		} finally {
			this.disconnect();
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
						"priority INTEGER," +
						"FOREIGN KEY(taskID) REFERENCES tasks(taskID)" +
						")";
		try (Connection connection = this.getConnection();
			Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create teleport tasks table!", e);
		} finally {
			this.disconnect();
		}
	}

	/**
	 * Save an area to the DB
	 *
	 * @param area Area to save
	 */
	public void saveArea(Area area) {
		this.connect();
		String query = "INSERT INTO areas (areaid, schematicfile, name, spawnpoint, tasks, priority) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = this.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, area.getAreaID());
			preparedStatement.setString(3, String.valueOf(area.getStructure().getStructureSchematic()));
			preparedStatement.setString(4, area.getName());
			preparedStatement.setString(5, GeneralMethods.locationToString(area.getSpawnPoint()));
			preparedStatement.setString(6, area.getTasks().toString());
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to save area to storage!", e);
		}
	}

	/**
	 * Get the ID for the corresponding Area
	 *
	 * @param areaName The Name of the area
	 * @return the areas ID
	 */
	public int getIdForArea(String areaName) {
		String query = "SELECT id FROM areas WHERE name = ?";
		this.connect();
		try (Connection connection = this.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, areaName);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Failed to gather ID for area");
		} finally {
			this.disconnect();
		}
		return 0;
	}

	/**
	 * Check if an area with that specific id already exists
	 *
	 * @param id ID to check
	 * @return true if areaid already exists
	 */
	public boolean idForAreaExisting(int id) {
		String query = "SELECT ID FROM areas WHERE id = ?";
		this.connect();
		try (Connection connection = this.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
			}
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Failed to gather ID for area");
		}
		return false;
	}
}
