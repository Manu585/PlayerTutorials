package org.bendersdestiny.playertutorials.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import lombok.Getter;
import org.bendersdestiny.playertutorials.gui.MasterGUI;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.tutorial.Tutorial;
import org.bendersdestiny.playertutorials.tutorial.area.Area;
import org.bendersdestiny.playertutorials.tutorial.area.structure.Structure;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bendersdestiny.playertutorials.utils.memory.MemoryUtil;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@dev.rollczi.litecommands.annotations.command.Command(name = "tutorial", aliases = "t")
@Permission("playertutorials.tutorial")
public class TutorialCommand {

    @Execute
    void mainCommand(@Context Player sender) {
        new MasterGUI().getGui().show(sender);
    }

    @Execute(name = "create", aliases = "c")
    void createCommand(@Context Player sender) {
        new CreateTutorialGUI(sender).getGui().show(sender);
    }

    @Execute(name = "areaaccept")
    void selectionToolCommand(@Context Player sender) {
        TutorialPlayer tp = TutorialPlayer.getPlayer(sender.getUniqueId());
        if (tp == null) return;

        int taskId = tp.getParticleTaskId();
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            tp.setParticleTaskId(-1);
        }
        sender.sendMessage(ChatUtil.translate("Area accepted! Particles removed."));
    }

    @Execute(name = "starttutorial")
    void startTutorial(@Context Player sender, @Arg int tutorialId) {
        Tutorial tutorial = MemoryUtil.getCreatedTutorials().get(tutorialId);
        if (tutorial == null) {
            sender.sendMessage(ChatUtil.translate("&cNo tutorial found with ID " + tutorialId));
            return;
        }

        if (tutorial.getAreas().isEmpty()) {
            sender.sendMessage(ChatUtil.translate("&cTutorial '" + tutorial.getName() + "' has no areas!"));
            return;
        }
        Area firstArea = tutorial.getAreas().getFirst();

        Structure structure = MemoryUtil.loadStructure(firstArea.getAreaID());
        if (structure == null) {
            sender.sendMessage(ChatUtil.translate("&cNo structure or blocks found for area: " + firstArea.getName()));
            return;
        }

        structure.spawn(sender.getLocation().add(0, 3, 0));

        if (firstArea.getSpawnPoint() != null) {
            sender.teleport(firstArea.getSpawnPoint());
            sender.sendMessage(ChatUtil.translate("&aTutorial '" + tutorial.getName() + "' started!"));
        } else {
            sender.sendMessage(ChatUtil.translate("&cNo spawnPoint set for area: " + firstArea.getName()));
        }
    }
}
