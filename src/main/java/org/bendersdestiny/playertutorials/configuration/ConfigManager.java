package org.bendersdestiny.playertutorials.configuration;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class ConfigManager {
	public static Config defaultConfig;

	public ConfigManager(PlayerTutorials plugin) {
		defaultConfig = new Config(plugin, "config.yml");

		configCheck();
	}

	private void configCheck() {
		FileConfiguration config = defaultConfig.get();
		config.addDefault("playertutorials.storage.type", "sqlite");
		config.addDefault("playertutorials.storage.mysql.username", "username");
		config.addDefault("playertutorials.storage.mysql.password", "password");
		config.addDefault("playertutorials.storage.mysql.database", "database");
		config.addDefault("playertutorials.storage.mysql.host", "localhost");
		config.addDefault("playertutorials.storage.mysql.port", "3306");

		defaultConfig.save();
	}
}
