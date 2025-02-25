package org.bendersdestiny.playertutorials.utils.memory.tutorialplayer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TutorialPlayer {
	public static Map<UUID, TutorialPlayer> PLAYERS = new ConcurrentHashMap<>();
	public static final Map<UUID, Map<Integer, ItemStack>> ORIGINAL_ITEMS = new ConcurrentHashMap<>();

	private final UUID uuid;
	private final Player player;

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
}
