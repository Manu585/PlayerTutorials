package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.entity.Player;

import java.io.File;

@Getter
public class TutorialManager {
	private final Tutorial tutorial;
	private final Player player;

	/**
	 * The {@link TutorialManager} manages the {@link Tutorial}. The {@link TutorialManager} starts
	 * the {@link Tutorial} and keeps them in a loop, until the tutorial is finished or the player
	 * quits the {@link Tutorial} or quits the {@link org.bukkit.Server}.
	 *
	 * @param tutorial The tutorial to manage
	 */
	public TutorialManager(Tutorial tutorial, final Player player) {
		this.tutorial = tutorial;
		this.player = player;
	}

	/**
	 * Start the {@link Tutorial}
	 */
	public void start() {
		if (tutorial != null) {
			StructureManager.loadStructure(new Structure(1, new File("BlaBla/schematic.schem"))); // TODO: Make IDs generate randomly
			TutorialPlayer.getPlayer(player.getUniqueId());
		}
	}

	/**
	 * Stop the {@link Tutorial}
	 */
	public void stop() {
		if (tutorial != null) {
			TutorialPlayer.removePlayer(player.getUniqueId());
		}
	}
}
