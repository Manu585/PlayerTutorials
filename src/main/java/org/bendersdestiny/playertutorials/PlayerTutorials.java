package org.bendersdestiny.playertutorials;

import lombok.Getter;
import org.bendersdestiny.playertutorials.commands.TutorialCommand;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.manager.StorageManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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

		new ConfigManager(this);

		storage = new Storage(); // ConfigManager has to be initialized first

		this.loadEverythingAsync();

		Objects.requireNonNull(getCommand("tutorial")).setExecutor(new TutorialCommand());
		Objects.requireNonNull(getCommand("tutorial")).setTabCompleter(new TutorialCommand());

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
	 * Saves every {@link org.bendersdestiny.playertutorials.tutorial.Tutorial}, {@link org.bendersdestiny.playertutorials.tutorial.area.Area}
	 * {@link org.bendersdestiny.playertutorials.tutorial.task.Task} and {@link org.bendersdestiny.playertutorials.tutorial.area.structure.Structure}
	 * on an async thread. Uses methods from {@link StorageManager} to save.
	 */
	private void saveEverythingAsync() {
		StorageManager.saveAllTutorialsAsync();
		StorageManager.saveAllAreasAsync();
		StorageManager.saveAllTasksAsync();
		StorageManager.saveAllStructuresAsync();
	}

	/**
	 * Loads every {@link org.bendersdestiny.playertutorials.tutorial.Tutorial}, {@link org.bendersdestiny.playertutorials.tutorial.area.Area}
	 * {@link org.bendersdestiny.playertutorials.tutorial.task.Task} and {@link org.bendersdestiny.playertutorials.tutorial.area.structure.Structure}
	 * on an async thread. Uses methods from {@link StorageManager} to load.
	 */
	private void loadEverythingAsync() {
		StorageManager.loadAllTutorialsAsync();
		StorageManager.loadAllAreasAsync();
		StorageManager.loadAllTasksAsync();
		StorageManager.loadAllStructuresAsync();
	}
}
