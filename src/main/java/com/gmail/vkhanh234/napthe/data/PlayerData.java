package com.gmail.vkhanh234.napthe.data;

import com.gmail.vkhanh234.napthe.gui.HistoryGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Created by KickVN on 15/2/2016.
 */
public class PlayerData {
    LinkedHashMap<String,Card> cards = new LinkedHashMap<>();
    String name,uuid;

    HistoryGui gui = null;
    public PlayerData(File configFile){
        this.uuid = configFile.getName().substring(0,configFile.getName().indexOf(".yml"));
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        loadFromFile(config);
    }
    public PlayerData(OfflinePlayer p){
        this.name = p.getName();
        this.uuid = p.getUniqueId().toString();
    }

    public void loadFromFile(FileConfiguration config){
        name = config.getString("name");
        ConfigurationSection cs = config.getConfigurationSection("cards");
        for(String k:cs.getKeys(false)){
            Card c = new Card(cs.getConfigurationSection(k));
            addCard(c);
        }
    }

    public void  addCard(Card c){
        cards.put(c.id,c);
    }

    public int countCards() {
        return cards.size();
    }

    public LinkedHashMap<String, Card> getCards() {
        return cards;
    }

    public int getSumAmount(long time) {
        int res=0;
        for(Card c:cards.values()){
            if(!c.isCorrect()) continue;
            if(time>0 && System.currentTimeMillis()-time>c.timestamp) continue;
            res+=c.amount;
        }
        return res;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uuid;
    }

    public void setGui(HistoryGui gui) {
        this.gui = gui;
    }

    public void removeGui() {
        if (gui != null) {
//            Bukkit.getPlayer(uuid).closeInventory();
            gui.clear();
            gui = null;
        }
    }

    public boolean click(ClickType type, int slot) {
        if (gui == null) return false;
        gui.click(type, slot);
        return true;
    }
}
