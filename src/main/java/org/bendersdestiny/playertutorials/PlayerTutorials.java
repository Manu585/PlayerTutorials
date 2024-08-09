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

		saveEverything();
	}

	void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	void saveEverything() {
		StorageManager.saveAllTutorialsAsync();
		StorageManager.saveAllAreasAsync();
		StorageManager.saveAllTasksAsync();
		StorageManager.saveAllStructuresAsync();
	}
}
