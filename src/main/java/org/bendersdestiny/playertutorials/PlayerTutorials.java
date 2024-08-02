package org.bendersdestiny.playertutorials;

import lombok.Getter;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.listeners.AreaListener;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
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

		getServer().getPluginManager().registerEvents(new AreaListener(), this);
	}

	@Override
	public void onDisable() {
		if (storage != null) {
			storage.disconnect();
		}
		if (chatUtil != null) {
			chatUtil.sendServerStopMessage();
		}
	}
}
