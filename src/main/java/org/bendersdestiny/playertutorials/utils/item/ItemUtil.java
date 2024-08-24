package org.bendersdestiny.playertutorials.utils.item;

import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemUtil {
    public static @NotNull ItemStack getFillerItem() {
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();

        if (fillerMeta == null) throw new NullPointerException("FillerMeta is null!!");

        fillerMeta.setDisplayName(ChatUtil.format("&7"));
        fillerItem.setItemMeta(fillerMeta);
        return fillerItem;
    }
}
