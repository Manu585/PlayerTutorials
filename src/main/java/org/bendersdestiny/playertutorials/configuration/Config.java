package org.bendersdestiny.playertutorials.configuration;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Getter
public class Config {
	private final File configFile;
	private final FileConfiguration config;
	private final PlayerTutorials plugin;

	public Config(PlayerTutorials plugin, String fileName) {
		this.plugin = plugin;

		configFile = new File(plugin.getDataFolder(), fileName);

		if (!configFile.exists()) {
			try {
				if (configFile.getParentFile().mkdirs()) {
					plugin.getLogger().log(Level.INFO, "Successfully created necessary directories!");
				}
				plugin.saveResource(fileName, false);
				plugin.getLogger().log(Level.INFO, "Successfully created config file: " + fileName);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to create config file: " + fileName, e);
			}
		}

		config = new YamlConfiguration();
		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load config file: " + fileName, e);
		}

		this.reload();
	}

	public void reload() {
		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to reload config file: " + configFile.getName(), e);
		}
	}

	public void save() {
		try {
			config.options().copyDefaults(true);
			config.save(configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to save config file: " + configFile.getName(), e);
		}
	}

	public FileConfiguration get() {
		return this.config;
	}
}
