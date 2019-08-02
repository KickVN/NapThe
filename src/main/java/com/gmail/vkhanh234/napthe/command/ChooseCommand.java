package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class ChooseCommand extends BaseCommand{

    public ChooseCommand(){
        addAliases("choose");
        setHideIgnorePerm();
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String id = args[0].toUpperCase();
        if (!NapThe.getPlugin().getType().getStatus().containsKey(id)){
            NapThe.getPlugin().showStatus(sender);
            return true;
        }
        NapThe.getPlugin().activeMode((Player) sender, id,args.length>=2?Boolean.parseBoolean(args[1]):true);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe choose <nhà_mạng> [true/false]"+ChatColor.RESET+" - chọn nhà mạng. True/false để tắt/bật hiển thị lựa chọn cho mệnh giá.");
    }
}
