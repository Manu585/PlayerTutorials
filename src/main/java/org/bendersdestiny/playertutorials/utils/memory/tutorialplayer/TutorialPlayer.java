package org.bendersdestiny.playertutorials.utils.memory.tutorialplayer;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TutorialPlayer {
	public static Map<UUID, TutorialPlayer> PLAYERS = new ConcurrentHashMap<>();

	private final UUID uuid;
	private final Player player;

	public TutorialPlayer(UUID uuid) {
		this.uuid = uuid;
		this.player = Bukkit.getPlayer(uuid);
	}

	public static void removePlayer(UUID uuid) {
		if (PLAYERS.containsKey(uuid)) {
			PLAYERS.remove(uuid);
		} else {
			throw new NullPointerException("Player " + uuid + " is not registered");
		}
	}

	public static TutorialPlayer getPlayer(UUID uuid) {
		if (PLAYERS.containsKey(uuid)) {
			return PLAYERS.get(uuid);
		} else {
			registerPlayer(new TutorialPlayer(uuid));
		}
		throw new NullPointerException("Player " + uuid + " is not registered");
	}

	public static void registerPlayer(TutorialPlayer player) {
		PLAYERS.put(player.getUuid(), player);
	}
}
