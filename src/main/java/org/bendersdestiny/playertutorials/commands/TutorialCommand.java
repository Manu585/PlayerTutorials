package org.bendersdestiny.playertutorials.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import lombok.Getter;
import org.bendersdestiny.playertutorials.PlayerTutorials;
import org.bendersdestiny.playertutorials.gui.MasterGUI;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.chat.prompts.AreaNamePrompt;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.AdminTutorialPlayer;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
@Command(name = "tutorial", aliases = "t")
@Permission("playertutorials.tutorial")
public class TutorialCommand {

    @Execute
    void mainCommand(@Context Player sender) {
        new MasterGUI().getGui().show(sender);
    }

    @Execute(name = "pos1", aliases = "p1")
    void setPositionOne(@Context @NotNull Player sender) {
        AdminTutorialPlayer tutorialPlayer = (AdminTutorialPlayer) AdminTutorialPlayer.getPlayer(sender.getUniqueId());

        if (tutorialPlayer != null) {
            tutorialPlayer.setPos1(sender.getLocation());
            sender.sendMessage(ChatUtil.translate("&#828282Successfully set &#f0c435Pos1&#828282 for your area!"));
        }
    }

    @Execute(name = "pos2", aliases = "p2")
    void setPositionTwo(@Context @NotNull Player sender) {
        AdminTutorialPlayer tutorialPlayer = (AdminTutorialPlayer) AdminTutorialPlayer.getPlayer(sender.getUniqueId());

        if (tutorialPlayer != null) {
            tutorialPlayer.setPos2(sender.getLocation());
            sender.sendMessage(ChatUtil.translate("&#828282Successfully set &#f0c435Pos2&#828282 for your &#82c238area&#828282!"));
        }
    }

    @Execute(name = "create", aliases = "c")
    void createCommand(@Context Player sender) {
        new CreateTutorialGUI(null, null).getGui().show(sender);
    }

    @Execute(name = "areaaccept")
    void selectionToolCommand(@Context @NotNull Player sender) {
        AdminTutorialPlayer tp = (AdminTutorialPlayer) AdminTutorialPlayer.getPlayer(sender.getUniqueId());
        if (tp == null) return;

        int taskId = tp.getParticleTaskId();
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            tp.setParticleTaskId(-1);
        }

        ConversationFactory factory = new ConversationFactory(PlayerTutorials.getInstance());
        Conversation conversation = factory.withFirstPrompt(new AreaNamePrompt(tp))
                .withLocalEcho(false)
                .withTimeout(60)
                .buildConversation(sender);

        conversation.begin();
    }

    @Execute(name = "starttutorial")
    void startTutorial(@Context Player sender, @Arg int tutorialId) {
        Tutorial tutorial = MemoryUtil.getCreatedTutorials().get(tutorialId);
        if (tutorial == null) {
            sender.sendMessage(ChatUtil.translate("&#dc4848No tutorial found with ID &#f0c435" + tutorialId));
            return;
        }

        if (tutorial.getAreas().isEmpty()) {
            sender.sendMessage(ChatUtil.translate("&#dc4848Tutorial &#828282'" + tutorial.getName() + "&#828282' has no areas!"));
            return;
        }

        Area firstArea = tutorial.getAreas().getFirst();

        for (Area area : tutorial.getAreas()) {
            area.spawnArea(sender.getLocation());
        }

        if (firstArea.getSpawnPoint() != null) {
            sender.teleport(firstArea.getSpawnPoint());
            sender.sendMessage(ChatUtil.translate("&#f0c435Tutorial &#828282'" + tutorial.getName() + "&#828282' started!"));
        } else {
            sender.sendMessage(ChatUtil.translate("&#dc4848No spawnPoint set for area: " + firstArea.getName()));
        }
    }
}
