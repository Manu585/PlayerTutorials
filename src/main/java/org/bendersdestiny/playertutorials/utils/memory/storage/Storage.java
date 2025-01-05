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
import java.util.logging.Level;

@Getter @SuppressWarnings("all")
public class Storage {
	private String storageType;
	private SQLiteStorage sqliteStorage;
	private MySQLStorage mySQLStorage;

	public Storage() {
		this.storageType = Objects.requireNonNull(ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.type")).equalsIgnoreCase("mysql")
				? "mysql"
				: "sqlite";

		if (this.storageType.equalsIgnoreCase("sqlite")) {
			this.setupSQLiteStorage();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			this.setupMySQLStorage();
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.WARNING,
					"Wrong option in config.yml 'playertutorials.storage.type'. " +
							"Viable options: 'mysql' or 'sqlite'! Defaulting to 'sqlite'.");
			this.storageType = "sqlite";
			this.setupSQLiteStorage();
		}

		this.createAllTables();
	}

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

	private void setupMySQLStorage() {
		String username = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.username");
		String password = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.password");
		String database = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.database");
		String host = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.host");
		String port = ConfigManager.defaultConfig.getConfig().getString("playertutorials.storage.mysql.port");

		this.mySQLStorage = MySQLStorage.getInstance(username, password, port, host, database);
	}

	public Connection getConnection() throws SQLException {
		if (this.storageType.equalsIgnoreCase("sqlite")) {
			return this.sqliteStorage.getConnection();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			return this.mySQLStorage.getConnection();
		} else {
			throw new NullPointerException("Error while receiving the connection from the database!");
		}
	}

	public void disconnect() {
		if (this.storageType.equalsIgnoreCase("sqlite")) {
			this.sqliteStorage.disconnect();
		} else if (this.storageType.equalsIgnoreCase("mysql")) {
			this.mySQLStorage.disconnect();
		}
	}

	private void createAllTables() {
		this.createTutorialTable();
		this.createAreaTable();
		this.createStructuresTable();
		this.createTasksTable();
		this.createCommandTaskTable();
		this.createTeleportTaskTable();
		this.createAreaTasksTable();
		this.createAreaBlocksTable();
	}

	/**
	 * Returns the proper auto-increment syntax depending on the DB type.
	 */
	private String getPrimaryKeyAutoIncrement() {
		if (this.storageType.equalsIgnoreCase("sqlite")) {
			return "INTEGER PRIMARY KEY AUTOINCREMENT";
		} else {
			// MySQL
			return "INT AUTO_INCREMENT PRIMARY KEY";
		}
	}

	/**
	 * Creates the tutorial table for all created tutorials
	 */
	public void createTutorialTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS tutorials (" +
						"  id INTEGER PRIMARY KEY AUTOINCREMENT," +
						"  name VARCHAR(255) NOT NULL," +
						"  icon VARCHAR(41) DEFAULT 'DIORITE'" +
						")";
		String mysql =
				"CREATE TABLE IF NOT EXISTS tutorials (" +
						"  id INT AUTO_INCREMENT PRIMARY KEY," +
						"  name VARCHAR(255) NOT NULL," +
						"  icon VARCHAR(41) DEFAULT 'DIORITE'" +
						") ENGINE=InnoDB";

		runStatement(this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create tutorials table!");
	}

	/**
	 * Creates the area table for all created tutorial areas
	 */
	public void createAreaTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS areas (" +
						"  areaID INTEGER PRIMARY KEY AUTOINCREMENT," +
						"  tutorialID INTEGER," +
						"  structureID INTEGER," +
						"  name VARCHAR(255)," +
						"  spawnPoint TEXT," +
						"  priority INTEGER," +
						"  FOREIGN KEY(tutorialID) REFERENCES tutorials(id) ON DELETE CASCADE" +
						")";

		String mysql =
				"CREATE TABLE IF NOT EXISTS areas (" +
						"  areaID INT AUTO_INCREMENT PRIMARY KEY," +
						"  tutorialID INT," +
						"  structureID INT," +
						"  name VARCHAR(255)," +
						"  spawnPoint TEXT," +
						"  priority INT," +
						"  FOREIGN KEY(tutorialID) REFERENCES tutorials(id) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create areas table!");
	}

