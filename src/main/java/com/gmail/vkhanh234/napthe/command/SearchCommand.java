package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class SearchCommand extends BaseCommand{

    public SearchCommand(){
        addAliases("timkiem");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        int start = args[0].startsWith("-")?0:1;
        String s = args[start];
        for(int i=start+1;i<args.length;i++) s+=" "+args[i];
        NapThe.getPlugin().showSearch(sender, s,start==1?1:Integer.valueOf(args[0]));
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe timkiem [page] [query]"+ChatColor.RESET+" - tìm card");
    }
}
