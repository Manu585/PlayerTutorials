package org.bendersdestiny.playertutorials;

import lombok.Getter;
import org.bendersdestiny.playertutorials.commands.TutorialCommand;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.listeners.TutorialListener;
import org.bendersdestiny.playertutorials.manager.ItemManager;
import org.bendersdestiny.playertutorials.manager.StorageManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public final class PlayerTutorials extends JavaPlugin {
	@Getter
	private static PlayerTutorials instance;
	@Getter
	private Storage storage;
	@Getter
	private ItemManager itemManager;
	private ChatUtil chatUtil;

	@Override
	public void onEnable() {
		instance = this;
		chatUtil = new ChatUtil(this);

		this.registerCommands();
		this.registerListeners(
				new TutorialListener()
		);

		new ConfigManager(this);
		storage = new Storage(); // Setup storage after config is initialized!!

		itemManager = new ItemManager();

		this.loadEverythingAsync();

		chatUtil.sendServerStartupMessage();
	}

	@Override
	public void onDisable() {
		// Close database connections cleanly
		if (storage != null) {
			storage.disconnect();
		}
		if (chatUtil != null) {
			chatUtil.sendServerStopMessage();
		}
	}

	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	private void registerCommands() {
		Objects.requireNonNull(getCommand("tutorial")).setExecutor(new TutorialCommand());
		Objects.requireNonNull(getCommand("tutorial")).setTabCompleter(new TutorialCommand());
	}

	private void loadEverythingAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				StorageManager.loadAllTutorialsAsync();
				StorageManager.loadAllAreasAsync();
				StorageManager.loadAllTasksAsync();
				StorageManager.loadAllStructuresAsync();

				// Adds all loaded areas to the coresponding tutorials
				MemoryUtil.finishTutorials();
			}
		}.runTaskAsynchronously(instance);
	}
}
