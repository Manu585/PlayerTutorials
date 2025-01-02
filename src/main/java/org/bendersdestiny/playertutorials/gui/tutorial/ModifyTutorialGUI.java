package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public class ModifyTutorialGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final Tutorial tutorial;

    public ModifyTutorialGUI(int rows, String title, Tutorial tutorial) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 1, Pane.Priority.HIGH);

        this.tutorial = tutorial;

        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(0));
        this.pane.addItem(getCreateAreaItem(), Slot.fromIndex(4));
        this.pane.addItem(getDeleteTutorialItem(), Slot.fromIndex(8));

        this.pane.setOnClick(event -> event.setCancelled(true));
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeNameItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(Component.text("Change Name", TextColor.color(240, 196, 53)));

        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            HumanEntity humanEntity = event.getWhoClicked();
            if (humanEntity instanceof Player p) {
                // Caches the tutorial for renaming
                if (this.tutorial != null) {
                    MemoryUtil.getModifyTutorialCache().put(0, this.tutorial);
                    // TODO: Close Inventory -> Start Conversation -> Update DB + Memory entry -> Open Tutorial again with new name
                }
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getDeleteTutorialItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(Component.text("Delete Tutorial", TextColor.color(220, 72, 72)));

        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            if (event.getWhoClicked() instanceof Player p) {
                if (this.tutorial != null) {
                    MemoryUtil.deleteTutorial(this.tutorial);
                    p.sendMessage(Component.textOfChildren(
                            Component.text("Tutorial", TextColor.color(130, 130, 130)),
                            Component.space(),
                            Component.text("'", TextColor.color(130, 130, 130)),
                            Component.text(this.tutorial.getName()),
                            Component.text("'", TextColor.color(130, 130, 130)),
                            Component.space(),
                            Component.text("has been", TextColor.color(130, 130, 130)),
                            Component.space(),
                            Component.text("deleted", TextColor.color(220, 72, 72))));
                    p.closeInventory();
                }
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getCreateAreaItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(Component.text("Create Area", TextColor.color(182, 61, 209)));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            if (!(event.getWhoClicked() instanceof Player p)) {
                return;
            }
            // TODO: Enter Area Selection Mode -> Select Pos1 + Pos2 -> Save Area -> Open Edit Area Menu
        });
    }
}

//                          POSSIBLE APPROACH FOR CREATING AN AREA
//            Structure structure = new Structure(0, 0, new File("TestStructure.schem"));
//
//            List<Task> tasks = new ArrayList<>();
//            tasks.add(new CommandTask(0, 0, 0, "TestCommand"));
//
//            Area newArea = MemoryUtil.createArea(
//                    this.tutorial.getId(),
//                    structure,
//                    "TestArea",
//                    new Location(Bukkit.getWorld("world"), 0, 64, 0),
//                    tasks,
//                    1
//            );
//
//            if (newArea == null) {
//                p.sendMessage(ChatUtil.format("&cError creating the new area!"));
//                return;
//            }
//
//            p.sendMessage(ChatUtil.format("&aSuccessfully created a new area with ID &e"
//                    + newArea.getAreaID() + "&a!"));