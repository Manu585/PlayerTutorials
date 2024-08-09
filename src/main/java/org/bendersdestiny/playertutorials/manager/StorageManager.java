package org.bendersdestiny.playertutorials.manager;

import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class StorageManager {
    /**
     * Saves all {@link org.bendersdestiny.playertutorials.tutorial.Tutorial} on an async {@link BukkitRunnable} thread
     */
    public static void saveAllTutorialsAsync() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    MemoryUtil.saveTutorials();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't save Tutorials!"));
                }
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
                try {
                    MemoryUtil.saveAreas();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't save Areas!"));
                }
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
                try {
                    MemoryUtil.saveTasks();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't save Tasks!"));
                }
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
                try {
                    MemoryUtil.saveStructures();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't save Structures!"));
                }
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
                try {
                    MemoryUtil.loadTutorials();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't load Tutorials!"));
                }
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
                try {
                    MemoryUtil.loadAreas();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't load Areas!"));
                }
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
                try {
                    MemoryUtil.loadTasks();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't load Tasks!"));
                }
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
                try {
                    MemoryUtil.loadStructures();
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("&4Couldn't load Structures!"));
                }
            }
        }.runTaskAsynchronously(PlayerTutorials.getInstance());
    }
}
