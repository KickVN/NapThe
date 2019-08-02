package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class RedeemCommand extends BaseCommand {

    public RedeemCommand() {
        addAliases("redeem", "napthe", "nap", "start");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        NapThe.getPlugin().sendChooser((Player) sender);
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "/napthe nap" + ChatColor.RESET + " - nạp thẻ");
    }
}
