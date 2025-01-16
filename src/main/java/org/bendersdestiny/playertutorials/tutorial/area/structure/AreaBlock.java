package org.bendersdestiny.playertutorials.tutorial.area.structure;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class AreaBlock {
	private int relativeX;
	private int relativeY;
	private int relativeZ;
	private Material material;

	public AreaBlock(int rx, int ry, int rz, Material mat) {
		this.relativeX = rx;
		this.relativeY = ry;
		this.relativeZ = rz;
		this.material = mat;
	}
}
