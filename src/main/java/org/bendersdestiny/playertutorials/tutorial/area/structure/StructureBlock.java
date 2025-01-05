package org.bendersdestiny.playertutorials.tutorial.area.structure;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class StructureBlock {
	private int relativeX;
	private int relativeY;
	private int relativeZ;
	private Material material;

	public StructureBlock(int relativeX, int relativeY, int relativeZ, Material material) {
		this.relativeX = relativeX;
		this.relativeY = relativeY;
		this.relativeZ = relativeZ;
		this.material = material;
	}
}
