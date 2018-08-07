package com.gmail.vkhanh234.napthe.hook;

import com.gmail.vkhanh234.napthe.NapThe;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class Placeholder extends EZPlaceholderHook {

    public Placeholder() {
        super(NapThe.getPlugin(),"napthe");
    }

    @Override
    public String onPlaceholderRequest(Player player, String iden) {
        String[] args = iden.split("_");
        if (args[0].equalsIgnoreCase("status")) {
            return NapThe.getPlugin().getStatusText(args[1]);
        }
        return null;
    }
}
