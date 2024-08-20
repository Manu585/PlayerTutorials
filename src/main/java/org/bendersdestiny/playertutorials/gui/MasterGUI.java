package org.bendersdestiny.playertutorials.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.item.ItemUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MasterGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final PaginatedPane paginatedPane; // TODO: Implement paginated feature

    private final List<GuiItem> tutorialItems = new ArrayList<>();

    private final int maxTutorialsPerPage = 24;

    public MasterGUI(int rows, String title) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 4, Pane.Priority.HIGH);
        this.paginatedPane = new PaginatedPane(0, 0,9, 3);

        this.fillTutorialItemList();
        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.fillWith(ItemUtil.getFillerItem());

        int counter = 0;
        for (GuiItem guiItem : this.tutorialItems) {
            this.pane.addItem(guiItem, Slot.fromIndex(counter));
            counter += 1;
        }

        // Remove bad looking Glass Pane
        for (int i = counter; i < 24; i++) {
            this.pane.removeItem(Slot.fromIndex(i));
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
}
