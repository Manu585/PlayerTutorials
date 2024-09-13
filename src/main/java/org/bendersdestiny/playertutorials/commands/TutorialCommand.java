package org.bendersdestiny.playertutorials.commands;

import lombok.Getter;
import org.bendersdestiny.playertutorials.gui.MasterGUI;
import org.bendersdestiny.playertutorials.gui.tutorial.CreateTutorialGUI;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TutorialCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.format("&cOnly a player can run this command!"));
        } else {
            if (player.hasPermission("playertutorials.tutorial.create")) {
                if (label.equalsIgnoreCase("tutorial")) {
                    if (args.length == 0) {
                        MasterGUI gui = new MasterGUI();
                        gui.getGui().show(player);
                        return true;
                    }
                    if (args.length == 1) {
                        String arg = args[0];
                        if (arg.equalsIgnoreCase("create")) {
                            CreateTutorialGUI gui = new CreateTutorialGUI();
                            gui.getGui().show(player);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> arg = new ArrayList<>();
        if (args.length == 1) {
            arg.add("create");
        }
        return arg;
    }
}
