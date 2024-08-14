package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.items.BaseItem;
import org.bendersdestiny.playertutorials.items.tutorialmaker.AreaSelector;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

@Getter
public class ItemManager {
    private BaseItem areaSelector;

    public void initiateBaseItems() {
        FileConfiguration config = ConfigManager.languageConfig.get();
        String path = "playertutorials.items.";

        areaSelector = new AreaSelector(
                ChatUtil.format(config.getString(path + "areaselector.name")),
                config.getStringList(path + "areaselector.lore").stream()
                        .map(ChatUtil::format)
                        .collect(Collectors.toList()),
                Material.valueOf(config.getString(path + "material", "WOODEN_AXE")));
    }
}
