package org.bendersdestiny.playertutorials.utils.memory.tutorialplayer;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AdminTutorialPlayer extends TutorialPlayer {
	private boolean areaSelectionMode = false;

	@Setter
	private Location pos1, pos2;
	@Setter
	private int particleTaskId = -1;
	@Setter
	private Tutorial editingTutorial;

	/**
	 * Helper Class for easy data retrival and defining / finding Players who manage tutorials / play a tutorial.
	 *
	 * @param uuid UUID of the Player Managing / Playing the tutorial
	 */
	public AdminTutorialPlayer(UUID uuid) {
		super(uuid);
	}


	public void enterAreaSelectionMode() {
		if (this.getPlayer() == null) return;

		this.areaSelectionMode = true;

		Map<Integer, ItemStack> original = new ConcurrentHashMap<>();
		ItemStack[] contents = this.getPlayer().getInventory().getContents();
		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack item = contents[slot];
			if (item != null) {
				original.put(slot, item);
			}
		}

		ORIGINAL_ITEMS.put(this.getUuid(), original);

		this.getPlayer().getInventory().clear();
		this.getPlayer().getInventory().setItem(4, PlayerTutorials.getInstance().getItemManager().getAreaSelector().getItem());
		this.getPlayer().getInventory().setHeldItemSlot(4);

		String mainTitle = "&#828282Select the &#b43cd2area &#828282with the &#f0c435Axe";
		String subTitle = "&#828282Left click: &#f0c435Pos1&#828282, Right click: &#f0c435Pos2";

		Title.Times times = Title.Times.times(
				Duration.ofMillis(500),
				Duration.ofMillis(3000),
				Duration.ofMillis(500)
		);

		Title title = Title.title(
				ChatUtil.translate(mainTitle),
				ChatUtil.translate(subTitle),
				times
		);

		this.getPlayer().showTitle(title);
	}

	public void leaveAreaSelectionMode() {
		if (this.getPlayer() == null) return;

		this.areaSelectionMode = false;

		this.getPlayer().getInventory().clear();

		Map<Integer, ItemStack> original = ORIGINAL_ITEMS.remove(this.getUuid());

		if (original != null) {
			for (Map.Entry<Integer, ItemStack> entry : original.entrySet()) {
				this.getPlayer().getInventory().setItem(entry.getKey(), entry.getValue());
			}
		}
	}

}
