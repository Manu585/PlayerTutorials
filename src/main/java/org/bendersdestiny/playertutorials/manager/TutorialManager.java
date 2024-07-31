package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TutorialManager {
	@Getter
	private Tutorial tutorial;
	@Getter
	private BukkitRunnable task;

	private final PlayerTutorials plugin;

	@Getter
	public static final Map<Integer, BukkitRunnable> activeTasks = new ConcurrentHashMap<>();

	public TutorialManager(Tutorial tutorial, BukkitRunnable task, PlayerTutorials plugin) {
		this.tutorial = tutorial;
		this.task = task;
		this.plugin = plugin;
	}

	public void start() {
		if (tutorial != null) {
			if (task != null) {
				activeTasks.put(task.getTaskId(), task);
				task.runTaskAsynchronously(plugin);
			}
		}
	}

	public void stop() {
		if (tutorial != null) {
			activeTasks.remove(task.getTaskId());
			task.cancel();
		}
	}
}
