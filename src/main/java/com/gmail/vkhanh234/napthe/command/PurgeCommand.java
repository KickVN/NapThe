package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class PurgeCommand extends BaseCommand{

    public PurgeCommand(){
        addAliases("purge");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        long time = Long.parseLong(args[0])*1000;
        NapThe.getPlugin().purge(sender,time);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe purge [seconds]"+ChatColor.RESET+" - xóa dữ liệu đã tồn tại lâu hơn khoảng thời gian [seconds]");
    }
}
