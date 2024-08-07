package org.bendersdestiny.playertutorials.tutorial.area;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bukkit.Location;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;

@Getter
public class Area extends Structure {
	private final int areaID;

	@Setter
	private File schematicFile;
	@Setter
	private String name;
	@Setter
	private Location spawnPoint;
	@Setter
	private Map<Integer, Task> tasks;
	@Setter
	private int priority;

	/**
	 * An {@link Area} is like the name implies an {@link Area} for a {@link Tutorial}.
	 * Each {@link Area} has an ID, Name, Two {@link Location} to define the area with,
	 * A list of {@link Task} and a priority which defines in which order the {@link Area}
	 * are categorized in.
	 *
	 * @param areaID ID of the Area
	 * @param schematicFile The schematic of the structure
	 * @param name Name of the Area
	 * @param spawnPoint Spawn point of the Area
	 * @param tasks Tasks of the Area
	 * @param priority Priority of the Area
	 */
	public Area(int areaID, File schematicFile, String name, Location spawnPoint, Map<Integer, Task> tasks, int priority) {
		super(schematicFile);
		this.areaID = areaID;
		this.schematicFile = schematicFile;
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
			tasks.put(GeneralMethods.validateID(1, tasks), task);
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
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while removing a task!");
		}
	}
}
