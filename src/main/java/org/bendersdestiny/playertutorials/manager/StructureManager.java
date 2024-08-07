package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;

@Getter
public class StructureManager {

    public StructureManager() {

    }

    public static void registerWhiteWorldSpace() {

    }

    public static void loadStructure(Structure structureToLoad) {
        MemoryUtil.activeStructures.put(structureToLoad.getStructureID(), structureToLoad);
    }

    public static void destroyStructure(Structure structureToDestroy) {
        MemoryUtil.activeStructures.remove(structureToDestroy.getStructureID());
    }
}
