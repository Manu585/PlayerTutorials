package org.bendersdestiny.playertutorials.tutorial.task;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

@Getter
public abstract class Task {
	private final int id;
	private final Player player;

	@Setter
	private int priority;

	/**
	 * A {@link Task} is like the name implies, a task which has to be executed by a player or the console.
	 * A console Task could be to teleport the {@link TutorialPlayer} to a specific location or another {@link Area}.
	 * Whereas a player task could be to make the player execute a {@link Command} for example.
	 *
	 * @param id ID of the task
	 * @param player Player related to the task
	 * @param priority Priority of the task
	 */
	public Task(int id, Player player, int priority) {
		this.id = id;
		this.player = player;
		this.priority = priority;
	}
}
