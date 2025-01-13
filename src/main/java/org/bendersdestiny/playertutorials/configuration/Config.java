package org.bendersdestiny.playertutorials.configuration;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
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

	/**
	 * Creates a new Config file
	 *
	 * @param plugin Plugin instance
	 * @param fileName Name of the file
	 */
	public Config(PlayerTutorials plugin, String fileName) {
		this.plugin = plugin;

		this.configFile = new File(plugin.getDataFolder(), fileName);

		if (!this.configFile.exists()) {
			try {
				if (this.configFile.getParentFile().mkdirs()) {

					plugin.getLogger().log(
							Level.INFO,
							ChatUtil.translateString("&aSuccessfully created necessary directories!")
					);

				}
				plugin.saveResource(fileName, false);

				plugin.getLogger().log(
						Level.INFO,
						ChatUtil.translateString("&aSuccessfully created config file: " + fileName)
				);

			} catch (Exception e) {

				plugin.getLogger().log(
						Level.SEVERE,
						ChatUtil.translateString("&cFailed to create config file: " + fileName),
						e
				);

			}
		}

		this.config = new YamlConfiguration();

		try {
			this.config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {

			plugin.getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cFailed to load config file: " + fileName),
					e
			);
		}
		this.reload();
	}

	/**
	 * Reloads the {@link Config}
	 */
	public void reload() {
		try {
			this.config.load(this.configFile);
		} catch (IOException | InvalidConfigurationException e) {

			this.plugin.getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cFailed to reload config file: " + this.configFile.getName()),
					e
			);

		}
	}

	/**
	 * Saves the {@link Config}
	 */
	public void save() {
		try {
			this.config.options().copyDefaults(true);
			this.config.save(this.configFile);
		} catch (IOException e) {

			this.plugin.getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cFailed to save config file: " + this.configFile.getName()),
					e
			);

		}
	}

	/**
	 * Get the {@link FileConfiguration}
	 *
	 * @return the {@link FileConfiguration}
	 */
	public FileConfiguration get() {
		return this.config;
	}
}
