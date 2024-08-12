package org.bendersdestiny.playertutorials.tutorial.area.structure;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.File;

/**
 * A structure is the physical aspect to an
 * area. The structure are all blocks / builds
 * in an area and the area handles the functionalities.
 */
@Setter
@Getter
public class Structure {
    private int structureID;
    private int areaID;
    private File structureSchematic;

    public static String structureColor = "#2863ed";

    /**
     * Create a structure which already existed
     * in the db with its own unique ID.
     *
     * @param structureID Unique structure ID
     * @param areaID The ID of the area the Structure correlates to
     * @param structureSchematic The path to the .schem file
     */
    public Structure(int structureID, int areaID, File structureSchematic) {
        this.structureID = structureID;
        this.areaID = areaID;
        this.structureSchematic = structureSchematic;
    }

    public void spawn(Location center) {

    }

    public void destroy(Location center) {

    }
}
