package org.bendersdestiny.playertutorials.utils.memory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.tutorial.area.structure.AreaBlock;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.CommandTask;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.TeleportTask;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.storage.Storage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Utility class for all {@link Tutorial} related Memory and more.
 * Helps with loading and saving all {@link Tutorial}, {@link Area},
 * {@link Task}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryUtil {
    private static final Storage storage = PlayerTutorials.getInstance().getStorage();

    @Getter
    private static final Map<Integer, Tutorial> createdTutorials = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, Area> createdAreas = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, Task> createdTasks = new ConcurrentHashMap<>();


    // ------------------------------------------------------------------------
    // Instantly-Save: TUTORIALS / AREAS / TASKS
    // ------------------------------------------------------------------------

    /**
     * Create + save a new {@link Tutorial} to DB & memory.
     */
    public static Tutorial createTutorial(String name, Material icon) {
        Tutorial tutorial = new Tutorial(0, name, icon);

        String sql = "INSERT INTO tutorials (name, icon) VALUES (?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
     * Create + save a new {@link Area} in DB & memory.
     */
    public static Area createArea(int tutorialID, String name, Location pos1, Location pos2, int priority) {
        if (pos1 == null || pos2 == null) return null;

        List<AreaBlock> blocks;
        blocks = collectAreaBlocks(pos1, pos2);


        Area area = new Area(
                0,
                tutorialID,
                name,
                null,
                new ArrayList<>(),
                blocks,
                priority
        );

        area.setSpawnPoint(pos1.clone());

        String sql = "INSERT INTO areas (tutorialID, name, spawnPoint, priority) VALUES (?, ?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, tutorialID);
            ps.setString(2, name);
            ps.setString(3, GeneralMethods.locationToString(area.getSpawnPoint()));
            ps.setInt(4, priority);
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

            // Link area -> tutorial
            Tutorial tutorial = createdTutorials.get(tutorialID);
            if (tutorial != null) {
                tutorial.addArea(area);
            }

            storeAreaBlocks(area);
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
     * Creates a new {@link CommandTask}, saves instantly, and links to the specified {@link Area}.
     */
    public static CommandTask createCommandTask(int areaID, String requiredCommand, int priority) {
        CommandTask task = new CommandTask(0, areaID, priority, requiredCommand);

        String sql = "INSERT INTO tasks (areaID, type, priority) VALUES (?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

            // Link to area
            Area area = createdAreas.get(areaID);
            if (area != null) {
                area.addTask(task);
            }

            // CommandTask details
            String cmdSql = "INSERT INTO command_tasks (taskID, required_command) VALUES (?, ?)";
            try (PreparedStatement cmdPs = conn.prepareStatement(cmdSql)) {
                cmdPs.setInt(1, task.getTaskID());
                cmdPs.setString(2, requiredCommand);
                cmdPs.executeUpdate();

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&7Saved CommandTask details for Task ID: " + task.getTaskID())
                );

            }
            return task;
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
     * Create + save a new {@link TeleportTask} in DB & memory, linking to an existing {@link Area}.
     */
    public static TeleportTask createTeleportTask(int areaID, Location from, Location to, int priority) {
        TeleportTask task = new TeleportTask(0, areaID, priority, from, to);

        String sql = "INSERT INTO tasks (areaID, type, priority) VALUES (?, ?, ?)";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

            // Link task -> area
            Area area = createdAreas.get(areaID);
            if (area != null) {
                area.addTask(task);
            }

            // TeleportTask details
            String tpSql = "INSERT INTO teleport_tasks (taskID, fromLocation, toLocation) VALUES (?, ?, ?)";
            try (PreparedStatement tpPs = conn.prepareStatement(tpSql)) {
                tpPs.setInt(1, task.getTaskID());
                tpPs.setString(2, (from != null) ? GeneralMethods.locationToString(from) : "");
                tpPs.setString(3, (to != null)   ? GeneralMethods.locationToString(to)   : "");
                tpPs.executeUpdate();

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("Saved TeleportTask details for Task ID: " + task.getTaskID())
                );
            }

            return task;

        } catch (SQLException e) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("Error creating TeleportTask for Area ID " + areaID),
                    e
            );

            return null;
        }
    }

    // ------------------------------------------------------------------------
    // Area Blocks
    // ------------------------------------------------------------------------

    /**
     * Gathers blocks between pos1 and pos2 (inclusive) into a list of {@link AreaBlock},
     * computing the relative offsets from the minimum corner.
     */
    private static List<AreaBlock> collectAreaBlocks(Location pos1, Location pos2) {
        List<AreaBlock> blockList = new ArrayList<>();
        if (pos1.getWorld() == null || pos2.getWorld() == null) {
            return blockList;
        }

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            return blockList;
        }

        World w = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location blockLoc = new Location(w, x, y, z);
                    Material mat = blockLoc.getBlock().getType();

                    int relX = x - minX;
                    int relY = y - minY;
                    int relZ = z - minZ;
                    blockList.add(new AreaBlock(relX, relY, relZ, mat));
                }
            }
        }
        return blockList;
    }

    private static void storeAreaBlocks(Area area) {
        if (area.getBlocks() == null || area.getBlocks().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO area_blocks (areaID, relativeX, relativeY, relativeZ, blockType) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (AreaBlock block : area.getBlocks()) {
                ps.setInt(1, area.getAreaID());
                ps.setInt(2, block.getRelativeX());
                ps.setInt(3, block.getRelativeY());
                ps.setInt(4, block.getRelativeZ());
                ps.setString(5, block.getMaterial().toString());
                ps.addBatch();
            }
            ps.executeBatch();

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Stored " + area.getBlocks().size() +
                            " blocks in DB for Area ID " + area.getAreaID())
            );

        } catch (SQLException ex) {
            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't save blocks for area " + area.getName() + " (ID " + area.getAreaID() + ")"),
                    ex
            );
        }
    }

    // ------------------------------------------------------------------------
    // Loading Methods
    // ------------------------------------------------------------------------

    /**
     * Load all data from DB into memory. (Tutorials -> Areas -> Tasks, then link).
     */
    public static void loadAllData() {
        loadTutorials();
        loadAreas();
        loadAreaBlocks();
        loadTasks();
        linkAreasToTutorials();
    }

    /**
     * Loads area_blocks from DB for each area, populating area.getBlocks().
     */
    private static void loadAreaBlocks() {
        String sql = "SELECT areaID, relativeX, relativeY, relativeZ, blockType FROM area_blocks";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int areaId = rs.getInt("areaID");
                int rx = rs.getInt("relativeX");
                int ry = rs.getInt("relativeY");
                int rz = rs.getInt("relativeZ");
                String blockMat = rs.getString("blockType");
                Material material = Material.matchMaterial(blockMat);
                if (material == null) {
                    material = Material.AIR;
                }

                AreaBlock ab = new AreaBlock(rx, ry, rz, material);

                Area area = createdAreas.get(areaId);
                if (area != null) {
                    area.getBlocks().add(ab);
                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Loaded area_blocks from DB & attached to Areas.")
            );

        } catch (SQLException ex) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load area_blocks from DB!"),
                    ex
            );

        }
    }

    /**
     * LOADS ALL TUTORIALS
     */
    public static void loadTutorials() {
        long t0 = System.currentTimeMillis();
        PlayerTutorials.getInstance().getLogger().log(

                Level.INFO,
                ChatUtil.translateString("&7Loading &6Tutorials...")
        );

        String sql = "SELECT * FROM tutorials";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int tid = rs.getInt("id");
                String tname = rs.getString("name");
                String ticon = rs.getString("icon");

                Material mat;
                try {
                    mat = Material.valueOf(ticon.toUpperCase());
                } catch (Exception e) {
                    mat = Material.DIORITE;
                }
                Tutorial tut = new Tutorial(tid, tname, mat);
                createdTutorials.put(tid, tut);

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&8Loaded Tutorial ID: " + tid)
                );

            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.FINE,
                    ChatUtil.translateString("&7Loaded &6Tutorials &7in &a"
                            + ((System.currentTimeMillis() - t0) / 1000.0) + "s")
            );

        } catch (SQLException ex) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load all tutorials!"),
                    ex
            );

        }
    }

    /**
     * LOADS ALL AREAS (without blocks).
     */
    public static void loadAreas() {
        long t0 = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Loading Areas...")
        );

        String sql = "SELECT * FROM areas";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int aid = rs.getInt("areaID");
                int tid = rs.getInt("tutorialID");
                String aname = rs.getString("name");
                String spawnStr = rs.getString("spawnPoint");
                int prio = rs.getInt("priority");

                Location spawn = (spawnStr != null && !spawnStr.isEmpty())
                        ? GeneralMethods.stringToLocation(spawnStr)
                        : null;

                // Fill blocks later from area_blocks, tasks also loaded separately
                Area area = new Area(
                        aid,
                        tid,
                        aname,
                        spawn,
                        new ArrayList<>(), // tasks
                        new ArrayList<>(), // blocks
                        prio
                );

                createdAreas.put(aid, area);

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&7Loaded Area ID: " + aid)
                );

            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Loaded Areas in &a"
                            + ((System.currentTimeMillis() - t0) / 1000.0) + "s")
            );

        } catch (SQLException ex) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load areas!"),
                    ex
            );

        }
    }

    /**
     * LOADS ALL TASKS
     */
    public static void loadTasks() {
        long t0 = System.currentTimeMillis();

        PlayerTutorials.getInstance().getLogger().log(
                Level.INFO,
                ChatUtil.translateString("&7Loading Tasks...")
        );

        String sql = "SELECT * FROM tasks";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int taskId = rs.getInt("taskID");
                int areaId = rs.getInt("areaID");
                String taskType = rs.getString("type");
                int priority = rs.getInt("priority");

                switch (taskType) {
                    case "CommandTask" -> loadCommandTask(taskId, areaId, priority);
                    case "TeleportTask" -> loadTeleportTask(taskId, areaId, priority);
                    default -> {

                        PlayerTutorials.getInstance().getLogger().log(
                                Level.SEVERE,
                                ChatUtil.translateString("&cUnknown task type: " + taskType)
                        );

                    }
                }
            }

            PlayerTutorials.getInstance().getLogger().log(
                    Level.INFO,
                    ChatUtil.translateString("&7Loaded Tasks in &a"
                            + ((System.currentTimeMillis() - t0) / 1000.0) + "s")
            );

        } catch (SQLException ex) {

            PlayerTutorials.getInstance().getLogger().log(
                    Level.SEVERE,
                    ChatUtil.translateString("&cCouldn't load tasks!"),
                    ex
            );

        }
    }

    private static void loadCommandTask(int taskID, int areaID, int priority) {
        String sql = "SELECT required_command FROM command_tasks WHERE taskID=?";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String cmd = rs.getString("required_command");
                    CommandTask ctask = new CommandTask(taskID, areaID, priority, cmd);
                    createdTasks.put(taskID, ctask);

                    // Link to area
                    Area area = createdAreas.get(areaID);
                    if (area != null) {
                        area.addTask(ctask);

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

    private static void loadTeleportTask(int taskID, int areaID, int priority) {
        String sql = "SELECT fromLocation, toLocation FROM teleport_tasks WHERE taskID=?";
        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fromStr = rs.getString("fromLocation");
                    String toStr   = rs.getString("toLocation");

                    Location from = (fromStr != null && !fromStr.isEmpty())
                            ? GeneralMethods.stringToLocation(fromStr)
                            : null;
                    Location to   = (toStr   != null && !toStr.isEmpty())
                            ? GeneralMethods.stringToLocation(toStr)
                            : null;

                    TeleportTask ttask = new TeleportTask(taskID, areaID, priority, from, to);
                    createdTasks.put(taskID, ttask);

                    // Link to area
                    Area area = createdAreas.get(areaID);
                    if (area != null) {
                        area.addTask(ttask);

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

    // ------------------------------------------------------------------------
    // Linking
    // ------------------------------------------------------------------------

    /**
     * Link each Area with its parent Tutorial (based on tutorialID).
     */
    public static void linkAreasToTutorials() {
        for (Area area : createdAreas.values()) {
            Tutorial tut = createdTutorials.get(area.getTutorialID());
            if (tut != null) {
                tut.addArea(area);

                PlayerTutorials.getInstance().getLogger().log(
                        Level.INFO,
                        ChatUtil.translateString("&7Linked Area ID " + area.getAreaID()
                                + " to Tutorial ID " + tut.getId())
                );

            } else {

                PlayerTutorials.getInstance().getLogger().log(
                        Level.WARNING,
                        ChatUtil.translateString("&eTutorial ID " + area.getTutorialID()
                                + " not found for Area ID " + area.getAreaID())
                );

            }
        }
    }

    // ------------------------------------------------------------------------
    // Modify / Delete
    // ------------------------------------------------------------------------

    /**
     * Delete a Tutorial from DB & memory (cascade removes its Areas, etc.).
     */
    public static void deleteTutorial(Tutorial tutorial) {
        if (tutorial == null) return;
        String sql = "DELETE FROM tutorials WHERE id=?";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tutorial.getId());
            ps.executeUpdate();
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
     * Rename a Tutorial in DB & memory.
     */
    public static void renameTutorial(Tutorial tutorial, String newName) {
        if (tutorial == null) return;
        String sql = "UPDATE tutorials SET name=? WHERE id=?";

        try (Connection conn = storage.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
}
