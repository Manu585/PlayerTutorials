package org.bendersdestiny.playertutorials.utils.memory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.area.structure.StructureBlock;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.CommandTask;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.TeleportTask;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Utility class for all {@link Tutorial} related Memory and more.
 * Helps with loading and saving all {@link Tutorial}, {@link Area},
 * {@link Task} and {@link Structure}!
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) @SuppressWarnings("all")
public class MemoryUtil {
    private static final Storage storage = PlayerTutorials.getInstance().getStorage();

    @Getter
    private static final Map<Integer, Tutorial> createdTutorials = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, Area> createdAreas = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, Structure> createdStructures = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, Task> createdTasks = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, TeleportTask> createdTeleportTasks = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, CommandTask> createdCommandTasks = new ConcurrentHashMap<>();

    // Some caches
    @Getter
    private static final Map<UUID, Map<Integer, String>> guiCache = new ConcurrentHashMap<>();
    @Getter
    private static final Map<UUID, Tutorial> modifyTutorialCache = new ConcurrentHashMap<>();


    /**
     * ------------------------------
     * Bulk SAVE Methods
     * ------------------------------
     */


    /**
     * SAVE ALL TUTORIALS
     */
    public static void saveTutorials() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                .serialize(ChatUtil.translate("&7Saving " + Tutorial.tutorialColor + "Tutorials &7...")));

        String query = "INSERT INTO tutorials (name, icon) VALUES(?, ?)";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Tutorial tutorial : createdTutorials.values()) {

                if (tutorial.getId() != 0) {
                    continue;
                }

                ps.setString(1, tutorial.getName());
                ps.setString(2, tutorial.getIcon().toString());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        tutorial.setId(keys.getInt(1));
                    }
                } catch (Exception e) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, LegacyComponentSerializer.legacySection()
                            .serialize(ChatUtil.translate("&cError retrieving generated ID for tutorial!")));
                }
            }

            PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                    .serialize(ChatUtil.translate("&7Successfully saved all " + Tutorial.tutorialColor + "&6Tutorials &7in &a" +
                            ((System.currentTimeMillis() - startTime) / 1000.0) + " &7s")));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't save tutorials", e);
        }
    }


    /**
     * SAVE ALL AREAS
     */
    public static void saveAreas() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                .serialize(ChatUtil.translate("&7Saving " + Area.areaColor + "Areas &7...")));

        String query = "INSERT INTO areas (tutorialID, structureID, name, spawnPoint, priority) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Area area : createdAreas.values()) {
                if (area.getAreaID() != 0) {
                    continue;
                }

                ps.setInt(1, area.getTutorialID());
                ps.setInt(2, (area.getStructure() != null) ? area.getStructure().getStructureID() : 0);
                ps.setString(3, area.getName());
                ps.setString(4, (area.getSpawnPoint() != null)
                        ? GeneralMethods.locationToString(area.getSpawnPoint())
                        : "");
                ps.setInt(5, area.getPriority());

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        area.setAreaID(keys.getInt(1));
                    }
                } catch (Exception ex) {
                    PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                            "Error retrieving generated ID for area!", ex);
                }
            }

            PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                    .serialize(ChatUtil.translate("&aSuccessfully saved all " + Area.areaColor + "Areas &ain &6" +
                            ((System.currentTimeMillis() - startTime) / 1000.0) + " &7s")));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE, LegacyComponentSerializer.legacySection()
                    .serialize(ChatUtil.translate("&cCouldn't save areas" + e)));
        }
    }


    /**
     * SAVE ALL TASKS
     */
    public static void saveTasks() {
        saveGeneralTasks();
        saveCommandTasks();
        saveTeleportTasks();
    }


    /**
     * SAVE ALL GENERAL TASKS
     */
    private static void saveGeneralTasks() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                .serialize(ChatUtil.translate("&7Saving " + Task.taskColor + "Tasks &7...")));

        String query = "INSERT INTO tasks (areaID, type, priority) VALUES (?, ?, ?)";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Task task : createdTasks.values()) {
                if (task.getTaskID() != 0) {
                    continue;
                }

                ps.setInt(1, task.getAreaID());
                ps.setString(2, task.getTaskType());
                ps.setInt(3, task.getPriority());

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        task.setTaskID(keys.getInt(1));
                    }
                }
            }

            PlayerTutorials.getInstance().getLogger().log(Level.INFO,
                    "Successfully saved all " + Task.taskColor + "Tasks in &a" +
                            ((System.currentTimeMillis() - startTime) / 1000.0) + " &7s");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't save tasks", e);
        }
    }


    /**
     * SAVE ALL COMMAND TASKS
     */
    private static void saveCommandTasks() {
        String query = "INSERT INTO command_tasks (taskID, required_command) VALUES (?, ?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (CommandTask task : createdCommandTasks.values()) {
                ps.setInt(1, task.getTaskID());
                ps.setString(2, task.getRequiredCommand());
                ps.executeUpdate();
            }
            PlayerTutorials.getInstance().getLogger().log(Level.INFO,
                    "Successfully saved all command tasks");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't save command tasks", e);
        }
    }


    /**
     * SAVE ALL TELEPORT TASKS
     */
    private static void saveTeleportTasks() {
        String query = "INSERT INTO teleport_tasks (taskID, fromLocation, toLocation) VALUES (?, ?, ?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (TeleportTask teleportTask : createdTeleportTasks.values()) {
                ps.setInt(1, teleportTask.getTaskID());
                ps.setString(2, GeneralMethods.locationToString(teleportTask.getFrom()));
                ps.setString(3, GeneralMethods.locationToString(teleportTask.getTo()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't save teleport tasks", e);
        }
    }


    /**
     * SAVE ALL STRUCTURES
     */
    public static void saveStructures() {
        String query = "INSERT INTO structures (areaID) VALUES (?)";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (Structure structure : createdStructures.values()) {
                if (structure.getStructureID() != 0) {
                    continue;
                }

                ps.setInt(1, structure.getAreaID());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        structure.setStructureID(keys.getInt(1));
                    }
                }
            }

            PlayerTutorials.getInstance().getLogger().log(Level.INFO,
                    "Successfully saved all Structures");

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't save structure", e);
        }
    }


    /**
     * ------------------------------
     * Bulk LOAD Methods
     * ------------------------------
     */


    /**
     * LOADS ALL TUTORIALS
     */
    public static void loadTutorials() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                .serialize(ChatUtil.translate("&7Loading &6Tutorials...")));

        String query = "SELECT * FROM tutorials";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int tutorialID = rs.getInt("id");
                String tutorialName = rs.getString("name");
                String tutorialIcon = rs.getString("icon");

                Tutorial t = new Tutorial(
                        tutorialID,
                        tutorialName,
                        Material.valueOf(tutorialIcon.toUpperCase())
                );
                createdTutorials.put(tutorialID, t);
            }

            PlayerTutorials.getInstance().getLogger().log(Level.FINE, LegacyComponentSerializer.legacySection()
                    .serialize(ChatUtil.translate("&7Loaded &6Tutorials &7in &a" +
                    ((System.currentTimeMillis() - loadingStartTime) / 1000.0) + "s")));
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't load all tutorials!", e);
        }
    }


    /**
     * LOADS ALL AREAS
     */
    public static void loadAreas() {
        long loadingStartTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                .serialize(ChatUtil.translate("&7Loading Areas...")));

        String query = "SELECT * FROM areas";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int areaID = rs.getInt("areaID");
                int tutorialID = rs.getInt("tutorialID");
                int structureID = rs.getInt("structureID");
                String areaName = rs.getString("name");
                String spawnStr = rs.getString("spawnPoint");
                int priority = rs.getInt("priority");

                Location spawn = (spawnStr != null && !spawnStr.isEmpty())
                        ? GeneralMethods.stringToLocation(spawnStr)
                        : null;

                Area area = new Area(
                        areaID,
                        tutorialID,
                        createdStructures.get(structureID),
                        areaName,
                        spawn,
                        null, // Load tasks separately or link them after
                        priority
                );

                createdAreas.put(areaID, area);
            }
            PlayerTutorials.getInstance().getLogger().log(Level.FINE,
                    "&7Loaded Areas in &a" +
                            ((System.currentTimeMillis() - loadingStartTime) / 1000.0) + "s");
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't load areas!", e);
        }
    }


    /**
     * LOADS ALL TASKS
     */
    public static void loadTasks() {
        long startTime = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                .serialize(ChatUtil.translate("&7Loading Tasks...")));

        String query = "SELECT * FROM tasks";

        // 1) 'try' block for the main tasks query
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int taskID = rs.getInt("taskID");
                int areaID = rs.getInt("areaID");
                String taskType = rs.getString("type");
                int priority = rs.getInt("priority");

                switch (taskType) {
                    case "CommandTask" -> loadCommandTask(taskID, areaID, priority);
                    case "TeleportTask" -> loadTeleportTask(taskID, areaID, priority);
                    default -> PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                            "Unknown task type! " + taskType);
                }
            }

            PlayerTutorials.getInstance().getLogger().log(Level.INFO, LegacyComponentSerializer.legacySection()
                    .serialize(ChatUtil.translate("&7Loaded Tasks in &a" +
                            ((System.currentTimeMillis() - startTime) / 1000.0) + "s")));

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't load tasks!", e);
        }
    }



    /**
     * LOADS ALL COMMAND TASKS
     */
    private static void loadCommandTask(int taskID, int areaID, int priority) {
        String query = "SELECT required_command FROM command_tasks WHERE taskID = ?";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, taskID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String requiredCommand = rs.getString("required_command");
                    CommandTask commandTask = new CommandTask(taskID, areaID, priority, requiredCommand);
                    createdTasks.put(taskID, commandTask);
                    createdCommandTasks.put(taskID, commandTask);
                }
            }
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error loading CommandTask with ID=" + taskID, e);
        }
    }


    /**
     * LOADS ALL TELEPORT TASKS
     */
    private static void loadTeleportTask(int taskID, int areaID, int priority) {
        String query = "SELECT fromLocation, toLocation FROM teleport_tasks WHERE taskID = ?";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, taskID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fromLocationStr = rs.getString("fromLocation");
                    String toLocationStr = rs.getString("toLocation");
                    Location from = (fromLocationStr != null)
                            ? GeneralMethods.stringToLocation(fromLocationStr)
                            : null;
                    Location to = (toLocationStr != null)
                            ? GeneralMethods.stringToLocation(toLocationStr)
                            : null;

                    TeleportTask teleportTask = new TeleportTask(taskID, areaID, priority, from, to);
                    createdTasks.put(taskID, teleportTask);
                    createdTeleportTasks.put(taskID, teleportTask);
                }
            }
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error loading TeleportTask with ID=" + taskID, e);
        }
    }


    /**
     * LOADS ALL STRUCTURES
     */
    public static void loadStructures() {
        long start = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().info("Loading Structures...");

        String query = "SELECT structureID, areaID FROM structures";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int structureID = rs.getInt("structureID");
                int areaID = rs.getInt("areaID");

                List<StructureBlock> blocks = new ArrayList<>();

                Structure structure = new Structure(structureID, areaID, blocks);
                createdStructures.put(structureID, structure);
            }

            PlayerTutorials.getInstance().getLogger().info(
                    "Loaded Structures in " +
                            ((System.currentTimeMillis() - start) / 1000.0) + "s"
            );

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE, "Couldn't load structures!", e
            );
        }
    }


    /**
     * Attach loaded Areas to Tutorials.
     * Runs after areas and tutorials are loaded
     */
    public static void addAreasToTutorials() {
        for (Area area : createdAreas.values()) {
            Tutorial t = createdTutorials.get(area.getTutorialID());
            if (t != null) {
                t.addArea(area);
            }
        }
    }


    /**
     * ------------------------------
     * OTHER METHODS
     * ------------------------------
     */


    /**
     * Delete a tutorial from DB + memory
     */
    public static void deleteTutorial(Tutorial tutorial) {
        String delTutorials = "DELETE FROM tutorials WHERE id=?";

        try (Connection connection = storage.getConnection();
             PreparedStatement stmt = connection.prepareStatement(delTutorials)) {
            stmt.setInt(1, tutorial.getId());
            stmt.executeUpdate();
            createdTutorials.remove(tutorial.getId());
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't delete Tutorial!", e);
        }
    }


    /**
     * Rename a tutorial
     */
    public static void renameTutorial(Tutorial tutorial, String newName) {
        String renameQuery = "UPDATE tutorials SET name=? WHERE id=?";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(renameQuery)) {
            ps.setString(1, newName);
            ps.setInt(2, tutorial.getId());
            ps.executeUpdate();
            tutorial.setName(newName);
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Couldn't rename Tutorial!", e);
        }
    }


    /**
     * ------------------------------
     * "Instant" CREATE methods
     * ------------------------------
     */


    /**
     * Create a Tutorial
     */
    public static Tutorial createTutorial(String name, Material icon) {
        Tutorial tutorial = new Tutorial(0, name, icon);

        String query = "INSERT INTO tutorials (name, icon) VALUES (?, ?)";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, icon.toString());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tutorial.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error creating tutorial '" + name + "'", e);
            return null;
        }

        createdTutorials.put(tutorial.getId(), tutorial);
        return tutorial;
    }


    /**
     * Create an Area
     */
    public static Area createArea(int tutorialID, Structure structure,
                                  String name, Location spawnPoint,
                                  List<Task> tasks, int priority) {

        Area area = new Area(0, tutorialID, structure, name, spawnPoint, tasks, priority);

        String query = "INSERT INTO areas (tutorialID, structureID, name, spawnPoint, priority)"
                + " VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, tutorialID);
            ps.setInt(2, (structure != null) ? structure.getStructureID() : 0);
            ps.setString(3, name);
            ps.setString(4, (spawnPoint != null) ? GeneralMethods.locationToString(spawnPoint) : "");
            ps.setInt(5, priority);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    area.setAreaID(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error creating area '" + name + "'", e);
            return null;
        }

        createdAreas.put(area.getAreaID(), area);

        // Attach area to tutorial
        Tutorial parentTut = createdTutorials.get(tutorialID);
        if (parentTut != null) {
            parentTut.addArea(area);
        }

        // If tasks were passed, create them + link
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.getTaskID() <= 0) {
                    createTask(task);
                }
                linkAreaTask(area.getAreaID(), task.getTaskID());
            }
        }
        return area;
    }


    /**
     * Create a Task
     */
    public static Task createTask(Task task) {
        String sql = "INSERT INTO tasks (areaID, type, priority) VALUES (?, ?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, task.getAreaID());
            ps.setString(2, task.getTaskType());
            ps.setInt(3, task.getPriority());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setTaskID(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error creating task with type=" + task.getTaskType(), e);
            return null;
        }

        createdTasks.put(task.getTaskID(), task);

        if (task instanceof CommandTask) {
            createCommandTask((CommandTask) task);
        } else if (task instanceof TeleportTask) {
            createTeleportTask((TeleportTask) task);
        }

        return task;
    }


    /**
     * Create a Command Task
     */
    public static void createCommandTask(CommandTask cmdTask) {
        String sql = "INSERT INTO command_tasks (taskID, required_command) VALUES (?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cmdTask.getTaskID());
            ps.setString(2, cmdTask.getRequiredCommand());
            ps.executeUpdate();
            createdCommandTasks.put(cmdTask.getTaskID(), cmdTask);

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error creating command task", e);
        }
    }


    /**
     * Create a Teleport Task
     */
    public static void createTeleportTask(TeleportTask tpTask) {
        String sql = "INSERT INTO teleport_tasks (taskID, fromLocation, toLocation) VALUES (?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tpTask.getTaskID());
            ps.setString(2, (tpTask.getFrom() != null) ? GeneralMethods.locationToString(tpTask.getFrom()) : "");
            ps.setString(3, (tpTask.getTo() != null) ? GeneralMethods.locationToString(tpTask.getTo()) : "");
            ps.executeUpdate();
            createdTeleportTasks.put(tpTask.getTaskID(), tpTask);

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error creating teleport task", e);
        }
    }


    /**
     * Link Tasks to Area
     */
    private static void linkAreaTask(int areaID, int taskID) {
        String sql = "INSERT OR IGNORE INTO area_tasks (areaID, taskID) VALUES (?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, areaID);
            ps.setInt(2, taskID);
            ps.executeUpdate();

        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().log(Level.SEVERE,
                    "Error linking areaID=" + areaID + " with taskID=" + taskID, e);
        }
    }

    /**
     * Saves blocks in an {@link Area} in the DB
     *
     * @param area Blocks in area
     * @param pos1 First position
     * @param pos2 Second position
     */
    public static void saveAreaBlocks(Area area, Location pos1, Location pos2) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        String sql = "INSERT OR IGNORE INTO area_blocks (areaID, relativeX, relativeY, relativeZ, blockType) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Iterate all blocks in bounding box
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Location loc = new Location(pos1.getWorld(), x, y, z);
                        Material mat = loc.getBlock().getType();

                        int relX = x - minX;
                        int relY = y - minY;
                        int relZ = z - minZ;

                        ps.setInt(1, area.getAreaID());
                        ps.setInt(2, relX);
                        ps.setInt(3, relY);
                        ps.setInt(4, relZ);
                        ps.setString(5, mat.toString());
                        ps.addBatch();
                    }
                }
            }
            ps.executeBatch();
        } catch (SQLException e) {
            PlayerTutorials.getInstance().getLogger().severe("Error saving blocks for area " + area.getName());
            e.printStackTrace();
        }
    }


    public static Structure loadStructure(int areaID) {
        String query = "SELECT relativeX, relativeY, relativeZ, blockType FROM area_blocks WHERE areaID=?";
        List<StructureBlock> blocks = new ArrayList<>();

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, areaID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int rx = rs.getInt("relativeX");
                    int ry = rs.getInt("relativeY");
                    int rz = rs.getInt("relativeZ");
                    String matStr = rs.getString("blockType");

                    Material material = Material.matchMaterial(matStr);
                    if (material == null) material = Material.AIR;

                    blocks.add(new StructureBlock(rx, ry, rz, material));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return new Structure(0, areaID, blocks);
    }

}
