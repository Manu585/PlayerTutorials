package org.bendersdestiny.playertutorials.methods;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
}
