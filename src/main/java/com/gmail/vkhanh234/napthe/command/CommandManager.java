package com.gmail.vkhanh234.napthe.command;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KickVN on 18-Apr-18.
 */
public class CommandManager {
    List<BaseCommand> commands = new ArrayList<>();
    public CommandManager(){
        loadCommands();
    }

    private void loadCommands() {
        commands.add(new ReloadCommand());
        commands.add(new StatusCommand());
        commands.add(new InfoCommand());
        commands.add(new ChooseCommand());
        commands.add(new PriceCommand());
        commands.add(new GiveCommand());
        if(NapThe.getPlugin().getData().isDetailAvailable()){
            commands.add(new TopCommand());
            commands.add(new HistoryCommand());
            commands.add(new StatsCommand());
            commands.add(new SearchCommand());
            commands.add(new PurgeCommand());
        }
    }

    public void onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length==0){
            sendAllHelp(sender,cmd.getName());
            return;
        }
        for(BaseCommand c:commands){
            if(!c.isRightLabel(cmd.getName())) continue;
            String t = args[0];
            if(!c.isRightCommand(t)) continue;
            if(!hasPerm(sender,c)) {
                sender.sendMessage(NapThe.getPlugin().getMessage("noPerm"));
                continue;
            }
            boolean b = c.onCommand(sender,fetchArgs(args));
            if(!b) sendCommandHelp(sender,c);
            return;
        }
    }

    private String[] fetchArgs(String[] args) {
        String[] ar = new String[args.length-1];
        for(int i=1;i<args.length;i++) ar[i-1]=args[i];
        return ar;
    }

    private void sendAllHelp(CommandSender sender, String label) {
        for(BaseCommand c:commands){
            if(!c.isRightLabel(label)) continue;
            if(!hasPerm(sender,c)) continue;
            sendCommandHelp(sender,c);
        }
    }

    private void sendCommandHelp(CommandSender sender, BaseCommand c){
        c.sendHelp(sender);
    }

    private boolean hasPerm(CommandSender sender, BaseCommand c) {
        if(c.getId()==null) return false;
        if(c.isIgnorePerm()) return true;
        return sender.hasPermission(NapThe.getPlugin().getName()+".command.*") || sender.hasPermission(NapThe.getPlugin().getName()+".admin") || sender.hasPermission(NapThe.getPlugin().getName()+".command."+c.getId());
    }
}
