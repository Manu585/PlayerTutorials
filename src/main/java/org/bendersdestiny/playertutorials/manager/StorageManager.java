package org.bendersdestiny.playertutorials.manager;

import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class StorageManager {
    /**
     * Saves all {@link org.bendersdestiny.playertutorials.tutorial.Tutorial} on an async {@link BukkitRunnable} thread
     */
    public static void saveAllTutorialsAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveTutorials();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Saves all {@link org.bendersdestiny.playertutorials.tutorial.area.Area} on an async {@link BukkitRunnable} thread
     */
    public static void saveAllAreasAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveAreas();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Saves all {@link org.bendersdestiny.playertutorials.tutorial.task.Task} on an async {@link BukkitRunnable} thread
     */
    public static void saveAllTasksAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveTasks();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Saves all {@link org.bendersdestiny.playertutorials.tutorial.area.structure.Structure} on an async {@link BukkitRunnable} thread
     */
    public static void saveAllStructuresAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.saveStructures();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Loads all {@link org.bendersdestiny.playertutorials.tutorial.Tutorial} on an async {@link BukkitRunnable} thread
     */
    public static void loadAllTutorialsAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.loadTutorials();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Loads all {@link org.bendersdestiny.playertutorials.tutorial.area.Area} on an async {@link BukkitRunnable} thread
     */
    public static void loadAllAreasAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.loadAreas();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Loads all {@link org.bendersdestiny.playertutorials.tutorial.task.Task} on an async {@link BukkitRunnable} thread
     */
    public static void loadAllTasksAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.loadTasks();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }

    /**
     * Loads all {@link org.bendersdestiny.playertutorials.tutorial.area.structure.Structure} on an async {@link BukkitRunnable} thread
     */
    public static void loadAllStructuresAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                MemoryUtil.loadStructures();
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }
}
