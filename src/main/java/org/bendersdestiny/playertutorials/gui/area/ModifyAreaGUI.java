package org.bendersdestiny.playertutorials.gui.area;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.task.Task;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ModifyAreaGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final Tutorial tutorial;

    public ModifyAreaGUI(int rows, String title, Tutorial tutorial) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 4, Pane.Priority.HIGH);
        this.tutorial = tutorial;

        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.addItem(this.changeTutorialNameItem(), Slot.fromIndex(31));

        int counter = 0;
        for (GuiItem task : this.allTaskItems()) {
            this.pane.addItem(task, Slot.fromIndex(counter));
            counter++;
        }

        for (int i = counter; i < 24; i++) {
            this.pane.removeItem(Slot.fromIndex(i));
        }
    }

    private @NotNull List<GuiItem> allTaskItems() {
        List<GuiItem> items = new ArrayList<>();
        for (Task task : MemoryUtil.getCreatedTutorials().get(tutorial.getId()).getAreas().iterator().next().getTasks()) {
            items.add(new GuiItem(task.getTaskItemStack()));
        }
        return items;
    }

    @Contract(" -> new")
    private @NotNull GuiItem changeTutorialNameItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) throw new NullPointerException("ItemMeta cannot be NULL!");

        meta.setDisplayName(tutorial.getName());
        item.setItemMeta(meta);

        return new GuiItem(item);
    }
}
