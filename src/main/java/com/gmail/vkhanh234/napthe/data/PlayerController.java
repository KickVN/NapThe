package com.gmail.vkhanh234.napthe.data;

import com.gmail.vkhanh234.napthe.NapThe;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerController {
    private HashMap<UUID, PlayerData> players = new HashMap<>();

    public void loadPlayer(OfflinePlayer p){
        PlayerData data = NapThe.getPlugin().getData().loadPlayer(p);
        data.name = p.getName();
        data.uuid = p.getUniqueId().toString();
        if(data==null) return;
        players.put(p.getUniqueId(),data);
    }

    public void unloadPlayer(Player p){
        players.remove(p.getUniqueId());
    }

    public PlayerData getPlayerData(Player p){
        return players.containsKey(p.getUniqueId())?players.get(p.getUniqueId()):null;
    }

    public void addCard(Player p, Card c) {
        if(!c.isAbleToSave()) return;
        PlayerData data = getPlayerData(p);
        if(data==null) return;
        if(!NapThe.getPlugin().getData().addCard(data,c)) return;
        data.addCard(c);
    }

    public void savePlayer(Player p){
        PlayerData data = getPlayerData(p);
        if (data == null) {
            System.out.println("Can't save player "+data.getName()+" data");
            return;
        }
        if(data.countCards() == 0) return;
        NapThe.getPlugin().getData().savePlayer(data);
    }

    public boolean updateCard(Card c) {
        if(!c.isAbleToSave()){
            c.remove=true;
            c.saved=false;
        }
        for(PlayerData data:players.values()){
            if(data.getCards().containsKey(c.id)){
                return updateCard(data,c);
            }
        }
        return NapThe.getPlugin().getData().updateCard(c);
    }

    public boolean updateCard(PlayerData data, Card c) {
        if(!c.isAbleToSave()){
            c.remove=true;
            c.saved=false;
        }
        data.getCards().remove(c.id);
        data.getCards().put(c.id,c);
        boolean b = NapThe.getPlugin().getData().updateCard(data,data.getCards().get(c.id));
        if(c.remove) data.getCards().remove(c.id);
        return b;
    }
    public boolean updateCard(Player p, Card c) {
        if(!players.containsKey(p.getUniqueId())) return false;
        return updateCard(players.get(p.getUniqueId()),c);
    }
}
