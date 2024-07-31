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

	public TeleportTask(int id, int priority, Player player, Location from, Location to) {
		super(id, player, priority);
		this.from = from;
		this.to = to;
	}

	public void teleportPlayerToNewLocation() {
		if (getPlayer() != null) {
			getPlayer().teleport(to);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while trying to teleport player to new location!\nLocation: " + GeneralMethods.locationToString(to));
		}
	}

	public void resetPlayerToOldLocation(){
		if (getPlayer() != null) {
			getPlayer().teleport(from);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Error while trying to teleport player back to old location!");
		}
	}
}
