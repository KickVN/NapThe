package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class ReloadCommand extends BaseCommand{

    public ReloadCommand(){
        addAliases("reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        NapThe.getPlugin().reloadConfig();
        NapThe.getPlugin().initConfig();
        sender.sendMessage(NapThe.getPlugin().getMessage("successReload"));
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe reload"+ChatColor.RESET+" - reload config");
    }
}
