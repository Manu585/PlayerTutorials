package org.bendersdestiny.playertutorials.utils.memory.storage.format;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class MySQLStorage {
	private static MySQLStorage instance;
	protected final String username;
	protected final String password;
	protected final String port;
	protected final String host;
	protected final String database;

	private final HikariDataSource dataSource;
	private Connection connection;

	private MySQLStorage(String username, String password, String port, String host, String database) {
		this.username = username;
		this.password = password;
		this.port = port;
		this.host = host;
		this.database = database;

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
		config.setUsername(username);
		config.setPassword(password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		this.dataSource = new HikariDataSource(config);
	}

	public static MySQLStorage getInstance(String username, String password, String port, String host, String database) {
		if (instance == null) {
			instance = new MySQLStorage(username, password, port, host, database);
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
				PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Error while disconnecting from database!", e);
			}
		}

		if (dataSource != null) {
			dataSource.close();
		}
	}
}
