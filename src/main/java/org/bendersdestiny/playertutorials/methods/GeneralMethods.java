package org.bendersdestiny.playertutorials.methods;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
	public static String locationToString(Location locationToTransform) {
		if (locationToTransform != null) {
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
	public static Location stringToLocation(String stringToTransform) {
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


	public static Set<Block> blocksInArea(Location PointA, Location PointB, Player player) {
		Set<Block> blocks = new HashSet<>();
		for (int x = PointA.getBlockX(); x <= PointB.getBlockX(); x++) {
			for (int y = PointA.getBlockY(); y <= PointB.getBlockY(); y++) {
				for (int z = PointA.getBlockZ(); z <= PointB.getBlockZ(); z++) {
					blocks.add(player.getWorld().getBlockAt(new Location(player.getWorld(), x, y, z)));
				}
			}
		}
		return blocks;
	}
}
