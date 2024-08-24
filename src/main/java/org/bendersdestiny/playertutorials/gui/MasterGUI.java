package org.bendersdestiny.playertutorials.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.gui.tutorial.ModifyTutorialGUI;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.item.ItemUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MasterGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final PaginatedPane paginatedPane; // TODO: Implement paginated feature
    private final Player player;

    private final List<GuiItem> tutorialItems = new ArrayList<>();

    private final int maxTutorialsPerPage = 24;

    public MasterGUI(int rows, String title, Player player) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.player = player;
        this.pane = new StaticPane(0, 0, 9, 4, Pane.Priority.HIGH);
        this.paginatedPane = new PaginatedPane(0, 0,9, 3);

        this.fillTutorialItemList();
        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.paginatedPane.addPane(0, this.pane);
        this.pane.setVisible(true);

        // Fill whole inventory with glass panes
        this.pane.fillWith(ItemUtil.getFillerItem());

        // Add all tutorials to the gui
        int counter = 0;
        for (GuiItem guiItem : this.tutorialItems) {
            this.pane.addItem(guiItem, Slot.fromIndex(counter));
            counter += 1;
        }

        // Remove bad looking Glass Pane
        for (int i = counter; i < this.maxTutorialsPerPage; i++) {
            this.pane.removeItem(Slot.fromIndex(i));
        }

        // Open the ModifyTutorialGUI in case of tutorial item clicked
        for (GuiItem tutorialItem : this.tutorialItems) {
            tutorialItem.setAction(e -> {
                if (tutorialItem.getItem().getItemMeta() == null) throw new NullPointerException("ItemMeta cannot be null!");

                this.player.closeInventory();
                this.player.openInventory(new ModifyTutorialGUI(
                        3,
                        ChatUtil.format("&6Modify " + tutorialItem.getItem().getItemMeta().getDisplayName()),
                        MemoryUtil.getCreatedTutorials().get(tutorialItem.getItem().getItemMeta().getCustomModelData()))
                        .getGui().getInventory());
            });
        }

        // Add create tutorial item + functionality
        this.pane.addItem(getCreateTutorialItem(), Slot.fromIndex(31));

        getCreateTutorialItem().setAction(e -> {
            if (e.getClickedInventory() == this.gui.getInventory()) {
                this.player.closeInventory();
                this.player.openInventory(new CreateTutorialGUI( // Open the CreateTutorialGUI
                        4,
                        ChatUtil.format("&aCreate Tutorial"),
                        this.player).getGui().getInventory());
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getCreateTutorialItem() {
        ItemStack createTutorialItem = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta itemMeta = createTutorialItem.getItemMeta();

        if (itemMeta == null) throw new NullPointerException("ItemMeta cannot be null");

        itemMeta.setDisplayName(ChatUtil.format("&aCreate Tutorial"));
        createTutorialItem.setItemMeta(itemMeta);

        return new GuiItem(createTutorialItem);
    }

    private void fillTutorialItemList() {
        for (Tutorial tutorial : MemoryUtil.getCreatedTutorials().values()) {
            ItemStack tutorialItemStack = new ItemStack(tutorial.getIcon());
            ItemMeta tutorialItemMeta = tutorialItemStack.getItemMeta();

            if (tutorialItemMeta == null) throw new NullPointerException("ItemMeta cannot be null");

            tutorialItemMeta.setCustomModelData(tutorial.getId());
            tutorialItemStack.setItemMeta(tutorialItemMeta);

            this.tutorialItems.add(new GuiItem(tutorialItemStack));
        }
    }
}
