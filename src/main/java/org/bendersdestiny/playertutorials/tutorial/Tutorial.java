package org.bendersdestiny.playertutorials.tutorial;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.area.Area;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

@Getter
public class Tutorial {
	private final int id;
	@Setter
	private String name;
	@Setter
	private Map<Integer, Area> areas;

	/**
	 * Freshly created {@link Tutorial} constructor
	 *
	 * @param id Id of the tutorial
	 * @param name Name of the tutorial
	 */
	public Tutorial(int id, String name) {
		this.id = id;
		this.name = name;
		this.areas = new ConcurrentHashMap<>();
	}

	/**
	 * Existing {@link Tutorial} coming from the database constructor
	 *
	 * @param id Id of the tutorial
	 * @param name Name of the tutorial
	 * @param areas Areas of the tutorial
	 */
	public Tutorial(int id, String name, Map<Integer, Area> areas) {
		this.id = id;
		this.name = name;
		this.areas = areas;
	}

	/**
	 * Add an {@link Area} to a {@link Tutorial}
	 *
	 * @param area Area to add
	 */
	public void addArea(Area area) {
		if (this.areas != null && area != null) {
			this.areas.put(area.getId(), area);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while adding a new area!");
		}
	}

	/**
	 * Remove an {@link Area} from a {@link Tutorial}
	 *
	 * @param area Area to remove
	 */
	public void removeArea(Area area) {
		if (this.areas != null && area != null) {
			this.areas.remove(area.getId());
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"Error while removing a area!");
		}
	}
}
