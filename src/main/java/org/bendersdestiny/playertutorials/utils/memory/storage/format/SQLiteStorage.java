package org.bendersdestiny.playertutorials.utils.memory.storage.format;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@Getter
public class SQLiteStorage {
	private static SQLiteStorage instance;
	private final File file;
	private Connection connection;

	private final AtomicBoolean connected = new AtomicBoolean(false);

	/**
	 * The SQLite Storage type
	 *
	 * @param file .db file
	 */
	private SQLiteStorage(File file) {
		this.file = file;
	}

	/**
	 * Instance of the {@link SQLiteStorage}
	 *
	 * @param file .db {@link File}
	 *
	 * @return the instance
	 */
	public static SQLiteStorage getInstance(File file) {
		if (instance == null) {
			instance = new SQLiteStorage(file);
		}
		return instance;
	}

	/**
	 * The SQLite way of connecting to the database
	 */
	public void connect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String connectionString = "jdbc:sqlite:" + file.getAbsolutePath();
					connection = DriverManager.getConnection(connectionString);
					PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully connected to local database!");
					connected.set(true);
				} catch (SQLException e) {
					PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to connect to local database!" + e.getMessage());
				}
			}
		}.runTaskAsynchronously(PlayerTutorials.getInstance());
	}

	/**
	 * The SQLite way of disconnecting from the database
	 */
	public void disconnect() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (connection != null && !connection.isClosed()) {
						connection.close();
						connected.set(false);
					}
				} catch (SQLException e) {
					PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to close local database!" + e.getMessage());
				}
			}
		}.runTaskAsynchronously(PlayerTutorials.getInstance());
	}
}
