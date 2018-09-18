package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class TopCommand extends BaseCommand{

    public TopCommand(){
        addAliases("top");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        int page = args.length>=1?Integer.valueOf(args[0]):1;
        long time = args.length>=2?Long.valueOf(args[1])*1000:System.currentTimeMillis();
        NapThe.getPlugin().showTop(sender,page,time);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe top [page] [seconds]"+ChatColor.RESET+" - xem Top nạp thẻ (trong khoảng thời gian)");
    }
}
