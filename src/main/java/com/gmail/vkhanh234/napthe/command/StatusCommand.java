package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class StatusCommand extends BaseCommand{

    public StatusCommand(){
        addAliases("status");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        NapThe.getPlugin().showStatus(sender);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe status"+ChatColor.RESET+" - xem trạng thái của các nhà mạng");
    }
}
