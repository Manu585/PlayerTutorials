package org.bendersdestiny.playertutorials.tutorial.area;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.CommandTask;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.TeleportTask;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Getter
public class Area {
	private final int areaID;
	private int tutorialID;

	@Setter
	private Structure structure;
	@Setter
	private String name;
	@Setter
	private Location spawnPoint;
	@Setter
	private List<Task> tasks;
	@Setter
	private int priority;

	public Area(int areaID, int tutorialID, Structure structure, String name, Location spawnPoint, List<Task> tasks, int priority) {
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
		if (tasks != null) {
			tasks.add(task);
			MemoryUtil.createdTasks.put(task.getTaskID(), task);
			switch (task.getTaskType()) {
				case "CommandTask":
					MemoryUtil.createdCommandTasks.put(task.getTaskID(), (CommandTask) task);
					break;
				case "TeleportTask":
					MemoryUtil.createdTeleportTasks.put(task.getTaskID(), (TeleportTask) task);
					break;
			}
			MemoryUtil.createdTasks.put(task.getTaskID(), task);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while adding a new task!");
		}
	}

	/**
	 * Remove a {@link Task} from an {@link Area}
	 *
	 * @param task Task to remove
	 */
	public void removeTask(Task task) {
		if (tasks != null) {
			tasks.remove(task.getPriority());
			MemoryUtil.createdTasks.remove(task.getTaskID());
			switch (task.getTaskType()) {
				case "CommandTask":
					MemoryUtil.createdCommandTasks.remove(task.getTaskID());
					break;
				case "TeleportTask":
					MemoryUtil.createdTeleportTasks.remove(task.getTaskID());
					break;
			}
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while removing a task!");
		}
	}

	public void linkAreaToTutorial(int tutorialID) {

	}
}
