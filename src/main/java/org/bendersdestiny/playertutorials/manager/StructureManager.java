package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.area.structure.StructureBlock;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StructureManager {
    public static void handleAreaSelection(Player player, Tutorial tutorial, String areaName, Location pos1, Location pos2, int priority) {
        Area area = MemoryUtil.createArea(tutorial.getId(), areaName, pos1, priority);
        if (area == null) {
            player.sendMessage(ChatUtil.translate("&cFailed to create area!"));
            return;
        }

        List<StructureBlock> blocks = scanBlocks(pos1, pos2);

        Structure structure = MemoryUtil.addStructureToArea(area.getAreaID(), blocks);
        if (structure == null) {
            player.sendMessage(ChatUtil.translate("&cFailed to create structure!"));
            return;
        }

        player.sendMessage(ChatUtil.translate("&aArea created with ID " + area.getAreaID() + " and structure ID " + structure.getStructureID()));

    }

    public static List<StructureBlock> scanBlocks(Location pos1, Location pos2) {
        List<StructureBlock> blocks = new ArrayList<>();

        World world = pos1.getWorld();
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(world, x, y, z);
                    Material material = loc.getBlock().getType();

                    int relativeX = x - minX;
                    int relativeY = y - minY;
                    int relativeZ = z - minZ;

                    if (material == Material.AIR) continue;

                    blocks.add(new StructureBlock(relativeX, relativeY, relativeZ, material));
                }
            }
        }
        return blocks;
    }
}
