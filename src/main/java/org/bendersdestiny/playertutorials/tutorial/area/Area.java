package org.bendersdestiny.playertutorials.tutorial.area;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
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


	public Area(int id, String name, Location pointOne, Location pointTwo, Location spawnPoint, List<Task> tasks) {
		this.id = id;
		this.name = name;
		this.pointOne = pointOne;
		this.pointTwo = pointTwo;
		this.spawnPoint = spawnPoint;
		this.tasks = tasks;
	}

	public void addTask(Task task) {
		if (tasks != null && !tasks.contains(task)) {
			tasks.add(task);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while adding a new task!");
		}
	}

	public void removeTask(Task task) {
		if (tasks != null && tasks.contains(task)) {
			tasks.remove(task);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while removing a task!");
		}
	}

}
