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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * Utility class for all {@link Tutorial} related Memory and more.
 * Helps with loading and saving all {@link Tutorial}, {@link Area},
 * {@link Task} and {@link Structure}!
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryUtil {
    public static AtomicBoolean memorySetup = new AtomicBoolean(false);

    private static final Storage storage = PlayerTutorials.getInstance().getStorage();

    @Getter
    public static Map<Integer, Tutorial> createdTutorials = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, Area> createdAreas = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, Structure> createdStructures = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, Task> createdTasks = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, TeleportTask> createdTeleportTasks = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, CommandTask> createdCommandTasks = new ConcurrentHashMap<>();
    @Getter
    public static Map<Integer, String> guiCache = new ConcurrentHashMap<>();

    /**
     * Saves all {@link Tutorial} to the Database
     */
    public static void saveTutorials() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Saving " + Tutorial.tutorialColor + "Tutorials &7..."));
        String query = "INSERT INTO tutorials VALUES(?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Tutorial tutorial : createdTutorials.values()) {
                preparedStatement.setInt(1, tutorial.getId());
                preparedStatement.setString(2, tutorial.getName());
                preparedStatement.setString(3, tutorial.getIcon().toString());
                preparedStatement.executeUpdate();
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Successfully saved all "  + Tutorial.tutorialColor + "&6Tutorials &7in &a" +
                    ((System.currentTimeMillis() - startTime) / 1000) + " &7seconds"));
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("Couldn't save " + Tutorial.tutorialColor + "tutorials"), e.getMessage());
        }
    }

    public static void saveTutorial(Tutorial tutorial) {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Saving " + Tutorial.tutorialColor + "Tutorial &7..."));
        String query = "INSERT INTO tutorials VALUES(?,?,?)";
        storage.connect();
        try (Connection connection = storage.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, tutorial.getId());
            preparedStatement.setString(2, tutorial.getName());
            preparedStatement.setString(3, tutorial.getIcon().toString());
            preparedStatement.executeUpdate();

            PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Successfully saved tutorial in database! Took " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds!"));
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, ChatUtil.format("Couldn't save " + Tutorial.tutorialColor + "tutorial"), e.getMessage());
        }
    }

    /**
     * Saves all {@link Area} to the Database
     */
    public static void saveAreas() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Saving " + Area.areaColor + "Areas &7..."));
        String query = "INSERT INTO areas VALUES(?,?,?,?,?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Area area : createdAreas.values()) {
                preparedStatement.setInt(1, area.getAreaID());
                preparedStatement.setInt(2, area.getTutorialID());
                preparedStatement.setInt(3, area.getStructure().getStructureID());
                preparedStatement.setString(4, area.getName());
                preparedStatement.setString(5, GeneralMethods.locationToString(area.getSpawnPoint()));
                preparedStatement.setInt(6, area.getTasks().listIterator().next().getTaskID());
                preparedStatement.setInt(7, area.getPriority());
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully saved all " + Area.areaColor + "Areas &7in &a" +
                    ((System.currentTimeMillis() - startTime) / 1000) + " &7seconds");
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save areas", e.getMessage());
        }
    }

    /**
     * Main task saving method with the sub-task saving methods
     * like {@link #saveGeneralTasks()}, {@link #saveCommandTasks()}
     * or {@link #saveTeleportTasks()}
     */
    public static void saveTasks() {
        saveGeneralTasks();
        saveCommandTasks();
        saveTeleportTasks();
    }

    /**
     * Saves all {@link Task} to the Database
     */
    private static void saveGeneralTasks() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Saving " + Task.taskColor + "Tasks &7..."));
        String query = "INSERT INTO tasks VALUES(?,?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Task task : createdTasks.values()) {
                preparedStatement.setInt(1, task.getTaskID());
                preparedStatement.setInt(2, task.getAreaID());
                preparedStatement.setString(3, task.getTaskType());
                preparedStatement.setInt(4, task.getPriority());
                preparedStatement.executeUpdate();
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("Successfully saved all " + Task.taskColor + "Tasks &7in &a" +
                    (System.currentTimeMillis() - startTime) / 1000 + " &7seconds"));
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save tasks", e.getMessage());
        }
    }

    /**
     * Saves all {@link CommandTask} to the Database
     */
    private static void saveCommandTasks() {
        String query = "INSERT INTO command_tasks VALUES(?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (CommandTask task : createdCommandTasks.values()) {
                preparedStatement.setInt(1, task.getTaskID());
                preparedStatement.setString(2, task.getRequiredCommand());
                preparedStatement.executeUpdate();
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO, "Successfully saved all command tasks");
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save tasks", e.getMessage());
        }
    }

    /**
     * Saves all {@link TeleportTask} to the Database
     */
    private static void saveTeleportTasks() {
        String query = "INSERT INTO teleport_tasks VALUES(?,?,?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (TeleportTask teleportTask : createdTeleportTasks.values()) {
                preparedStatement.setInt(1, teleportTask.getTaskID());
                preparedStatement.setString(2, GeneralMethods.locationToString(teleportTask.getFrom()));
                preparedStatement.setString(3, GeneralMethods.locationToString(teleportTask.getTo()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save teleport tasks", e.getMessage());
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
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't save structure", e.getMessage());
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
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load all tutorials! " + e.getMessage());
        }
    }

    /**
     * Loads all {@link Area} from the Database into
     * the {@link #createdAreas createdAreas} map
     */
    public static void loadAreas() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Loading " + Area.areaColor + "Areas&7..."));
        storage.connect();
        String query = "SELECT * FROM areas";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) { // Get all fields from columns
                String areaSpawn = resultSet.getString("spawnpoint");
                int structureID = resultSet.getInt("structureID");
                int tutorialID = resultSet.getInt("tutorialID");
                String areaName = resultSet.getString("name");
                String taskIDs = resultSet.getString("tasks");
                int priority = resultSet.getInt("priority");
                int areaID = resultSet.getInt("areaID");

                Task task = null; // Initiate empty Task
                for (String key : taskIDs.split(",")) // Check if there are tasks in the Table
                    task = createdTasks.get(Integer.parseInt(key));

                if (task == null) { // If there aren't any tasks in the Table
                    Area noTasksArea = new Area(
                            areaID,
                            tutorialID,
                            createdStructures.get(structureID),
                            areaName,
                            GeneralMethods.stringToLocation(areaSpawn),
                            null,
                            priority);

                    createdAreas.put(areaID, noTasksArea);
                } else { // If there are tasks
                    Area area = new Area(
                            areaID,
                            tutorialID,
                            createdStructures.get(structureID),
                            areaName,
                            GeneralMethods.stringToLocation(areaSpawn),
                            new ArrayList<>(createdTasks.values()),
                            priority);

                    createdAreas.put(areaID, area);
                }
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE, ChatUtil.format("&7Successfully loaded all " + Area.areaColor + "Areas&7! \n" +
                    Area.areaColor + "Areas &7loaded in &a" + ((System.currentTimeMillis() - loadingStartTime) / 1000) + "&7seconds!"));
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load all areas! " + e.getMessage());
        }
    }

    /**
     * Loads all forms of {@link Task} via
     * the sub methods {@link #loadCommandTask(int, int, int, Connection) loadCommandTask}
     * and {@link #loadTeleportTask(int, int, int, Connection) loadTeleportTask}
     */
    public static void loadTasks() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.format("&7Loading " + Task.taskColor + "Tasks&7..."));
        storage.connect();
        String query = "SELECT * FROM tasks";
        try (Connection connection = storage.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int taskID = resultSet.getInt("taskID");
                int areaID = resultSet.getInt("areaID");
                String taskType = resultSet.getString("type");
                int priority = resultSet.getInt("priority");
                switch (taskType) {
                    case "CommandTask":
                        loadCommandTask(taskID, areaID, priority, connection);
                        break;
                    case "TeleportTask":
                        loadTeleportTask(taskID, areaID, priority, connection);
                    default:
                        PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Unknown task type! " + taskType);
                        break;
                }
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE, ChatUtil.format("&7Successfully loaded all " + Task.taskColor + "Tasks&7! \n" +
                    Task.taskColor + "Tasks &7loaded in &a" + ((System.currentTimeMillis() - loadingStartTime) / 1000) + "&7seconds!"));
        } catch (SQLException e) {
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load tasks! " + e.getMessage());
        }
    }

    /**
     * Loads the {@link CommandTask} into the
     * {@link #createdCommandTasks createdCommandTasks} and
     * {@link #createdTasks createdTasks} {@link Map}
     *
     * @param taskID TaskID
     * @param areaID AreaID
     * @param priority Priority of the {@link Task}
     * @param connection Connection
     * @throws SQLException If there are any complications
     */
    private static void loadCommandTask(int taskID, int areaID, int priority, Connection connection) throws SQLException {
        String query = "SELECT * FROM command_tasks WHERE taskID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, taskID);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String requiredCommand = resultSet.getString("required_command");
            CommandTask commandTask = new CommandTask(taskID, areaID, priority, requiredCommand);
            createdTasks.put(taskID, commandTask);
            createdCommandTasks.put(taskID, commandTask);
        }
    }

    /**
     * Loads the {@link TeleportTask} into the
     * {@link #createdTeleportTasks createdTeleportTasks} and
     * {@link #createdTasks createdTasks} {@link Map}
     *
     * @param taskID TaskID
     * @param areaID AreaID
     * @param priority Priority of the {@link Task}
     * @param connection Connection
     * @throws SQLException If there are any complications
     */
    private static void loadTeleportTask(int taskID, int areaID, int priority, Connection connection) throws SQLException {
        String query = "SELECT * FROM teleport_tasks WHERE taskID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, taskID);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            String teleportFrom = resultSet.getString("fromLocation");
            String teleportTo = resultSet.getString("toLocation");
            TeleportTask teleportTask = new TeleportTask(
                    taskID,
                    areaID,
                    priority,
                    GeneralMethods.stringToLocation(teleportFrom),
                    GeneralMethods.stringToLocation(teleportTo)
            );
            createdTasks.put(taskID, teleportTask);
            createdTeleportTasks.put(taskID, teleportTask);
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
            storage.disconnect();
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, "Couldn't load all structures! " + e.getMessage());
        }
    }
}
