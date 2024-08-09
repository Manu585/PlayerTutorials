package org.bendersdestiny.playertutorials;

import lombok.Getter;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.manager.StorageManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerTutorials extends JavaPlugin {
	@Getter
	private static PlayerTutorials instance;
	private ChatUtil chatUtil;
	@Getter
	private Storage storage;

	@Override
	public void onEnable() {
		instance = this;
		chatUtil = new ChatUtil(this);

		new ConfigManager(this);

		storage = new Storage(); // ConfigManager has to be initialized first

		this.loadEverythingAsync();

		chatUtil.sendServerStartupMessage();
	}

	@Override
	public void onDisable() {
		if (storage != null) {
			storage.disconnect();
		}
		if (chatUtil != null) {
			chatUtil.sendServerStopMessage();
		}
		this.saveEverythingAsync();
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
