package org.bendersdestiny.playertutorials.manager;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuiManager {

    public static void openGUI(Gui gui, int guiID, Player player) {
        if (MemoryUtil.getActiveInventories().get(player.getUniqueId()) != null) {
            MemoryUtil.getActiveInventories().get(player.getUniqueId()).get(guiID).show(player);
        } else {
            Map<Integer, Gui> guiMap = new ConcurrentHashMap<>();
            guiMap.put(guiID, gui);
            MemoryUtil.getActiveInventories().put(player.getUniqueId(), guiMap);
        }
    }

    public static void closeGUI(int guiID, Player player) {
        if (MemoryUtil.getActiveInventories().get(player.getUniqueId()) != null) {
            MemoryUtil.getActiveInventories().get(player.getUniqueId()).remove(guiID);
            player.closeInventory();
        }
    }
}
