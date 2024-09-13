package org.bendersdestiny.playertutorials.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class MasterGUI {
    private final ChestGui gui;
    private final StaticPane pane;

    private final int guiID = 0;

    private final List<GuiItem> tutorialItems = new ArrayList<>();

    private final int maxTutorialsPerPage = 24;

    private final Map<Integer, Gui> guiMap = new ConcurrentHashMap<>();


    public MasterGUI() {
        this.gui = new ChestGui(4, ChatUtil.format("&6Tutorials"), PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 4, Pane.Priority.HIGH);

        this.fillTutorialItemList();
        this.setupUI();
        this.gui.setOnGlobalClick(e -> e.setCancelled(true));
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        // Add all tutorials to the gui
        int counter = 0;
        for (GuiItem guiItem : this.tutorialItems) {
            this.pane.addItem(guiItem, Slot.fromIndex(counter));
            counter += 1;
        }

        // Fill whole inventory with glass panes
        this.pane.fillWith(ItemUtil.getFillerItem());

        // Remove bad looking Glass Pane
        for (int i = 27; i <= 35; i++) {
            this.pane.removeItem(Slot.fromIndex(i));
        }

        // Open the ModifyTutorialGUI in case of tutorial item clicked
        for (GuiItem tutorialItem : this.tutorialItems) {
            tutorialItem.setAction(e -> {
                if (tutorialItem.getItem().getItemMeta() == null) throw new NullPointerException("ItemMeta cannot be null!");

                HumanEntity whoClicked = e.getWhoClicked();
                if (whoClicked instanceof Player p) {
                    p.closeInventory();
                    ModifyTutorialGUI gui = new ModifyTutorialGUI(
                            1,
                            ChatUtil.format("&6Modify " + tutorialItem.getItem().getItemMeta().getDisplayName()),
                            MemoryUtil.getCreatedTutorials().get(tutorialItem.getItem().getItemMeta().getCustomModelData()));

                    gui.getGui().show(p);
                }
            });
        }
        this.pane.addItem(getCreateTutorialItem(), Slot.fromIndex(31));
    }

    @Contract(" -> new")
    private @NotNull GuiItem getCreateTutorialItem() {
        ItemStack createTutorialItem = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta itemMeta = createTutorialItem.getItemMeta();

        if (itemMeta == null) throw new NullPointerException("ItemMeta cannot be null");

        itemMeta.setDisplayName(ChatUtil.format("&aCreate Tutorial"));
        createTutorialItem.setItemMeta(itemMeta);

        return new GuiItem(createTutorialItem, event -> {
            HumanEntity whoClicked = event.getWhoClicked();
            if (whoClicked instanceof Player p) {
                p.closeInventory();
                CreateTutorialGUI gui = new CreateTutorialGUI();
                gui.getGui().show(p);
            }
        });
    }

    void fillTutorialItemList() {
        for (Tutorial tutorial : MemoryUtil.getCreatedTutorials().values()) {
            ItemStack tutorialItemStack = new ItemStack(tutorial.getIcon());
            ItemMeta tutorialItemMeta = tutorialItemStack.getItemMeta();

            if (tutorialItemMeta == null) throw new NullPointerException("ItemMeta cannot be null");

            tutorialItemMeta.setDisplayName(tutorial.getName());
            tutorialItemMeta.setCustomModelData(tutorial.getId());
            tutorialItemStack.setItemMeta(tutorialItemMeta);

            this.tutorialItems.add(new GuiItem(tutorialItemStack));
        }
    }
}
