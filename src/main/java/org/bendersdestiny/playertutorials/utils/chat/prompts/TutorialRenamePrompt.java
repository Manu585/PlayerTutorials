package org.bendersdestiny.playertutorials.utils.chat.prompts;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bendersdestiny.playertutorials.gui.tutorial.ModifyTutorialGUI;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TutorialRenamePrompt extends StringPrompt {
	private final Tutorial tutorial;

	public TutorialRenamePrompt(Tutorial tutorial) {
		this.tutorial = tutorial;
	}

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

		tutorial.setName(input.trim());
		MemoryUtil.renameTutorial(tutorial, input.trim());

		new ModifyTutorialGUI(this.tutorial).getGui().show((HumanEntity) context.getForWhom());

		return END_OF_CONVERSATION;
	}
}
