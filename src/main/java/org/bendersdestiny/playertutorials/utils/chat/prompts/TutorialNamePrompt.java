package org.bendersdestiny.playertutorials.utils.chat.prompts;

import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TutorialNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatUtil.format("&7Type the name of the &6tutorial &7or type '&6cancel&7' to cancel.");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        if (input == null || input.trim().isEmpty()) {
            context.getForWhom().sendRawMessage(ChatUtil.format("&cThe tutorial name cannot be empty. Please try again."));
            return this;
        }

        if (input.equalsIgnoreCase("cancel")) {
            context.getForWhom().sendRawMessage(ChatUtil.format("&7Tutorial renaming cancelled."));
            return END_OF_CONVERSATION;
        }

        MemoryUtil.guiCache.put(1, input.trim());
        new CreateTutorialGUI().getGui().show((HumanEntity) context.getForWhom());

        return END_OF_CONVERSATION;
    }
}
