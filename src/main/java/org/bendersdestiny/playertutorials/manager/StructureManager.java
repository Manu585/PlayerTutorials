package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class StructureManager {
    private final World areaWorld;
    private final double wholeAreaX, wholeAreaY, wholeAreaZ;

    public StructureManager(World world, double wholeAreaX, double wholeAreaY, double wholeAreaZ) {
        this.areaWorld = world;
        this.wholeAreaX = wholeAreaX;
        this.wholeAreaY = wholeAreaY;
        this.wholeAreaZ = wholeAreaZ;
    }

    public void loadStructure(Location where) {

    }
}
