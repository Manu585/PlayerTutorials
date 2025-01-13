package org.bendersdestiny.playertutorials.tutorial.area;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.CommandTask;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.TeleportTask;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Level;

@Getter
@Setter
public class Area {
	private int areaID;
	private int tutorialID;
	private Structure structure;
	private String name;
	private Location spawnPoint;
	private List<Task> tasks;
	private int priority;

	public static final String areaColor = "&#82c238";

	public Area(int areaID, int tutorialID, Structure structure, String name, Location spawnPoint, @Nullable List<Task> tasks, int priority) {
		this.areaID = areaID;
		this.tutorialID = tutorialID;
		this.structure = structure;
		this.name = name;
		this.spawnPoint = spawnPoint;
		this.tasks = tasks;
		this.priority = priority;
	}

	/**
	 * An {@link Area} is like the name implies an {@link Area} for a {@link Tutorial}.
	 * Each {@link Area} has an ID, Name, Two {@link Location} to define the area with,
	 * A list of {@link Task} and a priority which defines in which order the {@link Area}
	 * are categorized in.
	 *
	 * @param areaID ID of the Area
	 * @param structure The structure corresponding to the Area
	 * @param name Name of the Area
	 * @param spawnPoint Spawn point of the Area
	 * @param tasks Tasks of the Area
	 * @param priority Priority of the Area
	 */
	public Area(int areaID, Structure structure, String name, Location spawnPoint, @Nullable List<Task> tasks, int priority) {
		this.areaID = areaID;
		this.structure = structure;
		this.name = name;
		this.spawnPoint = spawnPoint;
		this.tasks = tasks;
		this.priority = priority;
	}

	/**
	 * Add a {@link Task} to an {@link Area}
	 *
	 * @param task Task to add
	 */
	public void addTask(Task task) {
		if (this.tasks != null && task != null) {
			this.tasks.add(task);
			MemoryUtil.getCreatedTasks().put(task.getTaskID(), task);
			switch (task.getTaskType()) {
				case "CommandTask" ->
						MemoryUtil.getCreatedCommandTasks().put(task.getTaskID(), (CommandTask) task);
				case "TeleportTask" ->
						MemoryUtil.getCreatedTeleportTasks().put(task.getTaskID(), (TeleportTask) task);
				default -> {}
			}
		} else {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError while adding a new task to area!"));

		}
	}

	/**
	 * Remove a {@link Task} from an {@link Area}
	 *
	 * @param task Task to remove
	 */
	public void removeTask(Task task) {
		if (this.tasks != null && task != null) {
			this.tasks.remove(task);
			MemoryUtil.getCreatedTasks().remove(task.getTaskID());
			switch (task.getTaskType()) {
				case "CommandTask" ->
						MemoryUtil.getCreatedCommandTasks().remove(task.getTaskID());
				case "TeleportTask" ->
						MemoryUtil.getCreatedTeleportTasks().remove(task.getTaskID());
				default -> {}
			}
		} else {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError while removing a task from area!"));

		}
	}
}
