package org.bendersdestiny.playertutorials.utils.chat.prompts;

import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TutorialNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return ChatUtil.format("&7Type the name of the &6tutorial &7or type '&6cancel&7' to cancel");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext conversationContext, @Nullable String s) {
        conversationContext.getAllSessionData().put("tutorialname", s);
        return null;
    }
}
