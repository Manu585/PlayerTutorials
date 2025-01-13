package org.bendersdestiny.playertutorials.utils.memory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

import javax.annotation.Nullable;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Getter
    private static Map<UUID, String> guiCache = new ConcurrentHashMap<>(); // TODO: GET RID OF THIS SHIT / FIND BETTER WAY. Prolly use {@link TutorialPlayer} data



    /**
     * ------------------------------
     * SAVE METHODS
     * ------------------------------
     */



    /**
     * SAVE ALL TUTORIALS
     */
    public static void saveTutorials() {
        long startTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Saving " + Tutorial.tutorialColor + "Tutorials &7...")
        );

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

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                ChatUtil.translateString("&7Saved Tutorial ID: " + tutorial.getId())
                        );

                    }
                } catch (Exception e) {

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.SEVERE,
                            ChatUtil.translateString("&cError retrieving generated ID for tutorial!"),
                            e
                    );

                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Successfully saved all " +
                            Tutorial.tutorialColor + "&6Tutorials &7in &a" +
                            ((System.currentTimeMillis() - startTime) / 1000.0) + " &7seconds")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save tutorials!"),
                    e
            );

        }
    }


    /**
     * SAVE ALL AREAS
     */
    public static void saveAreas() {
        long startTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Saving " + Area.areaColor + "Areas &7...")
        );

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
                ps.setString(4, (area.getSpawnPoint() != null) ? GeneralMethods.locationToString(area.getSpawnPoint()) : "");
                ps.setInt(5, area.getPriority());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        area.setAreaID(keys.getInt(1));

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                ChatUtil.translateString("&7Saved Area ID: " + area.getAreaID())
                        );

                    }
                } catch (Exception e) {

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.SEVERE,
                            ChatUtil.translateString("&cError retrieving generated ID for area!"),
                            e
                    );

                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&aSuccessfully saved all " + Area.areaColor + "Areas &ain &6" +
                            ((System.currentTimeMillis() - startTime) / 1000.0) + " &7s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save areas!"),
                    e
            );

        }
    }


    /**
     * SAVE ALL STRUCTURES
     */
    public static void saveStructures() {
        long startTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Saving Structures...")
        );

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

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                "Saved Structure ID: " + structure.getStructureID()
                        );

                    }
                } catch (Exception e) {

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.SEVERE,
                            ChatUtil.translateString("&cError retrieving generated ID for structure!"),
                            e
                    );

                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Successfully saved all Structures in &a" +
                    ((System.currentTimeMillis() - startTime) / 1000.0) + "s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save structures!"),
                    e
            );

        }
    }


    /**
     * SAVE ALL TASKS
     */
    public static void saveTasks() {
        long startTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Saving Tasks...")
        );

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

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                ChatUtil.translateString("&7Saved Task ID: " + task.getTaskID())
                        );

                    }
                } catch (Exception e) {

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.SEVERE,
                            ChatUtil.translateString("&cError retrieving generated ID for task!")
                    );

                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Successfully saved all Tasks in &a" +
                    ((System.currentTimeMillis() - startTime) / 1000.0) + "s")
            );

            saveCommandTasks();
            saveTeleportTasks();

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save tasks"),
                    e
            );

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
                if (task.getTaskID() == 0) {
                    continue;
                }
                ps.setInt(1, task.getTaskID());
                ps.setString(2, task.getRequiredCommand());
                ps.addBatch();
            }
            ps.executeBatch();

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("Successfully saved all CommandTasks.")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save command tasks"),
                    e
            );

        }
    }


    /**
     * SAVE ALL TELEPORT TASKS
     */
    private static void saveTeleportTasks() {
        String query = "INSERT INTO teleport_tasks (taskID, fromLocation, toLocation) VALUES (?, ?, ?)";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            for (TeleportTask task : createdTeleportTasks.values()) {
                if (task.getTaskID() == 0) {
                    continue;
                }
                ps.setInt(1, task.getTaskID());
                ps.setString(2, (task.getFrom() != null) ? GeneralMethods.locationToString(task.getFrom()) : "");
                ps.setString(3, (task.getTo() != null) ? GeneralMethods.locationToString(task.getTo()) : "");
                ps.addBatch();
            }

            ps.executeBatch();

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Successfully saved all TeleportTasks.")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save teleport tasks"),
                    e
            );

        }
    }


    /**
     * SAVE ALL BLOCKS IN AREA
     */
    public static void saveAreaBlocks() {
        long startTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Saving Area Blocks...")
        );

        String query = "INSERT INTO area_blocks (areaID, relativeX, relativeY, relativeZ, blockType) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            for (Area area : createdAreas.values()) {
                if (area.getStructure() == null) {
                    continue;
                }

                Structure structure = area.getStructure();
                for (StructureBlock block : structure.getBlocks()) {
                    ps.setInt(1, area.getAreaID());
                    ps.setInt(2, block.getRelativeX());
                    ps.setInt(3, block.getRelativeY());
                    ps.setInt(4, block.getRelativeZ());
                    ps.setString(5, block.getMaterial().toString());
                    ps.addBatch();
                }
            }

            ps.executeBatch();

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Successfully saved all Area Blocks in &a" +
                    ((System.currentTimeMillis() - startTime) / 1000.0) + "s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save area blocks"),
                    e
            );

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

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Loading &6Tutorials...")
        );

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

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&8Loaded Tutorial ID: " + tutorialID)
                );

            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.FINE,
                    ChatUtil.translateString("&7Loaded &6Tutorials &7in &a" +
                    ((System.currentTimeMillis() - loadingStartTime) / 1000.0) + "s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load all tutorials!"),
                    e
            );

        }
    }


    /**
     * LOADS ALL STRUCTURES
     */
    public static void loadStructures() {
        long start = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Loading Structures...")
        );

        String query = "SELECT * FROM structures";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int structureID = rs.getInt("structureID");
                int areaID = rs.getInt("areaID");

                // Load blocks for this structure
                Structure structure = loadStructureBlocks(areaID);
                if (structure != null) {
                    structure.setStructureID(structureID);
                    createdStructures.put(structureID, structure);

                    // Link structure to Area
                    Area area = createdAreas.get(areaID);
                    if (area != null) {
                        area.setStructure(structure);

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                ChatUtil.translateString("&7Linked Structure ID " + structureID + " to Area ID " + areaID)
                        );

                    }
                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Loaded Structures in " +
                    ((System.currentTimeMillis() - start) / 1000.0) + "s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load structures!"),
                    e
            );

        }
    }


    /**
     * LOADS ALL AREAS
     */
    public static void loadAreas() {
        long loadingStartTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Loading Areas...")
        );

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

                Structure structure = (structureID != 0) ? createdStructures.get(structureID) : null;

                Area area = new Area(
                        areaID,
                        tutorialID,
                        structure,
                        areaName,
                        spawn,
                        new ArrayList<>(), // Initialize with empty tasks, tasks will be loaded separately
                        priority
                );

                createdAreas.put(areaID, area);

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&7Loaded Area ID: " + areaID)
                );

            }

            PlayerTutorials.getInstance().getLogger().log(Level.INFO, ChatUtil.translateString("&7Loaded Areas in &a" +
                    ((System.currentTimeMillis() - loadingStartTime) / 1000.0) + "s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load areas!"),
                    e
            );

        }
    }


    /**
     * LOADS ALL TASKS
     */
    public static void loadTasks() {
        long startTime = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Loading Tasks...")
        );

        String query = "SELECT * FROM tasks";

        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int taskID = rs.getInt("taskID");
                int areaID = rs.getInt("areaID");
                String taskType = rs.getString("type");
                int priority = rs.getInt("priority");

                switch (taskType) {
                    case "CommandTask":
                        loadCommandTask(taskID, areaID, priority);
                        break;
                    case "TeleportTask":
                        loadTeleportTask(taskID, areaID, priority);
                        break;
                    default:

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.SEVERE,
                                ChatUtil.translateString("&cUnknown task type: " + taskType)
                        );

                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Loaded Tasks in &a" +
                    ((System.currentTimeMillis() - startTime) / 1000.0) + "s")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load tasks!"),
                    e
            );

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

                    // Link to Area
                    Area area = createdAreas.get(areaID);
                    if (area != null) {
                        area.addTask(commandTask);

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                ChatUtil.translateString("&7Linked CommandTask ID " + taskID + " to Area ID " + areaID)
                        );

                    }
                }
            }
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cError loading CommandTask ID " + taskID),
                    e
            );

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
                    Location from = (fromLocationStr != null && !fromLocationStr.isEmpty())
                            ? GeneralMethods.stringToLocation(fromLocationStr)
                            : null;
                    Location to = (toLocationStr != null && !toLocationStr.isEmpty())
                            ? GeneralMethods.stringToLocation(toLocationStr)
                            : null;

                    TeleportTask teleportTask = new TeleportTask(taskID, areaID, priority, from, to);
                    createdTasks.put(taskID, teleportTask);
                    createdTeleportTasks.put(taskID, teleportTask);

                    // Link to Area
                    Area area = createdAreas.get(areaID);
                    if (area != null) {
                        area.addTask(teleportTask);

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.INFO,
                                ChatUtil.translateString("Linked TeleportTask ID " + taskID + " to Area ID " + areaID)
                        );

                    }
                }
            }
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cError loading TeleportTask ID " + taskID),
                    e
            );

        }
    }


    /**
     * Load blocks for a Structure based on areaID.
     */
    private static Structure loadStructureBlocks(int areaID) {
        String query = "SELECT relativeX, relativeY, relativeZ, blockType FROM area_blocks WHERE areaID = ?";
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

            if (blocks.isEmpty()) {

                PlayerTutorials.getInstance().getLogger().log(
                        Level.WARNING,
                        ChatUtil.translateString("&eNo blocks found for Structure of Area ID " + areaID)
                );

                return null;
            }

            return new Structure(0, areaID, blocks);
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cError loading Structure blocks for Area ID " + areaID),
                    e
            );

            return null;
        }
    }



    /**
     * ------------------------------
     * Linking Methods
     * ------------------------------
     */



    /**
     * Link Areas to their respective Tutorials.
     * Should be called after loading Tutorials and Areas.
     */
    public static void linkAreasToTutorials() {
        for (Area area : createdAreas.values()) {
            Tutorial tutorial = createdTutorials.get(area.getTutorialID());
            if (tutorial != null) {
                tutorial.addArea(area);

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&7Linked Area ID " + area.getAreaID() + " to Tutorial ID " + tutorial.getId())
                );

            } else {

                PlayerTutorials.getInstance().getLogger().log(
                        Level.WARNING,
                        ChatUtil.translateString("&eTutorial ID " + area.getTutorialID() + " not found for Area ID " + area.getAreaID())
                );

            }
        }
    }



    /**
     * ------------------------------
     * CREATE METHODS
     * ------------------------------
     */



    /**
     * Create a new Tutorial.
     *
     * @param name  Name of the Tutorial.
     * @param icon  Icon Material for the Tutorial.
     * @return The created Tutorial object, or null if failed.
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

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.INFO,
                            ChatUtil.translateString("&7Created Tutorial ID: " + tutorial.getId())
                    );

                }
            }

            createdTutorials.put(tutorial.getId(), tutorial);
            return tutorial;
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cError creating tutorial '" + name + "'"),
                    e
            );

            return null;
        }
    }


    /**
     * Create a new Area.
     *
     * @param tutorialID  ID of the parent Tutorial.
     * @param name        Name of the Area.
     * @param spawnPoint  Spawn Location for the Area (can be null).
     * @param priority    Priority of the Area.
     * @return The created Area object, or null if failed.
     */
    public static Area createArea(int tutorialID, String name, @Nullable Location spawnPoint, int priority) {
        Area area = new Area(0, tutorialID, null, name, spawnPoint, new ArrayList<>(), priority);

        String query = "INSERT INTO areas (tutorialID, structureID, name, spawnPoint, priority) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, tutorialID);
            ps.setInt(2, 0); // No structure yet
            ps.setString(3, name);
            ps.setString(4, (spawnPoint != null) ? GeneralMethods.locationToString(spawnPoint) : "");
            ps.setInt(5, priority);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    area.setAreaID(rs.getInt(1));

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.INFO,
                            ChatUtil.translateString("&7Created Area ID: " + area.getAreaID())
                    );

                }
            }

            createdAreas.put(area.getAreaID(), area);

            // Link Area to Tutorial
            Tutorial tutorial = createdTutorials.get(tutorialID);
            if (tutorial != null) {
                tutorial.addArea(area);
            }

            return area;
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cError creating area '" + name + "'"),
                    e
            );

            return null;
        }
    }


    /**
     * Add a Structure to an existing Area.
     *
     * @param areaID  ID of the Area.
     * @param blocks  List of StructureBlocks representing the Structure.
     * @return The created Structure object, or null if failed.
     */
    public static Structure addStructureToArea(int areaID, List<StructureBlock> blocks) {
        Area area = createdAreas.get(areaID);
        if (area == null) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cArea ID " + areaID + " not found. Cannot add Structure.")
            );

            return null;
        }

        Structure structure = new Structure(0, areaID, blocks);

        String query = "INSERT INTO structures (areaID) VALUES (?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, areaID);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    structure.setStructureID(rs.getInt(1));

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.INFO,
                            ChatUtil.translateString("&7Created Structure ID: " + structure.getStructureID())
                    );

                }
            }

            createdStructures.put(structure.getStructureID(), structure);
            area.setStructure(structure);

            saveAreaBlocks();

            return structure;
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Error adding structure to Area ID " + areaID),
                    e
            );

            return null;
        }
    }


    /**
     * Create a new CommandTask and link it to an Area.
     *
     * @param areaID          ID of the Area.
     * @param requiredCommand The command the player must execute.
     * @param priority        Priority of the Task.
     * @return The created CommandTask object, or null if failed.
     */
    public static CommandTask createCommandTask(int areaID, String requiredCommand, int priority) {
        Task task = new CommandTask(0, areaID, priority, requiredCommand);

        String query = "INSERT INTO tasks (areaID, type, priority) VALUES (?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, areaID);
            ps.setString(2, task.getTaskType());
            ps.setInt(3, task.getPriority());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setTaskID(rs.getInt(1));

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.INFO,
                            ChatUtil.translateString("Created CommandTask ID: " + task.getTaskID())
                    );

                }
            }

            createdTasks.put(task.getTaskID(), task);
            createdCommandTasks.put(task.getTaskID(), (CommandTask) task);

            // Link Task to Area
            Area area = createdAreas.get(areaID);
            if (area != null) {
                area.addTask(task);
            }

            // Save CommandTask details
            saveCommandTaskDetails((CommandTask) task);

            return (CommandTask) task;
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Error creating CommandTask for Area ID " + areaID),
                    e
            );

            return null;
        }
    }


    /**
     * Create a new TeleportTask and link it to an Area.
     *
     * @param areaID ID of the Area.
     * @param from   From Location.
     * @param to     To Location.
     * @param priority Priority of the Task.
     * @return The created TeleportTask object, or null if failed.
     */
    public static TeleportTask createTeleportTask(int areaID, Location from, Location to, int priority) {
        Task task = new TeleportTask(0, areaID, priority, from, to);

        String query = "INSERT INTO tasks (areaID, type, priority) VALUES (?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, areaID);
            ps.setString(2, task.getTaskType());
            ps.setInt(3, task.getPriority());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setTaskID(rs.getInt(1));

                    PlayerTutorials.getInstance().getLogger().log(
                            Level.INFO,
                            ChatUtil.translateString("Created TeleportTask ID: " + task.getTaskID())
                    );

                }
            }

            createdTasks.put(task.getTaskID(), task);
            createdTeleportTasks.put(task.getTaskID(), (TeleportTask) task);

            // Link Task to Area
            Area area = createdAreas.get(areaID);
            if (area != null) {
                area.addTask(task);
            }

            // Save TeleportTask details
            saveTeleportTaskDetails((TeleportTask) task);

            return (TeleportTask) task;
        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Error creating TeleportTask for Area ID " + areaID),
                    e
            );

            return null;
        }
    }


    /**
     * Save CommandTask specific details.
     */
    private static void saveCommandTaskDetails(CommandTask cmdTask) {
        String query = "INSERT INTO command_tasks (taskID, required_command) VALUES (?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, cmdTask.getTaskID());
            ps.setString(2, cmdTask.getRequiredCommand());
            ps.executeUpdate();

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Saved CommandTask details for Task ID: " + cmdTask.getTaskID())
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cError saving CommandTask details for Task ID " + cmdTask.getTaskID()),
                    e
            );

        }
    }


    /**
     * Save TeleportTask specific details.
     */
    private static void saveTeleportTaskDetails(TeleportTask tpTask) {
        String query = "INSERT INTO teleport_tasks (taskID, fromLocation, toLocation) VALUES (?, ?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, tpTask.getTaskID());
            ps.setString(2, (tpTask.getFrom() != null) ? GeneralMethods.locationToString(tpTask.getFrom()) : "");
            ps.setString(3, (tpTask.getTo() != null) ? GeneralMethods.locationToString(tpTask.getTo()) : "");
            ps.executeUpdate();

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("Saved TeleportTask details for Task ID: " + tpTask.getTaskID())
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Error saving TeleportTask details for Task ID " + tpTask.getTaskID()),
                    e
            );

        }
    }



    /**
     * ------------------------------
     * Modify Methods
     * ------------------------------
     */



    /**
     * Delete a Tutorial from the database and memory.
     *
     * @param tutorial The Tutorial to delete.
     */
    public static void deleteTutorial(Tutorial tutorial) {
        String delTutorials = "DELETE FROM tutorials WHERE id=?";

        try (Connection connection = storage.getConnection();
             PreparedStatement stmt = connection.prepareStatement(delTutorials)) {

            stmt.setInt(1, tutorial.getId());
            stmt.executeUpdate();
            createdTutorials.remove(tutorial.getId());

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("Deleted Tutorial ID: " + tutorial.getId())
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Couldn't delete Tutorial!"),
                    e
            );

        }
    }


    /**
     * Rename a Tutorial in the database and memory.
     *
     * @param tutorial The Tutorial to rename.
     * @param newName  The new name for the Tutorial.
     */
    public static void renameTutorial(Tutorial tutorial, String newName) {
        String renameQuery = "UPDATE tutorials SET name=? WHERE id=?";
        try (Connection connection = storage.getConnection();
             PreparedStatement ps = connection.prepareStatement(renameQuery)) {

            ps.setString(1, newName);
            ps.setInt(2, tutorial.getId());
            ps.executeUpdate();
            tutorial.setName(newName);

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("Renamed Tutorial ID " + tutorial.getId() + " to '" + newName + "'")
            );

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Couldn't rename Tutorial!"),
                    e
            );

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

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Error linking areaID=" + areaID + " with taskID=" + taskID),
                    e
            );

        }
    }



    /**
     * ------------------------------
     * Helper Methods
     * ------------------------------
     */



    /**
     * Load all data from the database.
     */
    public static void loadAllData() {
        loadTutorials();
        loadAreas();
        loadStructures();
        loadTasks();
        linkAreasToTutorials();
    }


    /**
     * Save all data to the database.
     */
    public static void saveAllData() {
        saveTutorials();
        saveAreas();
        saveStructures();
        saveTasks();
        saveAreaBlocks();
    }
}
