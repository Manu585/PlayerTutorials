package org.bendersdestiny.playertutorials.commands;

import lombok.Getter;
import org.bendersdestiny.playertutorials.utils.chat.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public class TutorialCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.format("&cOnly a player can run this command!"));
        } else {
            if (player.hasPermission("playertutorials.tutorial.create")) {

            }
        }
        return false;
    }
}
