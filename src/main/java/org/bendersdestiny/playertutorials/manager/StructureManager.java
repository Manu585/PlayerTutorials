package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

@Getter
public class StructureManager {
    private final Tutorial tutorial;

    public StructureManager(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    public void spawnStructure() {
        List<Structure> structure = new java.util.ArrayList<>();
        for (int i = 0; i < this.tutorial.getAreas().size(); i++) {
            structure.add(this.tutorial.getAreas().get(i).getStructure());
        }
        structure.forEach(structure1 -> structure1.spawn(new Location(Bukkit.getWorld("world"), 0, 5, 0))); // Everything wrong with that
    }

    public void destroyStructure() {}
}
