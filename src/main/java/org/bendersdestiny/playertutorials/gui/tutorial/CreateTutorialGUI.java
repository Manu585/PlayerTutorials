package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import lombok.Setter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.gui.util.SelectIconGUI;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.chat.prompts.TutorialNamePrompt;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.HumanEntity;
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

    @Setter
    private String tutorialTitle = "Tutorial";
    @Setter
    private Material tutorialIcon = Material.DIORITE;

    public CreateTutorialGUI() {
        this.gui = new ChestGui(1, ChatUtil.format("&aCreate Tutorial"), PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 1);

        if (MemoryUtil.guiCache.get(0) != null) {
            this.tutorialIcon = Material.valueOf(MemoryUtil.guiCache.get(0));
        }

        if (MemoryUtil.guiCache.get(1) != null) {
            this.tutorialTitle = MemoryUtil.guiCache.get(1);
        }

        this.setupUI();
        this.gui.setOnGlobalClick(e -> e.setCancelled(true));
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(1));
        this.pane.addItem(getChangeIconItem(), Slot.fromIndex(4));
        this.pane.addItem(getSaveItem(), Slot.fromIndex(7));
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeIconItem() {
        ItemStack changeIconItemStack = new ItemStack(this.tutorialIcon);
        ItemMeta changeIconItemMeta = changeIconItemStack.getItemMeta();

        if (changeIconItemMeta == null) throw new NullPointerException("ItemMeta cannot be null!");

        changeIconItemMeta.setDisplayName(ChatUtil.format("&3Change Icon"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatUtil.format("&7Change the &6Icon"));
        lore.add(ChatUtil.format("&7of the &6Tutorial"));

        changeIconItemMeta.setLore(lore);
        changeIconItemStack.setItemMeta(changeIconItemMeta);

        return new GuiItem(changeIconItemStack, event -> {
            HumanEntity whoClicked = event.getWhoClicked();
            if (whoClicked instanceof Player p) {
                new SelectIconGUI().getGui().show(p);
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeNameItem() {
        ItemStack changeNameItemStack = new ItemStack(Material.OAK_SIGN);
        ItemMeta changeNameItemMeta = changeNameItemStack.getItemMeta();

        if (changeNameItemMeta == null) throw new NullPointerException("ItemMeta cannot be null!");

        changeNameItemMeta.setDisplayName(ChatUtil.format("&eRename"));

        List<String> changeNameItemLore = new ArrayList<>();
        changeNameItemLore.add("");
        changeNameItemLore.add(ChatUtil.format("&6Left-Click&r &7to"));
        changeNameItemLore.add(ChatUtil.format("&7Change the &6Name"));
        changeNameItemLore.add(ChatUtil.format("&7of the &6Tutorial"));

        changeNameItemMeta.setLore(changeNameItemLore);
        changeNameItemStack.setItemMeta(changeNameItemMeta);

        return new GuiItem(changeNameItemStack, event -> {
            HumanEntity whoClicked = event.getWhoClicked();
            if (whoClicked instanceof Player p) {
                p.closeInventory();

                ConversationFactory factory = new ConversationFactory(PlayerTutorials.getInstance());
                Conversation conversation = factory.withFirstPrompt(new TutorialNamePrompt())
                        .withLocalEcho(false)
                        .withTimeout(60)
                        .buildConversation(p);

                // Pass the current GUI context into the conversation, so it can be updated
                conversation.getContext().setSessionData("gui", this);

                conversation.begin();
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getSaveItem() {
        ItemStack saveItemStack = new ItemStack(Material.GREEN_DYE);
        ItemMeta saveItemMeta = saveItemStack.getItemMeta();

        if (saveItemMeta == null) throw new NullPointerException("ItemMeta cannot be null!");

        saveItemMeta.setDisplayName(ChatUtil.format("&aSave"));

        List<String> saveItemLore = new ArrayList<>();
        saveItemLore.add("");
        saveItemLore.add(ChatUtil.format("&7Current Name: &6" + this.tutorialTitle));
        saveItemLore.add(ChatUtil.format("&7Current Icon: &6" + this.tutorialIcon.toString().toLowerCase()));

        saveItemMeta.setLore(saveItemLore);
        saveItemStack.setItemMeta(saveItemMeta);

        return new GuiItem(saveItemStack, event -> {
            if (this.tutorialTitle != null && this.tutorialIcon != null) {
                Tutorial newTutorial = new Tutorial(GeneralMethods.createRandomID(PlayerTutorials.getInstance().getStorage()), this.tutorialTitle, this.tutorialIcon);
                MemoryUtil.getCreatedTutorials().put(newTutorial.getId(), newTutorial);

                gui.getViewers().forEach(p -> p.sendMessage(ChatUtil.format("&7Successfully created a new &6tutorial &7with the &6name " + this.tutorialTitle + " &7and the ID &6" + newTutorial.getId())));

                MemoryUtil.saveTutorial(newTutorial);

                // RESET
                MemoryUtil.guiCache.clear();
                this.tutorialTitle = "Tutorial";
                this.tutorialIcon = Material.DIORITE;

                this.gui.update();

                event.getWhoClicked().closeInventory();

                ModifyTutorialGUI gui = new ModifyTutorialGUI(1, ChatUtil.format("&6Modify " + newTutorial.getName()), newTutorial);
                gui.getGui().show(event.getWhoClicked() instanceof Player p ? p : event.getWhoClicked());
            }
        });
    }
}
