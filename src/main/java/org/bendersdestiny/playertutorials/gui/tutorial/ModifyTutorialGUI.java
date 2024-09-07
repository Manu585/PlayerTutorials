package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class ModifyTutorialGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final Tutorial tutorial;

    private GuiItem changeNameItem;
    private GuiItem deleteTutorialItem;
    private GuiItem createAreaItem;

    public ModifyTutorialGUI(int rows, String title, Tutorial tutorial) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0,0,9,3, Pane.Priority.HIGH);

        this.tutorial = tutorial;

        this.createNecessaryItems();
        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.addItem(changeNameItem, Slot.fromIndex(0));
        this.pane.addItem(createAreaItem, Slot.fromIndex(4));
        this.pane.addItem(deleteTutorialItem, Slot.fromIndex(8));
    }

    void createNecessaryItems() {
        this.createChangeNameItem();
        this.createCreateAreaItem();
        this.createDeleteTutorialItem();
    }

    private void createChangeNameItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.setDisplayName(ChatUtil.format("&6Change Name"));
        item.setItemMeta(meta);

        this.changeNameItem = new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {
            }
        });
    }

    private void createDeleteTutorialItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.setDisplayName(ChatUtil.format("&cDelete Tutorial"));
        item.setItemMeta(meta);

        this.deleteTutorialItem = new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {

            }
        });
    }

    private void createCreateAreaItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.setDisplayName(ChatUtil.format("&dCreate Area"));
        item.setItemMeta(meta);

        this.createAreaItem = new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {

            }
        });
    }
}
