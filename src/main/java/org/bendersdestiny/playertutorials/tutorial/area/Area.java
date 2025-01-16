package org.bendersdestiny.playertutorials.tutorial.area;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.area.structure.AreaBlock;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.List;
import java.util.logging.Level;

@Getter
@Setter
public class Area {
	private int areaID;
	private int tutorialID;
	private String name;
	private Location spawnPoint;
	private int priority;

	private List<Task> tasks;       // Tasks for the area
	private List<AreaBlock> blocks; // Blocks in the area

	public Area(int areaID, int tutorialID, String name, Location spawnPoint,
				@Nullable List<Task> tasks, @Nullable List<AreaBlock> blocks, int priority) {
		this.areaID = areaID;
		this.tutorialID = tutorialID;
		this.name = name;
		this.spawnPoint = spawnPoint;
		this.tasks = tasks;
		this.blocks = blocks;
		this.priority = priority;
	}

	/**
	 * Add a {@link Task} to an {@link Area}
	 *
	 * @param task Task to add
	 */
	public void addTask(Task task) {
		if (this.tasks == null || task == null) {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError while adding a new task to area!")
			);

			return;
		}

		this.tasks.add(task);
		MemoryUtil.getCreatedTasks().put(task.getTaskID(), task);
	}

	/**
	 * Remove a {@link Task} from an {@link Area}
	 *
	 * @param task Task to remove
	 */
	public void removeTask(Task task) {
		if (this.tasks == null || task == null) {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError while removing a task from area!")
			);
			return;
		}

		this.tasks.remove(task);
		MemoryUtil.getCreatedTasks().remove(task.getTaskID());
	}

	/**
	 * Spawns the area in the world.
	 * For instance, place all blocks at a given "startLocation".
	 */
	public void spawnArea(Location startLocation) {
		if (this.blocks == null) return;
		for (AreaBlock block : blocks) {
			Location loc = startLocation.clone().add(
					block.getRelativeX(),
					block.getRelativeY(),
					block.getRelativeZ()
			);
			loc.getBlock().setType(block.getMaterial());
		}
	}

	public void destroyArea(Location startLocation) {
		if (this.blocks == null) return;
		for (AreaBlock block : blocks) {
			Location loc = startLocation.clone().add(
					block.getRelativeX(),
					block.getRelativeY(),
					block.getRelativeZ()
			);
			loc.getBlock().setType(Material.AIR);
		}
	}
}
