package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.chat.prompts.TutorialNamePrompt;
import org.bendersdestiny.playertutorials.utils.item.ItemUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateTutorialGUI {
    private final ChestGui gui;
    private final StaticPane pane;
    private final Player player;

    @Setter
    private String tutorialTitle = "Tutorial";
    @Setter
    private Material tutorialIcon = Material.DIORITE;

    public CreateTutorialGUI(int rows, String title, Player player) {
        this.gui = new ChestGui(rows, title, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 1);
        this.player = player;

        this.setupUI();

        this.createChangeNameItemFunctionality();
        this.createChangeIconItemFunctionality();
        this.createSaveItemFunctionality();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.fillWith(ItemUtil.getFillerItem());

        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(1));
        this.pane.addItem(getChangeIconItem(), Slot.fromIndex(4));
        this.pane.addItem(getSaveItem(), Slot.fromIndex(7));
    }

    private void createChangeNameItemFunctionality() {
        getChangeNameItem().setAction(inventoryClickEvent -> {
            Conversation conversation = new Conversation(PlayerTutorials.getInstance(), this.player, new TutorialNamePrompt());
            conversation.begin();
            this.tutorialTitle = conversation.getContext().getAllSessionData().get("tutorialname").toString();
            if (this.tutorialTitle != null) {
                if (this.tutorialTitle.equalsIgnoreCase("cancel")) {
                    conversation.abandon();
                    this.player.openInventory(this.gui.getInventory());
                }
            }
        });
    }

    private void createChangeIconItemFunctionality() {
        getChangeIconItem().setAction(inventoryClickEvent -> {
            // new IconGUI() //TODO: Implement GUI
        });
    }

    private void createSaveItemFunctionality() {
        getSaveItem().setAction(inventoryClickEvent -> {
            if (this.tutorialTitle != null) {
                if (this.tutorialIcon != null) {
                    Tutorial newTutorial = new Tutorial(1, this.tutorialTitle, this.tutorialIcon); // Create new Tutorial
                    MemoryUtil.getCreatedTutorials().put(newTutorial.getId(), newTutorial);

                    this.tutorialTitle = "Tutorial";
                    this.tutorialIcon = Material.DIORITE;
                }
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeIconItem() {
        ItemStack changeIconItemStack = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta changeIconItemMeta = changeIconItemStack.getItemMeta();

        if (changeIconItemMeta == null) throw new NullPointerException("ItemMeta cannot be null!");

        changeIconItemMeta.setDisplayName(ChatUtil.format("&3Change Icon"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatUtil.format("&7Change the &6Icon"));
        lore.add(ChatUtil.format("&7of the &6Tutorial"));

        changeIconItemMeta.setLore(lore);
        changeIconItemStack.setItemMeta(changeIconItemMeta);

        return new GuiItem(changeIconItemStack);
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeNameItem() {
        ItemStack changeNameItemStack = new ItemStack(Material.OAK_SIGN);
        ItemMeta changeNameItemMeta = changeNameItemStack.getItemMeta();

        if (changeNameItemMeta == null) throw new NullPointerException("ItemMeta cannot be null!");

        changeNameItemMeta.setDisplayName(ChatUtil.format("&eRename"));

        List<String> changeNameItemLore = new ArrayList<>();
        changeNameItemLore.add("");
        changeNameItemLore.add(ChatUtil.format("&6Right-Click &7to"));
        changeNameItemLore.add(ChatUtil.format("&7Change the &6Name"));
        changeNameItemLore.add(ChatUtil.format("&7of the &6Tutorial"));

        changeNameItemMeta.setLore(changeNameItemLore);
        changeNameItemStack.setItemMeta(changeNameItemMeta);

        return new GuiItem(changeNameItemStack);
    }

    /**
     * Create and get the save {@link org.bendersdestiny.playertutorials.tutorial.Tutorial} item
     *
     * @return The save {@link org.bendersdestiny.playertutorials.tutorial.Tutorial} {@link GuiItem}
     */
    @Contract(" -> new")
    private @NotNull GuiItem getSaveItem() {
        ItemStack saveItemStack = new ItemStack(Material.GREEN_DYE);
        ItemMeta saveItemMeta = saveItemStack.getItemMeta();

        if (saveItemMeta == null) throw new NullPointerException("ItemMeta cannot be null!");

        saveItemMeta.setDisplayName(ChatUtil.format("&aSave"));

        List<String> saveItemLore = new ArrayList<>();
        saveItemLore.add("");
        saveItemLore.add(ChatUtil.format("&6Save &7 the &6Tutorial"));
        saveItemLore.add(ChatUtil.format("&7Current Name: &6" + this.tutorialTitle));
        saveItemLore.add(ChatUtil.format("&7Current Icon: &6" + this.tutorialIcon.toString()));

        saveItemMeta.setLore(saveItemLore);
        saveItemStack.setItemMeta(saveItemMeta);

        return new GuiItem(saveItemStack);
    }
}
