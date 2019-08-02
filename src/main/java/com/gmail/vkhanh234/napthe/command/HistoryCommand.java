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
        addAliases("history","lichsu");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if(args.length>=1 && !args[0].matches("\\d+")){
            if (NapThe.getPlugin().getCommandManager().isSuperior(sender))
                NapThe.getPlugin().showHistory(sender, Bukkit.getOfflinePlayer(args[0]), args.length >= 2 ? Integer.valueOf(args[1]) : 1);
            else
                sender.sendMessage(NapThe.getPlugin().getMessage("noPerm"));
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
        if (NapThe.getPlugin().getCommandManager().isSuperior(sender))
            sender.sendMessage(ChatColor.GOLD + "/napthe lichsu [player] [page]" + ChatColor.RESET + " - xem lịch sử nạp thẻ của người chơi khác");
    }
}
