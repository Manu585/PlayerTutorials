package org.bendersdestiny.playertutorials.utils.memory.storage;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.format.MySQLStorage;
import org.bendersdestiny.playertutorials.utils.memory.storage.format.SQLiteStorage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

@Getter
public class Storage {
	private final String storageType;
	private final SQLiteStorage sqliteStorage;
	private final MySQLStorage mySQLStorage;

	public Storage() {
		String configuredType = ConfigManager.defaultConfig.getConfig()
				.getString("playertutorials.storage.type", "sqlite");

		this.storageType = (configuredType.equalsIgnoreCase("mysql")) ? "mysql" : "sqlite";

		if (this.storageType.equals("sqlite")) {
			this.sqliteStorage = this.setupSQLiteStorage();
			this.mySQLStorage = null;
		} else {
			this.mySQLStorage = this.setupMySQLStorage();
			this.sqliteStorage = null;
		}

		this.createAllTables();
	}

	private SQLiteStorage setupSQLiteStorage() {
		File sqliteFile = new File(PlayerTutorials.getInstance().getDataFolder(), "storage.db");
		try {
			if (sqliteFile.createNewFile()) {

				PlayerTutorials.getInstance().getLogger().log(
						Level.INFO,
						ChatUtil.translateString("SQLITE DB file successfully created!")
				);

			}
		} catch (IOException e) {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError creating the .db file!"),
					e
			);

		}
		return SQLiteStorage.getInstance(sqliteFile);
	}

	private MySQLStorage setupMySQLStorage() {
		String username = ConfigManager.defaultConfig.getConfig()
				.getString("playertutorials.storage.mysql.username");
		String password = ConfigManager.defaultConfig.getConfig()
				.getString("playertutorials.storage.mysql.password");
		String database = ConfigManager.defaultConfig.getConfig()
				.getString("playertutorials.storage.mysql.database");
		String host = ConfigManager.defaultConfig.getConfig()
				.getString("playertutorials.storage.mysql.host");
		String port = ConfigManager.defaultConfig.getConfig()
				.getString("playertutorials.storage.mysql.port");

		return MySQLStorage.getInstance(username, password, port, host, database);
	}

	public Connection getConnection() throws SQLException {
		if ("sqlite".equalsIgnoreCase(this.storageType)) {
			return this.sqliteStorage.getConnection();
		} else if ("mysql".equalsIgnoreCase(this.storageType)) {
			return this.mySQLStorage.getConnection();
		}
		throw new SQLException("No valid database connection type found!");
	}

	public void disconnect() {
		try {
			if (this.sqliteStorage != null) this.sqliteStorage.disconnect();
			if (this.mySQLStorage != null) this.mySQLStorage.disconnect();
		} catch (Exception e) {

			PlayerTutorials.getInstance().getLogger().log(
					Level.WARNING,
					ChatUtil.translateString("&cError closing DB connection: "),
					e
			);

		}
	}

	private void createAllTables() {
		createTutorialTable();
		createAreaTable();
		createTasksTable();
		createCommandTaskTable();
		createTeleportTaskTable();
		createAreaTasksTable();
		createAreaBlocksTable();
	}

	private void runStatement(String sql, String errorMessage) {
		try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString(errorMessage),
					e
			);

		}
	}

	public void createTutorialTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS tutorials (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              name VARCHAR(255) NOT NULL,
              icon VARCHAR(41) DEFAULT 'DIORITE'
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS tutorials (
              id INT AUTO_INCREMENT PRIMARY KEY,
              name VARCHAR(255) NOT NULL,
              icon VARCHAR(41) DEFAULT 'DIORITE'
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create tutorials table!");
	}

	public void createAreaTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS areas (
              areaID INTEGER PRIMARY KEY AUTOINCREMENT,
              tutorialID INTEGER,
              name VARCHAR(255),
              spawnPoint TEXT,
              priority INTEGER,
              FOREIGN KEY(tutorialID) REFERENCES tutorials(id) ON DELETE CASCADE
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS areas (
              areaID INT AUTO_INCREMENT PRIMARY KEY,
              tutorialID INT,
              name VARCHAR(255),
              spawnPoint TEXT,
              priority INT,
              FOREIGN KEY(tutorialID) REFERENCES tutorials(id) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create areas table!");
	}

	public void createTasksTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS tasks (
              taskID INTEGER PRIMARY KEY AUTOINCREMENT,
              areaID INTEGER NOT NULL,
              type VARCHAR(50),
              priority INTEGER NOT NULL,
              FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS tasks (
              taskID INT AUTO_INCREMENT PRIMARY KEY,
              areaID INT NOT NULL,
              type VARCHAR(50),
              priority INT NOT NULL,
              FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create tasks table!");
	}

	public void createCommandTaskTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS command_tasks (
              taskID INTEGER PRIMARY KEY,
              required_command TEXT,
              FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS command_tasks (
              taskID INT PRIMARY KEY,
              required_command TEXT,
              FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create command tasks table!");
	}

	public void createTeleportTaskTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS teleport_tasks (
              taskID INTEGER PRIMARY KEY,
              fromLocation TEXT,
              toLocation TEXT,
              FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS teleport_tasks (
              taskID INT PRIMARY KEY,
              fromLocation TEXT,
              toLocation TEXT,
              FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create teleport tasks table!");
	}

	public void createAreaTasksTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS area_tasks (
              areaID INTEGER NOT NULL,
              taskID INTEGER NOT NULL,
              PRIMARY KEY (areaID, taskID),
              FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE,
              FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS area_tasks (
              areaID INT NOT NULL,
              taskID INT NOT NULL,
              PRIMARY KEY (areaID, taskID),
              FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE,
              FOREIGN KEY(taskID) REFERENCES tasks(taskID) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create area_tasks table!");
	}

	public void createAreaBlocksTable() {
		String sqlite = """
            CREATE TABLE IF NOT EXISTS area_blocks (
              areaID INTEGER NOT NULL,
              relativeX INT NOT NULL,
              relativeY INT NOT NULL,
              relativeZ INT NOT NULL,
              blockType VARCHAR(50) NOT NULL,
              PRIMARY KEY (areaID, relativeX, relativeY, relativeZ),
              FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE
            )
            """;
		String mysql = """
            CREATE TABLE IF NOT EXISTS area_blocks (
              areaID INT NOT NULL,
              relativeX INT NOT NULL,
              relativeY INT NOT NULL,
              relativeZ INT NOT NULL,
              blockType VARCHAR(50) NOT NULL,
              PRIMARY KEY (areaID, relativeX, relativeY, relativeZ),
              FOREIGN KEY(areaID) REFERENCES areas(areaID) ON DELETE CASCADE
            ) ENGINE=InnoDB
            """;

		runStatement(this.storageType.equals("sqlite") ? sqlite : mysql,
				"Failed to create area_blocks table!");
	}
}
