package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class GiveCommand extends BaseCommand{

    public GiveCommand(){
        addAliases("give");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player p = Bukkit.getPlayer(args[0]);
        if(p!=null){
            NapThe.getPlugin().sendPrize(p,Integer.parseInt(args[1]));
        }
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe give <player> <tiền>"+ChatColor.RESET+" - trao phần thưởng cho người chơi");
    }
}
