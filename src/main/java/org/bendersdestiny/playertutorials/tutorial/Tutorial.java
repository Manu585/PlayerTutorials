package org.bendersdestiny.playertutorials.tutorial;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Getter
public class Tutorial {
	private final int id;
	@Setter
	private String name;
	@Setter
	private Material icon;
	@Setter
	private List<Area> areas;

	public static final String tutorialColor = "&#F2B152";

	/**
	 * Freshly created {@link Tutorial} constructor
	 *
	 * @param id ID of the tutorial
	 * @param name Name of the tutorial
	 * @param icon The icon display in the GUI
	 */
	public Tutorial(int id, String name, @Nullable Material icon) {
		this.id = id;
		this.name = name;
		if (icon == null) {
			this.icon = Material.DIORITE;
		} else {
			this.icon = icon;
		}
		this.areas = new ArrayList<>();
	}

	/**
	 * Existing {@link Tutorial} coming from the database constructor
	 *
	 * @param id ID of the tutorial
	 * @param name Name of the tutorial
	 * @param areas Areas of the tutorial
	 */
	public Tutorial(int id, String name, Material icon, List<Area> areas) {
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.areas = areas;
	}

	/**
	 * Add an {@link Area} to a {@link Tutorial}
	 *
	 * @param area {@link Area} to add
	 */
	public void addArea(Area area) {
		if (this.areas != null && area != null) {
			this.areas.add(area);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"An Error occurred while adding a new area!");
		}
	}

	/**
	 * Remove an {@link Area} from a {@link Tutorial}
	 *
	 * @param area {@link Area} to remove
	 */
	public void removeArea(Area area) {
		if (this.areas != null && area != null) {
			this.areas.remove(area);
		} else {
			PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,"An Error occurred while removing an area!");
		}
	}
}
