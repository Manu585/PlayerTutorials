package org.bendersdestiny.playertutorials.tutorial.area.structure;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

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

    private List<StructureBlock> blocks;

    public static final String structureColor = "#2863ed";

    /**
     * Create a structure which already existed
     * in the db with its own unique ID.
     *
     * @param structureID Unique structure ID
     * @param areaID The ID of the area the Structure correlates to
     */
    public Structure(int structureID, int areaID, List<StructureBlock> blocks) {
        this.structureID = structureID;
        this.areaID = areaID;
        this.blocks = blocks;
    }

    // Example: place all the blocks at 'center'
    public void spawn(Location center) {
        for (StructureBlock block : blocks) {
            Location loc = center.clone().add(
                    block.getRelativeX(),
                    block.getRelativeY(),
                    block.getRelativeZ()
            );
            loc.getBlock().setType(block.getMaterial());
        }
    }

    public void destroy(Location center) {
        for (StructureBlock block : blocks) {
            Location loc = center.clone().add(
                    block.getRelativeX(),
                    block.getRelativeY(),
                    block.getRelativeZ()
            );
            loc.getBlock().setType(Material.AIR);
        }
    }
}


