package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.tutorial.task.tasks.CommandTask;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ModifyTutorialGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final Tutorial tutorial;

    public ModifyTutorialGUI(int rows, String title, Tutorial tutorial) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0,0,9,1, Pane.Priority.HIGH);

        this.tutorial = tutorial;

        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(0));
        this.pane.addItem(getCreateAreaItem(), Slot.fromIndex(4));
        this.pane.addItem(getDeleteTutorialItem(), Slot.fromIndex(8));

        this.pane.setOnClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    }

    @Contract(" -> new")
    private GuiItem getChangeNameItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.setDisplayName(ChatUtil.format("&6Change Name"));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {}
        });
    }

    private GuiItem getDeleteTutorialItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.setDisplayName(ChatUtil.format("&cDelete Tutorial"));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {
                if (this.tutorial != null) {
                    MemoryUtil.deleteTutorial(this.tutorial);
                }
            }
        });
    }

    private GuiItem getCreateAreaItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.setDisplayName(ChatUtil.format("&dCreate Area"));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {
                List<Task> tasks = new ArrayList<>();
                tasks.add(new CommandTask(0,0,0,"Test"));
                Area area = new Area(GeneralMethods.createRandomAreaID(PlayerTutorials.getInstance().getStorage()), tutorial.getId(), new Structure(GeneralMethods.createRandomAreaID(PlayerTutorials.getInstance().getStorage()),0,new File("Test")), "Test", new Location(Bukkit.getWorld("world"), 0, 0, 0), tasks, 1);
                MemoryUtil.saveArea(area);
            }
        });
    }
}
