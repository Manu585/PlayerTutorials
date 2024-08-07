package org.bendersdestiny.playertutorials.tutorial.area.structure;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
public class Structure {
    private int structureID;
    private File structureSchematic;

    public Structure(int structureID, File structureSchematic) {
        this.structureID = structureID;
        this.structureSchematic = structureSchematic;
    }

    public Structure(File structureSchematic) {
        this.structureSchematic = structureSchematic;
    }
}
