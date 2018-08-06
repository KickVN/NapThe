package com.gmail.vkhanh234.napthe;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MainListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();
        ChatStatus c = NapThe.getPlugin().getCard(p);
        if(c==null) return;
        e.setCancelled(true);
        if(e.getMessage().equals(NapThe.getPlugin().getMC().getCancelText())){
            e.getPlayer().sendMessage(NapThe.getPlugin().getMessage("cancelled"));
            c.stage = ChatStatus.Stage.DONE;
            NapThe.getPlugin().getCardMap().remove(p.getUniqueId());
            return;
        }
        if(!e.getMessage().matches(NapThe.getPlugin().getMC().getInputRegex())){
            e.getPlayer().sendMessage(NapThe.getPlugin().getMessage("wrongInput"));
            return;
        }
        if (c.stage == ChatStatus.Stage.SERI)
        {
            c.setSeri(e.getMessage());
            c.stage = ChatStatus.Stage.PIN;
            p.sendMessage(NapThe.getPlugin().getMessage("enteredSeri").replace("{value}",e.getMessage()));
            p.sendMessage(NapThe.getPlugin().getMessage("pin"));
        }
        else if (c.stage == ChatStatus.Stage.PIN)
        {
            c.setPin(e.getMessage());
            c.stage = ChatStatus.Stage.DONE;
            p.sendMessage(NapThe.getPlugin().getMessage("enteredPin").replace("{value}",e.getMessage()));
            NapThe.getPlugin().napThe(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Bukkit.getScheduler().runTaskAsynchronously(NapThe.getPlugin(), new Runnable() {
            @Override
            public void run() {
                NapThe.getPlugin().getPC().loadPlayer(e.getPlayer());
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Bukkit.getScheduler().runTaskAsynchronously(NapThe.getPlugin(), new Runnable() {
            @Override
            public void run() {
                NapThe.getPlugin().getPC().unloadPlayer(e.getPlayer());
            }
        });
    }
}
