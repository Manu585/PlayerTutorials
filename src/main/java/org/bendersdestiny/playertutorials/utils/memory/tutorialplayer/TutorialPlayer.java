package org.bendersdestiny.playertutorials.utils.memory.tutorialplayer;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TutorialPlayer {
	public static Map<UUID, TutorialPlayer> PLAYERS = new ConcurrentHashMap<>();
	public static final Map<UUID, Map<Integer, ItemStack>> ORIGINAL_ITEMS = new ConcurrentHashMap<>();

	private final UUID uuid;
	private final Player player;

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
	public TutorialPlayer(UUID uuid) {
		this.uuid = uuid;
		this.player = Bukkit.getPlayer(uuid);
	}

	/**
	 * Remove a {@link TutorialPlayer} from
	 * the {@link #PLAYERS} {@link Map}
	 *
	 * @param uuid {@link UUID} of the {@link Player} to remove
	 */
	public static void removePlayer(UUID uuid) {
		if (PLAYERS.containsKey(uuid)) {
			PLAYERS.remove(uuid);
			ORIGINAL_ITEMS.remove(uuid);
		} else {
			throw new NullPointerException("Player " + uuid + " is not registered");
		}
	}

	/**
	 * Gets an existing {@link TutorialPlayer} or creates a
	 * new one.
	 *
	 * @param uuid {@link UUID} of the {@link Player} to get
	 * @return The {@link TutorialPlayer}
	 */
	public static TutorialPlayer getPlayer(UUID uuid) {
		if (PLAYERS.containsKey(uuid)) {
			return PLAYERS.get(uuid);
		} else {
			registerPlayer(new TutorialPlayer(uuid));
		}
		throw new NullPointerException("Player " + uuid + " is not registered");
	}

	/**
	 * Register the player into the {@link #PLAYERS} {@link Map}
	 *
	 * @param player {@link TutorialPlayer} to register
	 */
	public static void registerPlayer(TutorialPlayer player) {
		PLAYERS.put(player.getUuid(), player);
	}

	public void enterAreaSelectionMode() {
		if (this.player == null) return;

		this.areaSelectionMode = true;

		Map<Integer, ItemStack> original = new ConcurrentHashMap<>();
		ItemStack[] contents = player.getInventory().getContents();
		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack item = contents[slot];
			if (item != null) {
				original.put(slot, item);
			}
		}
		ORIGINAL_ITEMS.put(this.uuid, original);

		player.getInventory().clear();
		player.getInventory().setItem(4, PlayerTutorials.getInstance().getItemManager().getAreaSelector().getItem());
		player.getInventory().setHeldItemSlot(4);

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

		player.showTitle(title);
	}

	public void leaveAreaSelectionMode() {
		if (this.player == null) return;

		this.areaSelectionMode = false;

		player.getInventory().clear();

		Map<Integer, ItemStack> original = ORIGINAL_ITEMS.remove(this.uuid);
		if (original != null) {
			for (Map.Entry<Integer, ItemStack> entry : original.entrySet()) {
				this.player.getInventory().setItem(entry.getKey(), entry.getValue());
			}
		}
	}
}
