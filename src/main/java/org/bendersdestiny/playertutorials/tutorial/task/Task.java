package org.bendersdestiny.playertutorials.tutorial.task;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public abstract class Task {
	private final int id;
	private final Player player;
	@Setter
	private int priority;

	public Task(int id, Player player, int priority) {
		this.id = id;
		this.player = player;
		this.priority = priority;
	}
}
