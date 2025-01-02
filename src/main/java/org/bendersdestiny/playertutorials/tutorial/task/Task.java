package org.bendersdestiny.playertutorials.tutorial.task;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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
@Setter
public abstract class Task {
	protected final String taskType;

	private int taskID;
	private int areaID;
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
		switch (this.taskType) {
			case "CommandTask" -> {
				ItemStack cmdBlock = new ItemStack(Material.COMMAND_BLOCK);
				ItemMeta meta = cmdBlock.getItemMeta();
				if (meta == null) throw new NullPointerException("ItemMeta cannot be NULL!");

				meta.displayName(Component.text(ChatUtil.format("&6CommandTask")));

				List<Component> lore = new ArrayList<>();
				lore.add(Component.text(ChatUtil.format("Priority: " + this.getPriority())));
				meta.lore(lore);
				meta.setCustomModelData(this.getTaskID());
				cmdBlock.setItemMeta(meta);

				return cmdBlock;
			}
			case "TeleportTask" -> {
				ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
				ItemMeta pearlMeta = pearl.getItemMeta();
				if (pearlMeta == null) throw new NullPointerException("ItemMeta cannot be NULL!");

				pearlMeta.displayName(Component.text(ChatUtil.format("&dTeleportTask")));

				List<Component> lore = new ArrayList<>();
				lore.add(Component.text(ChatUtil.format("Priority: " + this.getPriority())));
				pearlMeta.lore(lore);
				pearlMeta.setCustomModelData(this.getTaskID());
				pearl.setItemMeta(pearlMeta);

				return pearl;
			}
			default ->
					throw new IllegalArgumentException("Invalid task type: " + this.taskType);
		}
	}
}
