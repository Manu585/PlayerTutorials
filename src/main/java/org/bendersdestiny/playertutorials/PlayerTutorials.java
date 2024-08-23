package org.bendersdestiny.playertutorials;

import lombok.Getter;
import org.bendersdestiny.playertutorials.commands.TutorialCommand;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.listeners.TutorialListener;
import org.bendersdestiny.playertutorials.manager.ItemManager;
import org.bendersdestiny.playertutorials.manager.StorageManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.command.Command;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.logging.Level;

public final class PlayerTutorials extends JavaPlugin {
	@Getter
	private static PlayerTutorials instance;
	@Getter
	private Storage storage;
	private ChatUtil chatUtil;

	/**
	 * Runs once the server starts
	 */
	@Override
	public void onEnable() {
		instance = this;
		chatUtil = new ChatUtil(this);

		new ItemManager();

		this.registerCommands();

		this.registerListeners(
				new TutorialListener()
		);

		new ConfigManager(this);
		storage = new Storage(); // ConfigManager has to be initialized first

		this.loadEverythingAsync();

		chatUtil.sendServerStartupMessage();
	}

	/**
	 * Runs once the server closes
	 */
	@Override
    public void onDisable() {
		this.saveEverythingAsync();

		if (storage != null) {
			storage.disconnect();
		}
		if (chatUtil != null) {
			chatUtil.sendServerStopMessage();
		}
	}

	/**
	 * Registers all {@link Listener} in the {@link java.util.List}
	 *
	 * @param listeners Listeners List
	 */
	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	/**
	 * Utility method to register all {@link org.bukkit.command.Command} with
	 */
	private void registerCommands() {
		Objects.requireNonNull(getCommand("tutorial")).setExecutor(new TutorialCommand());
		Objects.requireNonNull(getCommand("tutorial")).setTabCompleter(new TutorialCommand());
	}

	/**
	 * Saves every {@link org.bendersdestiny.playertutorials.tutorial.Tutorial}, {@link org.bendersdestiny.playertutorials.tutorial.area.Area}
	 * {@link org.bendersdestiny.playertutorials.tutorial.task.Task} and {@link org.bendersdestiny.playertutorials.tutorial.area.structure.Structure}
	 * on an async thread. Uses methods from {@link StorageManager} to save.
	 */
	private void saveEverythingAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Storage.tableCreationComplete.get()) {
					StorageManager.saveAllTutorialsAsync();
					StorageManager.saveAllAreasAsync();
					StorageManager.saveAllTasksAsync();
					StorageManager.saveAllStructuresAsync();
				} else {
					getLogger().log(Level.SEVERE, "Couldn't save any data! Async issue!");
				}
			}
		}.runTaskAsynchronously(instance);
	}

	/**
	 * Loads every {@link org.bendersdestiny.playertutorials.tutorial.Tutorial}, {@link org.bendersdestiny.playertutorials.tutorial.area.Area}
	 * {@link org.bendersdestiny.playertutorials.tutorial.task.Task} and {@link org.bendersdestiny.playertutorials.tutorial.area.structure.Structure}
	 * on an async thread. Uses methods from {@link StorageManager} to load.
	 */
	private void loadEverythingAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Storage.tableCreationComplete.get()) {
					StorageManager.loadAllTutorialsAsync();
					StorageManager.loadAllAreasAsync();
					StorageManager.loadAllTasksAsync();
					StorageManager.loadAllStructuresAsync();
				} else {
					getLogger().log(Level.SEVERE, "Couldn't save any data! Async issue!");
				}
			}
		}.runTaskAsynchronously(instance);
	}
}
