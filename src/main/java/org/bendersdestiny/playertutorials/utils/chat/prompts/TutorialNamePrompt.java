package org.bendersdestiny.playertutorials.utils.chat.prompts;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TutorialNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
        return LegacyComponentSerializer.legacySection().serialize(ChatUtil.translate("&#828282Type the name of the &#f0c435tutorial &#828282or type '&#f0c435cancel&#828282' to cancel."));
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

        UUID uuid = player.getUniqueId();
        Map<Integer, String> cache = MemoryUtil.getGuiCache().computeIfAbsent(uuid, k -> new HashMap<>());
        cache.put(1, input.trim());
        new CreateTutorialGUI(player).getGui().show(player);

        return END_OF_CONVERSATION;
    }
}
