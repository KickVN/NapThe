package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class InfoCommand extends BaseCommand{

    public InfoCommand(){
        addAliases("info");
        setIgnorePerm(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN+"Plugin "+ChatColor.AQUA+ChatColor.BOLD+"napthe "+ChatColor.GREEN
                +"phiên bản "+ChatColor.AQUA+ChatColor.BOLD+NapThe.getPlugin().getDescription().getVersion()+ChatColor.GREEN+" được làm bởi "+ChatColor.AQUA+ChatColor.BOLD+"KickVN");
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe info"+ChatColor.RESET+" - thông tin Plugin");
    }
}
