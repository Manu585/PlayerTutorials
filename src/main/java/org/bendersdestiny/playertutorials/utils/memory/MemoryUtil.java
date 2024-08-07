package org.bendersdestiny.playertutorials.utils.memory;

import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryUtil {
    public static Map<Integer, Structure> activeStructures = new ConcurrentHashMap<>();

    public static void fillTutorialMapViaDB(Tutorial tutorial) {

    }

    public static void saveTutorials() {

    }

    public static void saveAreas() {

    }
}
