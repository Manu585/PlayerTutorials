package org.bendersdestiny.playertutorials.tutorial.structure;

import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;

import java.io.File;

@Setter
@Getter
public class Structure {
    private int structureID;
    private File structureSchematic;

    public Structure(int structureID, File structureSchematic) {
        this.structureID = structureID;
        this.structureSchematic = structureSchematic;

        MemoryUtil.activeStructures.put(structureID, this);
    }
}
