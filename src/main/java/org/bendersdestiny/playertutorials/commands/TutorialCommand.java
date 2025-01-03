package org.bendersdestiny.playertutorials.commands;

import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import lombok.Getter;
import org.bendersdestiny.playertutorials.gui.MasterGUI;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.methods.GeneralMethods;
import org.bendersdestiny.playertutorials.utils.memory.tutorialplayer.TutorialPlayer;
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

    @Execute(name = "selection")
    void selectionToolCommand(@Context Player sender) {
        GeneralMethods.joinAreaSelectionMode(TutorialPlayer.getPlayer(sender.getUniqueId()));
    }
}
