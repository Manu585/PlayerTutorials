package org.bendersdestiny.playertutorials.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Getter
public class MasterGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final Player player;

    private final List<GuiItem> tutorialItems = new ArrayList<>();

    private final int maxTutorials = 24;

    public MasterGUI(int rows, String title, Player player) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 4, Pane.Priority.HIGH);
        this.player = player;

        this.fillTutorialItemList();
        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.fillWith(this.getFillerItem());

        int counter = 0;
        for (GuiItem guiItem : this.tutorialItems) {
            this.pane.addItem(guiItem, Slot.fromIndex(counter));
            counter += 1;
        }

        this.pane.addItem(new GuiItem(new ItemStack(Material.GREEN_CONCRETE)), Slot.fromIndex(31));
    }

    private void fillTutorialItemList() {
        for (Tutorial tutorial : MemoryUtil.getCreatedTutorials().values()) {
            ItemStack tutorialItemStack = new ItemStack(tutorial.getIcon());
            ItemMeta tutorialItemMeta = tutorialItemStack.getItemMeta();

            if (tutorialItemMeta == null) throw new NullPointerException("ItemMeta cannot be null");

            tutorialItemMeta.setCustomModelData(tutorial.getId());
            tutorialItemStack.setItemMeta(tutorialItemMeta);

            tutorialItems.add(new GuiItem(tutorialItemStack));
        }
    }

    private @NotNull ItemStack getFillerItem() {
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        assert fillerMeta != null;
        fillerMeta.setDisplayName(ChatUtil.format("&7"));
        fillerItem.setItemMeta(fillerMeta);
        return fillerItem;
    }
}
