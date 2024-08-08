package org.bendersdestiny.playertutorials.methods;

import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
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
	 * Soul purpose of making duplicate ID entries impossible.
	 *
	 * @param idToValidate The ID to test
	 * @param mapToCompare The Map to compare the ID to
	 * @return The valid ID for the database
	 */
	@Contract(pure = true)
	public static Integer validateID(int idToValidate, Map<Integer, Task> mapToCompare) {
		while (mapToCompare.containsKey(idToValidate)) {
			idToValidate++;
		}
		return idToValidate;
	}

	public static int generateID(String type) {
        return switch (type) {
            case "Tutorial" -> createID(MemoryUtil.createdTutorials);
            case "Area" -> createID(MemoryUtil.createdAreas);
            case "Structure" -> createID(MemoryUtil.createdStructures);
			case "Task" -> createID(MemoryUtil.createdTasks);
            default -> -1;
        };
    }

	public static int createID(Map mapToCompare) {
		int generatedID = 1;
		if (mapToCompare != null) {
			while (mapToCompare.containsKey(generatedID)) {
				generatedID++;
			}
			return generatedID;
		} else {
			return -1;
		}
	}
}
