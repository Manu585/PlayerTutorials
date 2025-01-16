package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.gui.util.SelectIconGUI;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.chat.prompts.TutorialNamePrompt;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CreateTutorialGUI {
    private final ChestGui gui;
    private final StaticPane pane;

    @Setter
    private String tutorialTitle;
    @Setter
    private Material tutorialIcon;

    public CreateTutorialGUI(@Nullable String initialTitle, @Nullable Material initialIcon) {
        this.tutorialTitle = (initialTitle != null) ? initialTitle : "Tutorial";
        this.tutorialIcon = (initialIcon != null) ? initialIcon : Material.DIORITE;

        this.gui = new ChestGui(1, ChatUtil.translateString("&#54c72eCreate Tutorial"), PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 1, Pane.Priority.HIGH);

        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.gui.setOnGlobalClick(e -> e.setCancelled(true));

        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(1));
        this.pane.addItem(getChangeIconItem(), Slot.fromIndex(4));
        this.pane.addItem(getSaveItem(), Slot.fromIndex(7));
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeIconItem() {
        ItemStack stack = new ItemStack(this.tutorialIcon);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta null");

        meta.displayName(ChatUtil.translate("&#5287e3Change Icon"));
        List<Component> lore = new ArrayList<>();
        lore.add(ChatUtil.translate("&#828282Change the Icon of the Tutorial"));
        meta.lore(lore);
        stack.setItemMeta(meta);

        return new GuiItem(stack, event -> {
            if (event.getWhoClicked() instanceof Player p) {
                new SelectIconGUI(this).getGui().show(p);
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeNameItem() {
        ItemStack stack = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta null");

        meta.displayName(ChatUtil.translate("&#edd24cRename"));
        List<Component> lore = new ArrayList<>();
        lore.add(ChatUtil.translate("&#828282Left-Click to Change Name"));
        meta.lore(lore);

        stack.setItemMeta(meta);

        return new GuiItem(stack, event -> {
            if (event.getWhoClicked() instanceof Player p) {
                // Start a conversation to rename the tutorial
                p.closeInventory();
                ConversationFactory factory = new ConversationFactory(PlayerTutorials.getInstance());
                Conversation conversation = factory.withFirstPrompt(new TutorialNamePrompt(this))
                        .withLocalEcho(false)
                        .withTimeout(60)
                        .buildConversation(p);

                conversation.begin();
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getSaveItem() {
        ItemStack stack = new ItemStack(Material.GREEN_DYE);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta null");

        meta.displayName(ChatUtil.translate("&#40b825Save"));

        List<Component> lore = new ArrayList<>();
        lore.add(ChatUtil.translate("&#828282Current Name: " + this.tutorialTitle));
        lore.add(ChatUtil.translate("&#828282Current Icon: " + "&#f0c435" + this.tutorialIcon.toString()));
        meta.lore(lore);
        stack.setItemMeta(meta);

        return new GuiItem(stack, event -> {
            if (this.tutorialTitle != null && this.tutorialIcon != null) {
                Tutorial newTutorial = MemoryUtil.createTutorial(this.tutorialTitle, this.tutorialIcon);
                if (newTutorial == null) {
                    event.getWhoClicked().sendMessage(ChatUtil.translate("&#d24141Error creating tutorial!"));
                    return;
                }

                event.getWhoClicked().sendMessage(ChatUtil.translate("&#828282Created new Tutorial " +
                        this.tutorialTitle +
                        "&#828282 (ID: &#f0c435" +
                        newTutorial.getId() +
                        "&#828282)"));

                // Reset
                this.tutorialTitle = "Tutorial";
                this.tutorialIcon = Material.DIORITE;

                this.gui.update();
                event.getWhoClicked().closeInventory();

                // Show the Modify GUI
                ModifyTutorialGUI modify = new ModifyTutorialGUI(newTutorial);
                modify.getGui().show(event.getWhoClicked());
            }
        });
    }

    /**
     * Updates the tutorial name and icon in the GUI.
     */
    public void updateGUI() {
        this.pane.addItem(getSaveItem(), Slot.fromIndex(7));
        this.pane.addItem(getChangeIconItem(), Slot.fromIndex(4));
        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(1));
        this.gui.update();
    }
}
