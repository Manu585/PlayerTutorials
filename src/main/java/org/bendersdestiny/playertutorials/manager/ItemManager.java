package org.bendersdestiny.playertutorials.manager;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bendersdestiny.playertutorials.configuration.ConfigManager;
import org.bendersdestiny.playertutorials.items.BaseItem;
import org.bendersdestiny.playertutorials.items.tutorialmaker.AreaSelector;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class ItemManager { //TODO: Make better!!
    private BaseItem areaSelector;

    public ItemManager() {
        this.initiateBaseItems();
    }

    private void initiateBaseItems() {
        FileConfiguration config = ConfigManager.languageConfig.get();
        String path = "playertutorials.items.";

        List<String> loreGatherer = config.getStringList(path + "areaselector.lore")
                .stream()
                .map(ChatUtil::format)
                .toList();

        List<Component> lore = new java.util.ArrayList<>(List.of());

        for (String s : loreGatherer) {
            lore.add(Component.text(s));
        }

        areaSelector = new AreaSelector(
                ChatUtil.format(config.getString(path + "areaselector.name")),
                lore,
                Material.valueOf(config.getString(path + "material", "WOODEN_AXE"))
        );
    }
}
