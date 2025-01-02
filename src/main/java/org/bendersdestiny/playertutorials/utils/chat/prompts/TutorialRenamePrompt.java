package org.bendersdestiny.playertutorials.utils.chat.prompts;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TutorialRenamePrompt extends StringPrompt {
	@Override
	public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
		return ChatUtil.format("&7Type the name of the &6tutorial &7or type '&6cancel&7' to cancel.");
	}

	@Override
	public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
		Player player = (Player) context.getForWhom();
		if (input == null || input.trim().isEmpty()) {
			player.sendMessage(Component.text("The tutorial name cannot be empty. Please try again.", TextColor.color(220, 72, 72)));
			return this;
		}

		if (input.equalsIgnoreCase("cancel")) {
			player.sendMessage(Component.text("Tutorial renaming cancelled.", TextColor.color(130, 130, 130)));
			return END_OF_CONVERSATION;
		}

		new CreateTutorialGUI().getGui().show((HumanEntity) context.getForWhom());

		return END_OF_CONVERSATION;
	}
}
