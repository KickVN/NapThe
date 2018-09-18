package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class SetCardCodeCommand extends BaseCommand{

    public SetCardCodeCommand(){
        addAliases("setcardcode");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        String id = args[0];
        int code = Integer.valueOf(args[1]);
        NapThe.getPlugin().setCardCode(sender,id,code);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD+"/napthe setcardcode <id> <code>"+ChatColor.RESET+" - thay đổi trạng thái của thẻ cào đã nạp");
    }
}
