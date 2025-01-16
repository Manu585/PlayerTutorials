package org.bendersdestiny.playertutorials.utils.chat.prompts;

import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TutorialNamePrompt extends StringPrompt {
    private final CreateTutorialGUI parentGUI;

    public TutorialNamePrompt(CreateTutorialGUI parentGUI) {
        this.parentGUI = parentGUI;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatUtil.translateString("&#828282Type the name of the &#f0c435tutorial &#828282or type '&#f0c435cancel&#828282' to cancel.");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        Player player = (Player) context.getForWhom();
        if (input == null || input.trim().isEmpty()) {
            player.sendMessage(ChatUtil.translate("&#dc4848The tutorial name cannot be empty. Please try again."));
            return this;
        }

        if (input.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatUtil.translate("&#828282Tutorial renaming cancelled."));
            return END_OF_CONVERSATION;
        }

        updateGuiAndTitle(input.trim(), player);
        player.sendMessage(ChatUtil.translate("&#f0c435Tutorial name &#828282updated to " + input.trim()));

        return END_OF_CONVERSATION;
    }

    /**
     * Helper method because it looks ugly
     * @param input Tutorial Title
     * @param player Player
     */
    void updateGuiAndTitle(String input, Player player) {
        parentGUI.setTutorialTitle(input.trim());
        parentGUI.updateGUI();
        parentGUI.getGui().show(player);
    }
}
