package org.bendersdestiny.playertutorials.methods;

import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

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
			return "ERROR";
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
	public static int createTutorialsID(Storage storage) { //TODO: Not finished yet, just a WIP version
		storage.connect();
		String query = "SELECT id FROM tutorials";
		try (Connection connection = storage.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(query);
			int tutorialsID = statement.getResultSet().getInt(1);
			int id = 1;
			while (tutorialsID == id) {
				id++;
			}
			return id;
        } catch (SQLException e) {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't create random tutorial ID");
		} finally {
			storage.disconnect();
		}
		throw new NullPointerException("Failed to create tutorials ID");
	}
}
