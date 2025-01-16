package org.bendersdestiny.playertutorials.gui.tutorial;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.gui.MasterGUI;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.chat.prompts.TutorialRenamePrompt;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.AdminTutorialPlayer;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
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

    public ModifyTutorialGUI(Tutorial tutorial) {
        this.gui = new ChestGui(2, ChatUtil.translateString("&#4a6ad2Modify " + tutorial.getName()), PlayerTutorials.getInstance());
        this.pane = new StaticPane(0, 0, 9, 2, Pane.Priority.HIGH);

        this.tutorial = tutorial;
        this.setupUI();
    }

    private void setupUI() {
        this.gui.addPane(this.pane);
        this.pane.setVisible(true);

        this.pane.addItem(getChangeNameItem(), Slot.fromIndex(0));
        this.pane.addItem(getCreateAreaItem(), Slot.fromIndex(4));
        this.pane.addItem(getDeleteTutorialItem(), Slot.fromIndex(8));
        this.pane.addItem(getBackItem(), Slot.fromIndex(13));

        this.pane.setOnClick(event -> event.setCancelled(true));
    }

    @Contract(" -> new")
    private @NotNull GuiItem getChangeNameItem() {
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(ChatUtil.translate("&#f0c435Change Name"));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            if (!(event.getWhoClicked() instanceof Player p)) return;

            p.closeInventory();

            ConversationFactory factory = new ConversationFactory(PlayerTutorials.getInstance());
            Conversation conversation = factory
                    .withFirstPrompt(new TutorialRenamePrompt(this.tutorial))
                    .withLocalEcho(false)
                    .withTimeout(60)
                    .buildConversation(p);

            conversation.begin();
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getDeleteTutorialItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(ChatUtil.translate("&#dc4848Delete Tutorial"));

        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            if (event.getWhoClicked() instanceof Player p) {
                if (this.tutorial != null) {
                    MemoryUtil.deleteTutorial(this.tutorial);
                    p.sendMessage(ChatUtil.translate("&#828282Tutorial '" +
                            this.tutorial.getName() + "&#828282'" + " has been" + " &#dc4848deleted"));

                    p.closeInventory();
                    new MasterGUI().getGui().show(p);
                }
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getCreateAreaItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(ChatUtil.translate("&#b43cd2Create Area"));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            if (!(event.getWhoClicked() instanceof Player p)) return;

            AdminTutorialPlayer tutorialPlayer = (AdminTutorialPlayer) AdminTutorialPlayer.getPlayer(p.getUniqueId());
            if (tutorialPlayer != null) {
                tutorialPlayer.setEditingTutorial(this.tutorial);
                tutorialPlayer.enterAreaSelectionMode();
                p.closeInventory();
                // TODO: Enter Area Selection Mode -> Select Pos1 + Pos2 -> Save Area -> Open Edit Area Menu
            }
        });
    }

    @Contract(" -> new")
    private @NotNull GuiItem getBackItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new NullPointerException("ItemMeta cannot be null!");

        meta.displayName(ChatUtil.translate("&#dc4848Back"));
        item.setItemMeta(meta);

        return new GuiItem(item, event -> {
            if (!(event.getWhoClicked() instanceof Player p)) {
                return;
            }
            p.closeInventory();
            new MasterGUI().getGui().show(p);
        });
    }
}
