package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bukkit.World;

@Getter
public class StructureManager {
    private final String schematicFile;
    private final World areaWorld;
    private final double wholeAreaX, wholeAreaY, wholeAreaZ;

    public StructureManager(String schematicFile, World world, double wholeAreaX, double wholeAreaY, double wholeAreaZ) {
        this.schematicFile = schematicFile;
        this.areaWorld = world;
        this.wholeAreaX = wholeAreaX;
        this.wholeAreaY = wholeAreaY;
        this.wholeAreaZ = wholeAreaZ;
    }

}