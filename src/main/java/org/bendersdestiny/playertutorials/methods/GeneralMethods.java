package org.bendersdestiny.playertutorials.methods;

import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Utility class for general purposes and some more
 * advanced stuff. Mainly here for clean code
 */
public class GeneralMethods {

	/**
	 * Transform a location into a viable
	 * String for the database to use.
	 *
	 * @param locationToTransform Location to transform
	 * @return Location as String
	 */
	public static @NotNull String locationToString(@NotNull Location locationToTransform) {
		if (locationToTransform.getWorld() != null) {
			String world = locationToTransform.getWorld().getName();
			double x = locationToTransform.getX();
			double y = locationToTransform.getY();
			double z = locationToTransform.getZ();

			return world + "," + x + "," + y + "," + z;
		} else {
			throw new NullPointerException("locationToTransform is null");
		}
	}

	/**
	 * Parses the location string to create
	 * a new {@link Location} Object
	 *
	 * @param stringToTransform String to transform
	 * @return String as Location
	 */
	@Contract("_ -> new")
	public static @NotNull Location stringToLocation(@NotNull String stringToTransform) {
		String[] parts = stringToTransform.split(",");
		if (parts.length != 4) {
			throw new IllegalArgumentException("Invalid location string format");
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
	 * Get all {@link Block} in an area of two {@link Location}.
	 *
	 * @param PointA First point
	 * @param PointB Second point
	 * @param world world from the blocks
	 * @return all blocks in the area
	 */
	public static @NotNull Set<Block> blocksInArea(@NotNull Location PointA, @NotNull Location PointB, @NotNull World world) {
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
	 * Create the ID for the tutorials table
	 * It will check for all existing IDs and
	 * keeps on generating until it has one
	 * which is not in the database.
	 *
	 * @param storage Storage instance
	 * @return The ID for the tutorials table
	 */
	public static int createTutorialsID(Storage storage) {
		if (storage == null) {
			throw new NullPointerException("Storage has not been initiated yet!");
		}

		storage.connect();
		String query = "SELECT id FROM tutorials";
		int id = 1;
		Set<Integer> existingIds = new HashSet<>();

		try (Connection connection = storage.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(query)) {

			// Collect all existing IDs in the tutorials table
			while (resultSet.next()) {
				existingIds.add(resultSet.getInt("id"));
			}

			// Increment id until a non-existing one is found
			while (existingIds.contains(id)) {
				id++;
			}

			return id;

		} catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't create random tutorial ID", e);
		} finally {
			storage.disconnect();
		}
		throw new NullPointerException("Failed to create tutorials ID");
	}


	/**
	 * Join the {@link org.bendersdestiny.playertutorials.tutorial.area.Area} Selection mode so players can
	 * create the area they'd like to store in a
	 * {@link org.bendersdestiny.playertutorials.tutorial.Tutorial}
	 *
	 * @param tutorialPlayer The {@link Player} who should enter the Selection mode
	 */
	public static void joinAreaSelectionMode(TutorialPlayer tutorialPlayer) {
		if (tutorialPlayer != null) {
			Player player = tutorialPlayer.getPlayer();
			if (player != null) {
				// SAVE ORIGINAL ITEMS
				for (int i = 0; i < player.getInventory().getContents().length; i++) {
					tutorialPlayer.getORIGINAL_ITEMS().put(i, player.getInventory().getContents()[i]);
				}
				player.getInventory().clear();
				// player.getInventory().setItem(4, ItemManager.AreaSelector);
				player.sendTitle(
						ChatUtil.format("&7Select the area with the &6Axe"),
						ChatUtil.format("&7Left click: &6Pos1&7, Right click: &6Pos2"),
						10, 60, 10);
			}
		}
	}
}
