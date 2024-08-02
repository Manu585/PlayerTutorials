package org.bendersdestiny.playertutorials.utils.memory.storage;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
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
	private final String storageType;
	private SQLiteStorage sqliteStorage;
	private MySQLStorage mySQLStorage;

	/**
	 * The {@link Storage} object represents the storage in the PlayerTutorials plugin.
	 * It manages the Storage type, tables and connections.
	 */
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
			this.setupSQLiteStorage();
		}
		this.createTutorialTable();
		this.createAreaTable();
	}

	/**
	 * Sets up the SQLite Storage functionality
	 */
	private void setupSQLiteStorage() {
		File dataFolder = PlayerTutorials.getInstance().getDataFolder();
		File sqliteFile = new File(dataFolder, "storage.db");

		try {
			if (sqliteFile.createNewFile()) {
				PlayerTutorials.getInstance().getLogger().log(Level.INFO, "SQLITE DB file successfully created!");
			}
		} catch (IOException ignored) {}

		sqliteStorage = SQLiteStorage.getInstance(sqliteFile);
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
	 * @throws {@link SQLException}
	 */
	private Connection getConnection() throws SQLException {
		if (storageType.equalsIgnoreCase("sqlite")) {
			return sqliteStorage.getConnection();
		} else {
			return mySQLStorage.getConnection();
		}
	}

	/**
	 * Creates the area table for all created tutorial areas
	 */
	public void createAreaTable() {
        this.connect();
        String query =
				"CREATE TABLE IF NOT EXISTS areas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "tutorial_id INTEGER," +
                "name TEXT NOT NULL," +
                "pointOne TEXT NOT NULL," +
                "pointTwo TEXT NOT NULL," +
                "spawnpoint TEXT NOT NULL," +
                "tasks TEXT NOT NULL," +
                "FOREIGN KEY(tutorial_id) REFERENCES tutorials(id))";
        try (Connection connection = this.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create areas table!", e);
        }
    }

	/**
	 * Creates the tutorial table for all created tutorials
	 */
	public void createTutorialTable() {
		this.connect();
		String query =
				"CREATE TABLE IF NOT EXISTS tutorials (" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name TEXT NOT NULL," +
				"spawnpoint TEXT," +
				"areas TEXT)";
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to create tutorials table!", e);
		}
	}

	/**
	 * Register new area in the database
	 *
	 * @param area Area to register
	 */
	public void registerArea(Area area) {
		this.connect();
		String query = "INSERT INTO areas (id, tutorial_id, name, pointOne, pointTwo, spawnpoint, tasks) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = this.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, area.getId());
			// preparedStatement.setInt(2, area.getTutorial().getId());
			preparedStatement.setString(3, area.getName());
			preparedStatement.setString(4, GeneralMethods.locationToString(area.getPointOne()));
			preparedStatement.setString(5, GeneralMethods.locationToString(area.getPointTwo()));
			preparedStatement.setString(6, GeneralMethods.locationToString(area.getSpawnPoint()));
			preparedStatement.setString(7, area.getTasks().toString()); //TODO: Find way to optimize storing data
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to add area to storage!", e);
		} finally {
			this.disconnect();
		}
	}

	/**
	 * Register a new {@link Tutorial} in the database
	 *
	 * @param tutorial Tutorial to register
	 */
	public void registerTutorial(Tutorial tutorial) {
		this.connect();
		String query = "INSERT INTO tutorials (id, name, spawnpoint, areas) VALUES (?, ?, ?, ?)";
		try (Connection connection = this.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setInt(1, tutorial.getId());
			preparedStatement.setString(2, tutorial.getName());

			if (tutorial.getSpawnPoint() != null) {
				preparedStatement.setString(3, GeneralMethods.locationToString(tutorial.getSpawnPoint()));
			}

			if (tutorial.getAreas() != null) {
				for (int id : tutorial.getAreas().keySet()) {
					preparedStatement.setInt(4, tutorial.getAreas().get(id).getId()); //TODO: Create area data to minimize performance usage
				}
			}
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to add tutorial to storage!", e);
		} finally {
			this.disconnect();
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
}
