package org.bendersdestiny.playertutorials.tutorial;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bukkit.Location;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Getter
public class Tutorial {
	private final int id;
	@Setter
	private String name;
	@Setter
	private Location spawnPoint;
	@Setter
	private Map<Integer, Area> areas;

	public Tutorial(int id, String name) {
		this.name = name;
		this.id = id;
		areas = new ConcurrentHashMap<>();
	}

	public Tutorial(int id, String name, Location spawnPoint, Map<Integer, Area> areas) {
		this.id = id;
		this.name = name;
		this.spawnPoint = spawnPoint;
		this.areas = areas;
	}

	public void addArea(Area area) {
		if (areas != null) {
			areas.put(area.getId(), area);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while adding a new area!");
		}
	}

	public void removeArea(Area area) {
		if (areas != null) {
			areas.remove(area.getId());
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while removing a area!");
		}
	}
}
