package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TutorialManager {
	@Getter
	private final Tutorial tutorial;
	@Getter
	private final BukkitRunnable task;

	private final PlayerTutorials plugin;

	@Getter
	public static final Map<Integer, BukkitRunnable> activeTasks = new ConcurrentHashMap<>();

	/**
	 * The {@link TutorialManager} manages the {@link Tutorial}. The {@link TutorialManager} starts
	 * the {@link Tutorial} and keeps them in a loop, until the tutorial is finished or the player
	 * quits the {@link Tutorial} or quits the {@link org.bukkit.Server}.
	 *
	 * @param tutorial The tutorial to manage
	 * @param task The runnable
	 * @param plugin Plugin
	 */
	public TutorialManager(Tutorial tutorial, BukkitRunnable task, final PlayerTutorials plugin) {
		this.tutorial = tutorial;
		this.task = task;
		this.plugin = plugin;
	}

	/**
	 * Start the {@link Tutorial}
	 */
	public void start() {
		if (tutorial != null) {
			if (task != null) {
				activeTasks.put(task.getTaskId(), task);
				task.runTaskAsynchronously(plugin);
			}
		}
	}

	/**
	 * Stop the {@link Tutorial}
	 */
	public void stop() {
		if (tutorial != null) {
			activeTasks.remove(task.getTaskId());
			task.cancel();
		}
	}
}
