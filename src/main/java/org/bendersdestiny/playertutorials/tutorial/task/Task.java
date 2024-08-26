package org.bendersdestiny.playertutorials.tutorial.task;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Task {
	protected final String taskType;

	private final int taskID;
	private final int areaID;

	@Setter
	private int priority;

	public static final String taskColor = "#ed28b2";

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

	/**
	 * Gets the {@link ItemStack} for each specific {@link Task}
	 *
	 * @return the {@link Task} {@link ItemStack}
	 */
	public ItemStack getTaskItemStack() {
		switch (taskType) {
			case "CommandTask":
				ItemStack commandTaskItem = new ItemStack(Material.COMMAND_BLOCK);
				ItemMeta commantTaskItemMeta = commandTaskItem.getItemMeta();

				if (commantTaskItemMeta == null) throw new NullPointerException("ItemMeta cannot be NULL!");

				commantTaskItemMeta.setDisplayName(ChatUtil.format("&6CommandTask"));
				List<String> commandTaskLore = new ArrayList<>();
				commandTaskLore.add(ChatUtil.format(""));
				commandTaskLore.add(ChatUtil.format("Priority: " + getPriority()));
				commantTaskItemMeta.setLore(commandTaskLore);

				commantTaskItemMeta.setCustomModelData(getTaskID());

				return commandTaskItem;
			case "TeleportTask":
				ItemStack teleportTaskItem = new ItemStack(Material.ENDER_PEARL);
				ItemMeta teleportTaskItemItemMeta = teleportTaskItem.getItemMeta();

				if (teleportTaskItemItemMeta == null) throw new NullPointerException("ItemMeta cannot be NULL!");

				teleportTaskItemItemMeta.setDisplayName(ChatUtil.format("&dTeleportTask"));
				List<String> teleportTaskLore = new ArrayList<>();
				teleportTaskLore.add(ChatUtil.format(""));
				teleportTaskLore.add(ChatUtil.format("Priority: " + getPriority()));
				teleportTaskItemItemMeta.setLore(teleportTaskLore);

				teleportTaskItemItemMeta.setCustomModelData(getTaskID());

				return teleportTaskItem;
			default:
				throw new IllegalArgumentException("Invalid task type: " + taskType);
        }
    }
}
