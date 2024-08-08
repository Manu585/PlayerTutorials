package org.bendersdestiny.playertutorials.tutorial.task.tasks;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
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

	public void teleportPlayerToNewLocation(Player player) {
		if (player != null) {
			player.teleport(to);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while trying to teleport player to new location!\nLocation: " + GeneralMethods.locationToString(to));
		}
	}

	public void resetPlayerToOldLocation(Player player){
		if (player != null) {
			player.teleport(from);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Error while trying to teleport player back to old location!");
		}
	}
}
