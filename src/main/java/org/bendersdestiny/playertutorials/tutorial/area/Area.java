package org.bendersdestiny.playertutorials.tutorial.area;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bukkit.Location;

import java.util.List;
import java.util.logging.Level;

@Getter
public class Area {
	private final int id;
	@Setter
	private String name;
	@Setter
	private Location spawnPoint;
	@Setter
	private Location pointOne;
	@Setter
	private Location pointTwo;
	@Setter
	private List<Task> tasks;
	@Setter
	private int priority;

	/**
	 * An {@link Area} is like the name implies an {@link Area} for a {@link Tutorial}.
	 * Each {@link Area} has an ID, Name, Two {@link Location} to define the area with,
	 * A list of {@link Task} and a priority which defines in which order the {@link Area}
	 * are categorized in.
	 *
	 * @param id ID of the Area
	 * @param name Name of the Area
	 * @param pointOne First Point of the Area
	 * @param pointTwo Second Point of the Area
	 * @param spawnPoint Spawn point of the Area
	 * @param tasks Tasks of the Area
	 * @param priority Priority of the Area
	 */
	public Area(int id, String name, Location pointOne, Location pointTwo, Location spawnPoint, List<Task> tasks, int priority) {
		this.id = id;
		this.name = name;
		this.pointOne = pointOne;
		this.pointTwo = pointTwo;
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
		if (tasks != null && !tasks.contains(task)) {
			tasks.add(task);
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
		if (tasks != null && tasks.contains(task)) {
			tasks.remove(task);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while removing a task!");
		}
	}
}
