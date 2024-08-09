package org.bendersdestiny.playertutorials.tutorial.task;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.command.Command;

@Getter
public abstract class Task {
	protected final String taskType;

	private final int taskID;
	private final int areaID;

	@Setter
	private int priority;

	public static String taskColor = "#ed28b2";

	/**
	 * A {@link Task} is like the name implies, a task which has to be executed by a player or the console.
	 * A console Task could be to teleport the {@link TutorialPlayer} to a specific location or another {@link Area}.
	 * Whereas a player task could be to make the player execute a {@link Command} for example.
	 *
	 * @param taskType The name of the Task
	 * @param priority Priority of the task
	 */
	public Task(int taskID, int areaID, String taskType, int priority) {
		this.taskID = taskID;
		this.areaID = areaID;
		this.taskType = taskType;
		this.priority = priority;
	}
}
