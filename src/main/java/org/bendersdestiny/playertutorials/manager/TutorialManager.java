package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.entity.Player;

@Getter
public class TutorialManager { //TODO: FINISH
	private final Tutorial tutorial;
	private final Area area;
	private final Player player;

	/**
	 * The {@link TutorialManager} manages the {@link Tutorial}. The {@link TutorialManager} starts
	 * the {@link Tutorial} and keeps them in a loop, until the tutorial is finished or the player
	 * quits the {@link Tutorial} or quits the {@link org.bukkit.Server}.
	 *
	 * @param tutorial The tutorial to manage
	 */
	public TutorialManager(final Tutorial tutorial, final Area area, final Player player) {
		this.tutorial = tutorial;
		this.area = area;
		this.player = player;
	}

	/**
	 * Start the {@link Tutorial}
	 */
	public void start() {
		if (this.tutorial != null) {
			TutorialPlayer.getPlayer(this.player.getUniqueId());
		}
	}

	/**
	 * Stop the {@link Tutorial}
	 */
	public void stop() {
		if (this.tutorial != null) {
			TutorialPlayer.removePlayer(this.player.getUniqueId());
		}
	}
}
