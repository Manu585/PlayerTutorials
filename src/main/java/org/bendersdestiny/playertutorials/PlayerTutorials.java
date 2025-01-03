package org.bendersdestiny.playertutorials;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages;
import lombok.Getter;
import org.bendersdestiny.playertutorials.commands.TutorialCommand;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.listeners.TutorialListener;
import org.bendersdestiny.playertutorials.manager.ItemManager;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerTutorials extends JavaPlugin {
	@Getter
	private static PlayerTutorials instance;
	@Getter
	private Storage storage;
	@Getter
	private ItemManager itemManager;
	private ChatUtil chatUtil;

	@Getter
	private LiteCommands<CommandSender> liteCommands;

	@Override
	public void onEnable() {
		instance = this;
		this.chatUtil = new ChatUtil(this);


		// --------------------------------------------Command Builder--------------------------------------------
		this.liteCommands = LiteBukkitFactory.builder("playertutorials", this)
				.settings(settings -> settings.nativePermissions(false))

				.commands(
						new TutorialCommand()
				)

				.message(LiteBukkitMessages.PLAYER_ONLY, "&cOnly a player can execute this command!")
				.build();
		// -------------------------------------------------------------------------------------------------------


		// Listeners
		this.registerListeners(
				new TutorialListener()
		);

		new ConfigManager(this);
		this.storage = new Storage(); // Setup storage after config is initialized for Databasetype retrival!
		this.itemManager = new ItemManager();

		this.loadEverythingAsync();

		this.chatUtil.sendServerStartupMessage();
	}

	@Override
	public void onDisable() {
		MemoryUtil.saveTutorials();
		MemoryUtil.saveAreas();
		MemoryUtil.saveTasks();
		MemoryUtil.saveStructures();

		if (this.storage != null) {
			this.storage.disconnect();
		}
		if (this.chatUtil != null) {
			this.chatUtil.sendServerStopMessage();
		}
		if (this.liteCommands != null) {
			this.liteCommands.unregister();
		}
	}

	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	private void loadEverythingAsync() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					MemoryUtil.loadTutorials();
					MemoryUtil.loadAreas();
					MemoryUtil.loadTasks();
					MemoryUtil.loadStructures();

					// Attach areas to tutorials in memory
					MemoryUtil.addAreasToTutorials();

				} catch (Exception e) {
					PlayerTutorials.getInstance().getLogger().severe("Fatal error loading everything: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(this);
	}

}
