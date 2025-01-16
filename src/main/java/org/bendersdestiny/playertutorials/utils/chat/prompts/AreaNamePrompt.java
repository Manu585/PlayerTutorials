package org.bendersdestiny.playertutorials.utils.chat.prompts;

import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.AdminTutorialPlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AreaNamePrompt extends StringPrompt {

    private final AdminTutorialPlayer adminTP;

    public AreaNamePrompt(AdminTutorialPlayer adminTP) {
        this.adminTP = adminTP;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return ChatUtil.translateString(
                "&#828282Type the name of the &#f0c435area &#828282or type '&#f0c435cancel&#828282' to cancel."
        );
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        Player player = (Player) context.getForWhom();

        if (input == null || input.trim().isEmpty()) {
            player.sendMessage(ChatUtil.translate("&#dc4848The area name cannot be empty. Please try again."));
            return this;
        }

        if (input.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatUtil.translate("&#828282Area creation cancelled."));
            return END_OF_CONVERSATION;
        }

        final String areaName = input.trim();

        player.sendMessage(ChatUtil.translate("&#f0c435Area name &#828282set to " + areaName));

        if (adminTP.getEditingTutorial() != null && adminTP.getPos1() != null && adminTP.getPos2() != null) {
            MemoryUtil.createArea(
                    adminTP.getEditingTutorial().getId(),
                    areaName,
                    adminTP.getPos1(),
                    adminTP.getPos2(),
                    1
            );
            player.sendMessage(ChatUtil.translate("&#828282Area '" + areaName + "&#828282' created successfully!"));
            adminTP.leaveAreaSelectionMode();
        } else {
            player.sendMessage(ChatUtil.translate("&#dc4848Could not create area (missing tutorial or positions)."));
            adminTP.leaveAreaSelectionMode();
        }

        return END_OF_CONVERSATION;
    }
}
