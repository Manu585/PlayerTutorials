package org.bendersdestiny.playertutorials.manager;

import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class StorageManager {
    public static void saveAllTutorialsAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveTutorials();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    public static void saveAllAreasAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveAreas();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    public static void saveAllTasksAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveTasks();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    public static void saveAllStructuresAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveStructures();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }
}
