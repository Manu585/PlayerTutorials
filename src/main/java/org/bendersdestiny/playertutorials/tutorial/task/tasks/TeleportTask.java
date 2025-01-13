package org.bendersdestiny.playertutorials.tutorial.task.tasks;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.logging.Level;

@Getter @Setter
public class TeleportTask extends Task {
	private Location from;
	private Location to;

	public TeleportTask(int taskID, int areaID, int priority, Location from, Location to) {
		super(taskID, areaID, "TeleportTask", priority);
		this.from = from;
		this.to = to;
	}

	/**
	 * Teleports the {@link Player} to
	 * the {@link #to} {@link Location}
	 *
	 * @param player {@link Player} to TP
	 */
	public void teleportPlayerToNewLocation(Player player) {
		if (player != null) {
			player.teleport(this.to);
		} else {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError while trying to teleport player to new location!\nLocation: " + GeneralMethods.locationToString(to)));

		}
	}

	/**
	 * Resets the {@link Player} to
	 * the {@link #from} {@link Location}
	 *
	 * @param player {@link Player} to TP
	 */
	public void resetPlayerToOldLocation(Player player){
		if (player != null) {
			player.teleport(this.from);
		} else {

			PlayerTutorials.getInstance().getLogger().log(
					Level.SEVERE,
					ChatUtil.translateString("&cError while trying to teleport player back to old location!"));

		}
	}
}
