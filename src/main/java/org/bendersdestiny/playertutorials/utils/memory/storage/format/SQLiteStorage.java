package org.bendersdestiny.playertutorials.utils.memory.storage.format;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class SQLiteStorage {
	private static SQLiteStorage instance;
	private final File file;
	private Connection connection;

	/**
	 * The SQLite Storage type
	 *
	 * @param file .db file
	 */
	private SQLiteStorage(File file) {
		this.file = file;
	}

	public static SQLiteStorage getInstance(File file) {
		if (instance == null) {
			instance = new SQLiteStorage(file);
		}
		return instance;
	}

	public void connect() {
		try {
			String connectionString = "jdbc:sqlite:" + file.getAbsolutePath();
			connection = DriverManager.getConnection(connectionString);
			PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully connected to local database!");
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to connect to local database!" + e.getMessage());
		}
	}

	public void disconnect() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to close local database!" + e.getMessage());
		}
	}
}
