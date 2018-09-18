package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class HistoryCommand extends BaseCommand{

    public HistoryCommand(){
        addAliases("lichsu");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length>=1 && !args[0].matches("\\d+")){
            if(hasPermission(sender)) NapThe.getPlugin().showHistory(sender, Bukkit.getOfflinePlayer(args[0]), args.length >= 2 ? Integer.valueOf(args[1]):1);
        }
        else {
            int page = args.length >= 1 ? Integer.valueOf(args[0]) : 1;
            NapThe.getPlugin().showHistory(sender,(Player) sender, page);
        }
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe lichsu [page]"+ChatColor.RESET+" - xem lịch sử nạp thẻ và nhận thưởng");
        if(hasPermission(sender)) sender.sendMessage(ChatColor.GOLD+"/napthe lichsu [player]"+ChatColor.RESET+" - xem lịch sử nạp thẻ của người chơi khác");
    }

    private boolean hasPermission(CommandSender sender) {
        if(sender.isOp()) return true;
        if(sender.hasPermission(NapThe.getPlugin().getName()+".command.*")) return true;
        sender.sendMessage(NapThe.getPlugin().getMessage("noPerm"));
        return false;
    }
}