	/**
	 * Creates the structure table
	 */
	public void createStructuresTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS structures (" +
						"  structureID INTEGER PRIMARY KEY AUTOINCREMENT," +
						"  areaID INTEGER NOT NULL," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE" +
						")";

		String mysql =
				"CREATE TABLE IF NOT EXISTS structures (" +
						"  structureID INT AUTO_INCREMENT PRIMARY KEY," +
						"  areaID INT NOT NULL," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(
				this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create structures table!"
		);
	}

	/**
	 * Creates the tasks table
	 */
	public void createTasksTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS tasks (" +
						"  taskID INTEGER PRIMARY KEY AUTOINCREMENT," +
						"  areaID INTEGER NOT NULL," +
						"  type VARCHAR(50)," +
						"  priority INTEGER NOT NULL," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE" +
						")";
		String mysql =
				"CREATE TABLE IF NOT EXISTS tasks (" +
						"  taskID INT AUTO_INCREMENT PRIMARY KEY," +
						"  areaID INT NOT NULL," +
						"  type VARCHAR(50)," +
						"  priority INT NOT NULL," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create tasks table!");
	}

	/**
	 * Creates the teleport tasks table
	 */
	public void createCommandTaskTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS command_tasks (" +
						"  taskID INTEGER PRIMARY KEY," +
						"  required_command TEXT," +
						"  FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE" +
						")";
		String mysql =
				"CREATE TABLE IF NOT EXISTS command_tasks (" +
						"  taskID INT PRIMARY KEY," +
						"  required_command TEXT," +
						"  FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create command tasks table!");
	}

	/**
	 * Creates the command tasks table
	 */
	public void createTeleportTaskTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS teleport_tasks (" +
						"  taskID INTEGER PRIMARY KEY," +
						"  fromLocation TEXT," +
						"  toLocation TEXT," +
						"  FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE" +
						")";
		String mysql =
				"CREATE TABLE IF NOT EXISTS teleport_tasks (" +
						"  taskID INT PRIMARY KEY," +
						"  fromLocation TEXT," +
						"  toLocation TEXT," +
						"  FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create teleport tasks table!");
	}

	/**
	 * Creates the area_tasks bridging table to link areas and tasks
	 */
	public void createAreaTasksTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS area_tasks (" +
						"  areaID INTEGER NOT NULL," +
						"  taskID INTEGER NOT NULL," +
						"  PRIMARY KEY (areaID, taskID)," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE," +
						"  FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE" +
						")";
		String mysql =
				"CREATE TABLE IF NOT EXISTS area_tasks (" +
						"  areaID INT NOT NULL," +
						"  taskID INT NOT NULL," +
						"  PRIMARY KEY (areaID, taskID)," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE," +
						"  FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create area_tasks table!");
	}

	public void createAreaBlocksTable() {
		String sqlite =
				"CREATE TABLE IF NOT EXISTS area_blocks (" +
						"  areaID INTEGER NOT NULL," +
						"  relativeX INT NOT NULL," +
						"  relativeY INT NOT NULL," +
						"  relativeZ INT NOT NULL," +
						"  blockType VARCHAR(50) NOT NULL," +
						"  PRIMARY KEY (areaID, relativeX, relativeY, relativeZ)," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE" +
						")";

		String mysql =
				"CREATE TABLE IF NOT EXISTS area_blocks (" +
						"  areaID INT NOT NULL," +
						"  relativeX INT NOT NULL," +
						"  relativeY INT NOT NULL," +
						"  relativeZ INT NOT NULL," +
						"  blockType VARCHAR(50) NOT NULL," +
						"  PRIMARY KEY (areaID, relativeX, relativeY, relativeZ)," +
						"  FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE" +
						") ENGINE=InnoDB";

		runStatement(
				this.storageType.equalsIgnoreCase("sqlite") ? sqlite : mysql,
				"Failed to create area_blocks table!"
		);
	}

	private void runStatement(String sql, String errorMessage) {
		try (Connection connection = this.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, errorMessage, e);
		}
	}
}
