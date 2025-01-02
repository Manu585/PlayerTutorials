package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class CreateTutorialGUI {
    private final ChestGui gui;
    private final StaticPane pane;

    @Setter
    private String tutorialTitle = "Tutorial"; // DEFAULT
    @Setter
    private Material tutorialIcon = Material.DIORITE; // DEFAULT

    public CreateTutorialGUI() {
        Map<Integer, String> cache = MemoryUtil.getGuiCache(); // Cache for Tutorial Name + Icon

        if (cache.containsKey(0)) {
            try {
                this.tutorialIcon = Material.valueOf(cache.get(0));
            } catch (Exception ex) {
                this.tutorialIcon = Material.DIORITE;
            }
        }
        if (cache.containsKey(1)) {
            this.tutorialTitle = cache.get(1);
        }

        Component titleComp = Component.text("Create Tutorial", TextColor.color(84, 199, 46));
        String legacyTitle = LegacyComponentSerializer.legacySection().serialize(titleComp);
        this.gui = new ChestGui(1, legacyTitle, PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 1);

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
        ItemStack stack = new ItemStack(this.tutorialIcon);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta null");

        meta.displayName(Component.text("Change Icon", TextColor.color(82, 135, 227)));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Change the Icon of the Tutorial", TextColor.color(130, 130, 130)));
        meta.lore(lore);
        stack.setItemMeta(meta);

        return new GuiItem(stack, event -> {
            if (event.getWhoClicked() instanceof Player p) {
                new SelectIconGUI().getGui().show(p);
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeNameItem() {
        ItemStack stack = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta null");

        meta.displayName(Component.text("Rename", TextColor.color(237, 210, 76)));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Left-Click to Change Name", TextColor.color(130, 130, 130)));
        meta.lore(lore);

        stack.setItemMeta(meta);

        return new GuiItem(stack, event -> {
            if (event.getWhoClicked() instanceof Player p) {
                // Start a conversation to rename the tutorial
                p.closeInventory();
                ConversationFactory factory = new ConversationFactory(PlayerTutorials.getInstance());
                Conversation conversation = factory.withFirstPrompt(new TutorialNamePrompt())
                        .withLocalEcho(false)
                        .withTimeout(60)
                        .buildConversation(p);

                conversation.getContext().setSessionData("gui", this);
                conversation.begin();
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getSaveItem() {
        ItemStack stack = new ItemStack(Material.GREEN_DYE);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta null");

        meta.displayName(Component.text("Save", TextColor.color(64, 184, 37)));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Current Name: ").color(TextColor.color(130, 130, 130)).append(Component.text(this.tutorialTitle)));
        lore.add(Component.text("Current Icon: ").color(TextColor.color(130, 130, 130)).append(Component.text(this.tutorialIcon.toString())));
        meta.lore(lore);
        stack.setItemMeta(meta);

        return new GuiItem(stack, event -> {
            if (this.tutorialTitle != null && this.tutorialIcon != null) {
                // Create the tutorial in DB
                Tutorial newTutorial = MemoryUtil.createTutorial(this.tutorialTitle, this.tutorialIcon);
                if (newTutorial == null) {
                    event.getWhoClicked().sendMessage(Component.text("Error creating tutorial!", TextColor.color(209, 65, 65)));
                    return;
                }

                event.getWhoClicked().sendMessage(
                        Component.textOfChildren(
                                Component.text("Created new tutorial", TextColor.color(130, 130, 130)),
                                Component.space(),
                                Component.text(this.tutorialTitle),
                                Component.text(" (ID: ", TextColor.color(130, 130, 130)),
                                Component.text(newTutorial.getId(), TextColor.color(240, 196, 53)),
                                Component.text(")", TextColor.color(130, 130, 130))
                        )
                );

                // Clear the cache + reset
                MemoryUtil.getGuiCache().clear();
                this.tutorialTitle = "Tutorial";
                this.tutorialIcon = Material.DIORITE;

                this.gui.update();
                event.getWhoClicked().closeInventory();

                // Show the Modify GUI
                ModifyTutorialGUI modify = new ModifyTutorialGUI(
                        1,
                        ChatUtil.format("&6Modify " + newTutorial.getName()),
                        newTutorial
                );
                modify.getGui().show(event.getWhoClicked());
            }
        });
    }
}
