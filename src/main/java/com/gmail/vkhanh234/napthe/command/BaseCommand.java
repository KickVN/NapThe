package com.gmail.vkhanh234.napthe.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KickVN on 18-Apr-18.
 */
public abstract class BaseCommand {
    public enum DisplayType {
        PERMISSION, ALWAYS_SHOW, HIDE_IGNORE_PERM;
    }
    List<String> aliases = new ArrayList<>();
    String label;
    DisplayType displayType = DisplayType.PERMISSION;
    boolean alwaysUsable;

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

    public boolean isAlwaysShow() {
        return displayType.equals(DisplayType.ALWAYS_SHOW);
    }

    public void setAlwaysShow() {
        this.displayType = DisplayType.ALWAYS_SHOW;
    }

    public boolean isAlwaysUsable() {
        return alwaysUsable;
    }

    public void setAlwaysUsable(boolean alwaysUsable) {
        this.alwaysUsable = alwaysUsable;
    }

    public boolean isHideIgnorePerm() {
        return displayType.equals(DisplayType.HIDE_IGNORE_PERM);
    }

    public void setHideIgnorePerm() {
        this.displayType = DisplayType.HIDE_IGNORE_PERM;
    }

    public abstract void sendHelp(CommandSender sender);

    public boolean hasPermission(CommandSender sender, String prefix) {
        for (String s : aliases) {
            if (sender.hasPermission(prefix + s)) return true;
        }
        return false;
    }
}
