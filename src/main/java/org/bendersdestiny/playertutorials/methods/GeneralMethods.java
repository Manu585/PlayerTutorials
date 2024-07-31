package org.bendersdestiny.playertutorials.methods;

import org.bukkit.Location;

public class GeneralMethods {

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
}
