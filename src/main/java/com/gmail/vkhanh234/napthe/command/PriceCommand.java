package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class PriceCommand extends BaseCommand{

    public PriceCommand(){
        addAliases("price");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        NapThe.getPlugin().choosePrice((Player) sender,args[0]);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe price <giá_tiền>"+ChatColor.RESET+" - chọn giá tiền. Sử dụng sau khi chọn mạng.");
    }
}
