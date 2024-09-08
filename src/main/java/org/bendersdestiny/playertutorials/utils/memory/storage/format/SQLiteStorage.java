package org.bendersdestiny.playertutorials.utils.memory.storage.format;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class SQLiteStorage {
	private static SQLiteStorage instance;
	private final File file;
	private final HikariDataSource dataSource;
	private Connection connection;

	private SQLiteStorage(File file) {
		this.file = file;

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());

		this.dataSource = new HikariDataSource(config);
	}

	public static SQLiteStorage getInstance(File file) {
		if (instance == null) {
			instance = new SQLiteStorage(file);
		}
		return instance;
	}

	public Connection getConnection() throws SQLException {
		// Only request connection from the pool when needed
		if (connection == null || connection.isClosed()) {
			connection = dataSource.getConnection();
		}
		return connection;
	}

	public void disconnect() {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Failed to close local database!" + e.getMessage());
			}
		}

		if (dataSource != null) {
			dataSource.close();
		}
	}
}
