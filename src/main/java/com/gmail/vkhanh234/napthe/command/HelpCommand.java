package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class HelpCommand extends BaseCommand {

    public HelpCommand() {
        addAliases("help", "?");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        NapThe.getPlugin().getCommandManager().sendAllHelp(sender, label);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "/napthe help" + ChatColor.RESET + " - xem thông tin các lệnh");
    }
}
