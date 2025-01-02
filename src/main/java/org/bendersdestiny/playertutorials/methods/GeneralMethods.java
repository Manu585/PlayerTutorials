package org.bendersdestiny.playertutorials.methods;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class GeneralMethods {

	/**
	 * Transform a location into a viable
	 * String for the database to use.
	 *
	 * @param locationToTransform Location to transform
	 * @return Location as String
	 */
	public static @NotNull String locationToString(@NotNull Location locationToTransform) {
		if (locationToTransform.getWorld() == null) {
			throw new NullPointerException("Location world is null!");
		}
		String world = locationToTransform.getWorld().getName();
		double x = locationToTransform.getX();
		double y = locationToTransform.getY();
		double z = locationToTransform.getZ();
		return world + "," + x + "," + y + "," + z;
	}

	/**
	 * Parses the location string to create
	 * a new {@link Location} object
	 *
	 * @param stringToTransform String to transform
	 * @return Location
	 */
	@Contract("_ -> new")
	public static @NotNull Location stringToLocation(@NotNull String stringToTransform) {
		String[] parts = stringToTransform.split(",");
		if (parts.length != 4) {
			throw new IllegalArgumentException("Invalid location string format: " + stringToTransform);
		}

		World w = Bukkit.getWorld(parts[0]);
		if (w == null) {
			throw new NullPointerException("World not found: " + parts[0]);
		}

		double x = Double.parseDouble(parts[1]);
		double y = Double.parseDouble(parts[2]);
		double z = Double.parseDouble(parts[3]);
		return new Location(w, x, y, z);
	}

	/**
	 * Get all blocks in an area of two {@link Location}.
	 *
	 * @param PointA First point
	 * @param PointB Second point
	 * @param world The world of the blocks
	 * @return all blocks in the area
	 */
	public static @NotNull Set<Block> blocksInArea(@NotNull Location PointA,
												   @NotNull Location PointB,
												   @NotNull World world) {
		Set<Block> blocks = new HashSet<>();
		for (int x = PointA.getBlockX(); x <= PointB.getBlockX(); x++) {
			for (int y = PointA.getBlockY(); y <= PointB.getBlockY(); y++) {
				for (int z = PointA.getBlockZ(); z <= PointB.getBlockZ(); z++) {
					blocks.add(world.getBlockAt(new Location(world, x, y, z)));
				}
			}
		}
		return blocks;
	}

	/**
	 * Join the {@link org.bendersdestiny.playertutorials.tutorial.area.Area} Selection mode so players can
	 * create the area they'd like to store in a
	 * {@link org.bendersdestiny.playertutorials.tutorial.Tutorial}
	 *
	 * @param tutorialPlayer The {@link Player} who should enter the Selection mode
	 */
	public static void joinAreaSelectionMode(TutorialPlayer tutorialPlayer) {
		if (tutorialPlayer == null) return;

		Player player = tutorialPlayer.getPlayer();
		if (player == null) return;

		// SAVE ORIGINAL ITEMS
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			tutorialPlayer.getORIGINAL_ITEMS().put(i, player.getInventory().getContents()[i]);
		}
		player.getInventory().clear();
		player.getInventory().setItem(4,
				PlayerTutorials.getInstance().getItemManager().getAreaSelector().item
		);
		player.getInventory().setHeldItemSlot(4);

		final Component mainTitle = Component.textOfChildren(
				Component.text("Select the area with the", TextColor.color(130, 130, 130)),
				Component.space(),
				Component.text("Axe", TextColor.color(240, 196, 53)));

		final Component subTitle = Component.textOfChildren(
				Component.text("Left click:", TextColor.color(130, 130, 130)),
				Component.space(),
				Component.text("Pos1", TextColor.color(240, 196, 53)),
				Component.text(",", TextColor.color(130, 130, 130)),
				Component.space(),
				Component.text("Right click:", TextColor.color(130, 130, 130)),
				Component.space(),
				Component.text("Pos2", TextColor.color(240, 196, 53)));

		final Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));
		final Title title = Title.title(mainTitle, subTitle, times);

		player.sendMessage(title.title());
	}
}
