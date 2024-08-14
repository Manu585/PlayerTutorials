package org.bendersdestiny.playertutorials.configuration;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ConfigManager {
	public static Config defaultConfig;
	public static Config languageConfig;

	public ConfigManager(PlayerTutorials plugin) {
		defaultConfig = new Config(plugin, "config.yml");
		languageConfig = new Config(plugin, "language.yml");

		configCheck();
	}

	/**
	 * Puts values into the {@link Config}
	 */
	private void configCheck() {
		FileConfiguration config = defaultConfig.get();
		config.addDefault("playertutorials.storage.type", "sqlite");
		config.addDefault("playertutorials.storage.mysql.username", "username");
		config.addDefault("playertutorials.storage.mysql.password", "password");
		config.addDefault("playertutorials.storage.mysql.database", "database");
		config.addDefault("playertutorials.storage.mysql.host", "localhost");
		config.addDefault("playertutorials.storage.mysql.port", "3306");
		defaultConfig.save();

		FileConfiguration language = languageConfig.get();
		language.addDefault("playertutorials.items.areaselector.name", "&6AreaSelector");

		List<String> lore = new ArrayList<>();
		lore.add("&6Left Click to select first position");
		lore.add("&6Right Click to select second position");

		language.addDefault("playertutorials.items.areaselector.lore", lore);
		language.addDefault("playertutorials.items.areaselector.material", "WOODEN_AXE");
		languageConfig.save();
	}
}
