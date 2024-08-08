package org.bendersdestiny.playertutorials.utils.memory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.CommandTask;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.TeleportTask;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.Material;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Utility class for all {@link Tutorial} related Memory and more.
 * Helps with loading and saving all {@link Tutorial}, {@link Area},
 * {@link Task} and {@link Structure}!
 */
@SuppressWarnings("all") // TODO: Remove suppression. Only here because no DB schematic at work.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryUtil {
    private static final Storage storage = PlayerTutorials.getInstance().getStorage();

    @Getter
    public static Map<Integer, Tutorial> createdTutorials = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, Area> createdAreas = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, Structure> createdStructures = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, Task> createdTasks = new ConcurrentHashMap<>();

    /**
     * Saves all {@link Tutorial} to the Database
     */
    public static void saveTutorials() {
        String query = "INSERT INTO tutorials VALUES(?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Tutorial tutorial : createdTutorials.values()) {
                preparedStatement.setInt(1, tutorial.getId());
                preparedStatement.setString(2, tutorial.getName());
                preparedStatement.setString(3, tutorial.getIcon().toString());
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully saved all Tutorials");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save tutorials", e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Saves all {@link Area} to the Database
     */
    public static void saveAreas() {
        String query = "INSERT INTO areas VALUES(?,?,?,?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Area area : createdAreas.values()) {
                preparedStatement.setInt(1, area.getAreaID());
                preparedStatement.setInt(2, area.getTutorialID());
                preparedStatement.setInt(3, area.getStructure().getStructureID());
                preparedStatement.setString(4, area.getName());
                preparedStatement.setString(5, GeneralMethods.locationToString(area.getSpawnPoint()));
                preparedStatement.setInt(6, area.getPriority());
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully saved all Areas");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save areas", e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Saves all {@link Task} to the Database
     */
    public static void saveTasks() {
        String query = "INSERT INTO tasks VALUES(?,?,?,?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Task task : createdTasks.values()) {
                preparedStatement.setInt(1, task.getTaskID());
                preparedStatement.setInt(2, task.getAreaID());
                preparedStatement.setString(3, task.getTaskType());
                preparedStatement.setInt(4, task.getPriority());
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully saved all Tasks");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save tasks", e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Saves all {@link Structure} to the Database
     */
    public static void saveStructures() {
        String query = "INSERT INTO structures VALUES(?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Structure structure : createdStructures.values()) {
                preparedStatement.setInt(1, structure.getStructureID());
                preparedStatement.setInt(2, structure.getAreaID());
                preparedStatement.setString(3, structure.getStructureSchematic().getPath());
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully saved all Structures");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save structure", e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Loads all {@link Tutorial} from the Database into
     * the {@link #createdTutorials createdTutorials} map
     */
    public static void loadTutorials() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Loading &6Tutorials..."));
        storage.connect();
        String query = "SELECT * FROM tutorials";
        try (Connection connection = storage.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int tutorialID = resultSet.getInt("id");
                String tutorialName = resultSet.getString("name");
                String tutorialIcon = resultSet.getString("icon");
                createdTutorials.put(tutorialID, new Tutorial(
                        tutorialID,
                        tutorialName,
                        Material.valueOf(tutorialIcon.toUpperCase())));
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE, ChatUtil.format("&7Successfully loaded all &6Tutorials&7! \n" +
                    "&6Tutorials &7loaded in &a" + ((System.currentTimeMillis() - loadingStartTime) / 1000) + "&7seconds!"));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load all tutorials! " + e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Loads all {@link Structure} from the Database into
     * the {@link #createdStructures createdStructures} map
     */
    public static void loadStructures() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Loading &5Structures..."));
        storage.connect();
        String query = "SELECT * FROM structures";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int structureID = resultSet.getInt("structureID");
                int areaID = resultSet.getInt("areaID");
                String schematic = resultSet.getString("schematic");
                createdStructures.put(structureID, new Structure(
                        structureID,
                        areaID,
                        new File(schematic)));
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE, ChatUtil.format("&7Successfully loaded all &5Structures&7! \n" +
                    "&5Structures &7loaded in &a" + ((System.currentTimeMillis() - loadingStartTime) / 1000) + "&7seconds!"));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load all structures! " + e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Loads all {@link Task} from the Database into
     * the {@link #createdTasks createdTasks} map
     */
    public static void loadTasks() { // TODO: Not so correctly made yet. Gotta use joins and redo some db structure
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Loading &dTasks..."));
        storage.connect();
        String query = "SELECT * FROM tasks";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int taskID = resultSet.getInt("taskID");
                int areaID = resultSet.getInt("areaID");
                String taskType = resultSet.getString("type");
                switch (taskType) {
                    case "CommandTask":
                        String commandTaskQuery = "SELECT * FROM command_tasks";
                        PreparedStatement commandPreparedStatement = connection.prepareStatement(commandTaskQuery);
                        ResultSet commandResult = commandPreparedStatement.executeQuery();
                        int commandPriority = commandResult.getInt("priority");
                        String requiredCommand = commandResult.getString("requiredCommand");
                        createdTasks.put(taskID, new CommandTask(
                                taskID,
                                areaID,
                                commandPriority,
                                requiredCommand));
                    case "TeleportTask":
                        String teleportTaskQuery = "SELECT * FROM teleport_tasks";
                        PreparedStatement teleportPreparedStatement = connection.prepareStatement(teleportTaskQuery);
                        ResultSet teleportResult = teleportPreparedStatement.executeQuery();
                        int teleportPriority = teleportResult.getInt("priority");
                        String fromTeleport = teleportResult.getString("from");
                        String toTeleport = teleportResult.getString("to");
                        createdTasks.put(taskID, new TeleportTask(
                                taskID,
                                areaID,
                                teleportPriority,
                                GeneralMethods.stringToLocation(fromTeleport),
                                GeneralMethods.stringToLocation(toTeleport)));
                }
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE, ChatUtil.format("&7Successfully loaded all &dTasks&7! \n" +
                    "&dStructures &7loaded in &a" + ((System.currentTimeMillis() - loadingStartTime) / 1000) + "&7seconds!"));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load tasks! " + e.getMessage());
        } finally {
            storage.disconnect();
        }
    }

    /**
     * Loads all {@link Area} from the Database into
     * the {@link #createdAreas createdAreas} map
     */
    public static void loadAreas() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Loading &8Areas..."));
        storage.connect();
        String query = "SELECT * FROM areas";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int areaID       = resultSet.getInt("areaID");
                int tutorialID   = resultSet.getInt("tutorialID");
                int structureID  = resultSet.getInt("structureID");
                String areaName  = resultSet.getString("name");
                String areaSpawn = resultSet.getString("spawnpoint");
                String taskIDs   = resultSet.getString("tasks");
                int priority     = resultSet.getInt("priority");

                Task task = null;
                for (String key : taskIDs.split(",")) // Check if there are tasks in the DB
                    task = createdTasks.get(Integer.parseInt(key));

                if (task == null) { // If there aren't any tasks in the DB
                    Area noTasksArea = new Area(
                            areaID,
                            tutorialID,
                            createdStructures.get(structureID),
                            areaName,
                            GeneralMethods.stringToLocation(areaSpawn),
                            null,
                            priority);

                    createdAreas.put(areaID, noTasksArea);
                } else {
                    Map<Integer, Task> tasks = new ConcurrentHashMap<>();
                    tasks.put(task.getPriority(), task);

                    Area area = new Area(
                            areaID,
                            tutorialID,
                            createdStructures.get(structureID),
                            areaName,
                            GeneralMethods.stringToLocation(areaSpawn),
                            tasks,
                            priority);

                    createdAreas.put(areaID, area);
                }
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE, ChatUtil.format("&7Successfully loaded all &8Areas&7! \n" +
                    "&8Areas &7loaded in &a" + ((System.currentTimeMillis() - loadingStartTime) / 1000) + "&7seconds!"));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load all areas! " + e.getMessage());
        } finally {
            storage.disconnect();
        }
    }
}
