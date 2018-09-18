package com.gmail.vkhanh234.napthe.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KickVN on 18-Apr-18.
 */
public abstract class BaseCommand {
    List<String> aliases = new ArrayList<>();
    String label;
    boolean ignorePerm=false;
    public abstract boolean onCommand(CommandSender sender, String[] args);

    public List<String> getAliases() {
        return aliases;
    }

    public String getId() {
        return aliases.size()>0?aliases.get(0):null;
    }

    public void addAliases(String... args){
        for(String s:args) aliases.add(s.toLowerCase());
    }

    public boolean isRightLabel(String s){
        return label==null || label.equalsIgnoreCase(s);
    }

    public boolean isRightCommand(String s){
        return s!=null && (aliases.contains(s.toLowerCase()));
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isIgnorePerm() {
        return ignorePerm;
    }

    public void setIgnorePerm(boolean ignorePerm) {
        this.ignorePerm = ignorePerm;
    }

    public abstract void sendHelp(CommandSender sender);
}
