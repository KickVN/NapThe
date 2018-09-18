package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class StatsCommand extends BaseCommand{

    public StatsCommand(){
        addAliases("thongke");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        final long time = args.length>=1?Long.valueOf(args[0])*1000:System.currentTimeMillis();
        Bukkit.getScheduler().runTaskAsynchronously(NapThe.getPlugin(), new Runnable() {
            @Override
            public void run() {
                sender.sendMessage(NapThe.getPlugin().getMessage("total").replace("{value}",NapThe.getPlugin().getData().getTotalAmount(time)+""));
            }
        });
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe thongke [seconds]"+ChatColor.RESET+" - thống kê tổng tiền (trong khoảng thời gian)");
    }
}
